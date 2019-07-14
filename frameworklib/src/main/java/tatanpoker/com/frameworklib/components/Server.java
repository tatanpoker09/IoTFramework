package tatanpoker.com.frameworklib.components;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.Semaphore;

import tatanpoker.com.frameworklib.events.server.DeviceConnectedEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.IPacket;
import tatanpoker.com.frameworklib.framework.network.packets.ServerReadyPacket;


/*
Based on https://examples.javacodegeeks.com/android/core/socket-core/android-socket-example/
 */
@Device(id=0)
public class Server extends NetworkComponent {
    private ServerSocket serverSocket;
    private ServerThread serverThread = null;

    public static final int SERVERPORT = 6666;

    private Semaphore semaphore;
    public int devices;


    public Server() throws InvalidIDException {
        super(0, -10000);
    }

    @Override
    public void onEnable(){
        if(isLocal()) {
            this.serverThread = new ServerThread();
            this.serverThread.start();

            int componentCount = Framework.getComponents().size();
            semaphore = new Semaphore(0);

            Framework.getLogger().info(String.format("Starting Semaphore to wait for devices to connect... (0/%s)", componentCount));
            devices = 0;
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ServerReadyPacket serverReadyPacket = new ServerReadyPacket();
            sendPacket(serverReadyPacket);
        }
    }

    /**
     * Sends a packet to all connected devices.
     * @param packet
     */
    private void sendPacket(IPacket packet) {
        for(NetworkComponent component : Framework.getNetwork().getComponents()){
            if(!(component instanceof Server)) {
                try {
                    component.getClientThread().sendPacket(packet);
                } catch (IOException e) {
                    e.printStackTrace();
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

    class ServerThread extends Thread {
        private Socket socket;
        public synchronized void run() {
            try {
                InetAddress addr = InetAddress.getByName(getLocalIpAddress());
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

    public Semaphore getSemaphore() {
        return semaphore;
    }

}
