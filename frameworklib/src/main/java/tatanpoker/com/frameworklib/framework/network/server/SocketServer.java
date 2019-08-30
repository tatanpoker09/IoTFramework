package tatanpoker.com.frameworklib.framework.network.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

import tatanpoker.com.frameworklib.components.Device;
import tatanpoker.com.frameworklib.events.server.DeviceConnectedEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.Tree;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.IPacket;

import static tatanpoker.com.frameworklib.framework.Tree.SERVER_IP;


/*
Based on https://examples.javacodegeeks.com/android/core/socket-core/android-socket-example/
 */
@Device(id=0)
public class SocketServer extends Server {
    private ServerThread serverThread = null;
    private static final boolean USE_USER_GIVEN_IP = true;


    public SocketServer() throws InvalidIDException {
        super(0, -10000);
    }


    /**
     * Sends a packet to all connected devices.
     * @param packet
     */
    public void sendPacket(IPacket packet) {
        for(NetworkComponent component : Framework.getNetwork().getComponents()){
            if(!(component instanceof SocketServer)) {
                component.getClientThread().sendPacket(packet);
            }
        }
    }

    public void closeServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getLocalIpAddress() throws Exception {
        String resultIpv6 = "";
        String resultIpv4 = "";

        for (Enumeration en = NetworkInterface.getNetworkInterfaces();
             en.hasMoreElements();) {

            NetworkInterface intf = (NetworkInterface) en.nextElement();
            for (Enumeration enumIpAddr = intf.getInetAddresses();
                 enumIpAddr.hasMoreElements();) {

                InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                if(!inetAddress.isLoopbackAddress()){
                    if (inetAddress instanceof Inet4Address) {
                        resultIpv4 = inetAddress.getHostAddress().toString();
                    } else if (inetAddress instanceof Inet6Address) {
                        resultIpv6 = inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        return ((resultIpv4.length() > 0) ? resultIpv4 : resultIpv6);
    }

    @Override
    protected void startServer() {
        this.serverThread = new SocketServer.ServerThread();
        this.serverThread.start();
    }

    class ServerThread extends Thread {
        private Socket socket;
        public synchronized void run() {
            try {
                InetAddress addr;
                if(USE_USER_GIVEN_IP){
                    addr = InetAddress.getByName(SERVER_IP);
                } else {
                    addr = InetAddress.getByName(getLocalIpAddress());
                }
                Framework.getLogger().info(String.format("Starting server on ip: %s:%s", addr,SERVERPORT));
                serverSocket = new ServerSocket(SERVERPORT,0,addr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Framework.getLogger().info("Accepting connections.");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    Framework.getNetwork().callEvent(new DeviceConnectedEvent(socket.getInetAddress()));
                    Framework.getLogger().info("Component connected with ip "+socket.getInetAddress());

                    ConnectionThread commThread = new ConnectionThread(socket);
                    new Thread(commThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public ServerThread getServerThread() {
        return serverThread;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

}
