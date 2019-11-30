package tatanpoker.com.frameworklib.framework.network.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import tatanpoker.com.frameworklib.events.server.DeviceConnectedEvent;
import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;

import static tatanpoker.com.frameworklib.framework.Tree.SERVER_IP;


/*
Based on https://examples.javacodegeeks.com/android/core/socket-core/android-socket-example/
 */
//@Device(id = 0, stub = false, layout = -10000) not necessary.
public class SocketServer extends Server {
    private ServerSocket serverSocket;
    private ServerThread serverThread = null;
    private static final boolean USE_USER_GIVEN_IP = false;


    public SocketServer() throws InvalidIDException {
        super(0, -10000);
    }


    /**
     * Sends a packet to all connected devices.
     * @param packet
     */
    public void sendPacket(Packet packet) {
        for(NetworkComponent component : Framework.getNetwork().getComponents()){
            if(!(component instanceof SocketServer)) {
                if (component.getStatus() == TreeStatus.ONLINE && component.getClientThread() != null) { //We make sure we're online.
                    try {
                        component.getClientThread().sendPacket(packet);
                    } catch (DeviceOfflineException e) {
                        e.printStackTrace();
                    }
                }
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

    public String getLocalIpAddress() throws SocketException {
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
                        resultIpv4 = inetAddress.getHostAddress();
                    } else if (inetAddress instanceof Inet6Address) {
                        resultIpv6 = inetAddress.getHostAddress();
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
                if (USE_USER_GIVEN_IP) {
                    addr = InetAddress.getByName(SERVER_IP);
                } else {
                    addr = InetAddress.getByName(getLocalIpAddress());
                }
                Framework.getLogger().info(String.format("Starting server on ip: %s:%s", addr, SERVERPORT));
                serverSocket = new ServerSocket(SERVERPORT, 0, addr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Framework.getLogger().info("Accepting connections.");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    Framework.getNetwork().callEvent(new DeviceConnectedEvent(socket.getInetAddress()));

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
