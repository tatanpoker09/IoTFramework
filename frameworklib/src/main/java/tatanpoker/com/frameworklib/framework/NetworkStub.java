package tatanpoker.com.frameworklib.framework;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import tatanpoker.com.frameworklib.components.Server;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;

import static tatanpoker.com.frameworklib.components.Server.SERVERPORT;

/**
 * Network reference to the component.
 */
public class NetworkStub extends NetworkComponent{
    private static String SERVER_IP = "172.0.0.1";
    private Socket socket;
    private Thread clientThread;

    public NetworkStub(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(this.isLocal()) { //Only connect if we're the same component.
            connect();
        }
    }

    /**
     * Connects to the server.
     */
    private void connect(){
        this.clientThread = new Thread(new ClientThread());
        this.clientThread.start();
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                Socket s1 = new Socket();
                s1.setSoTimeout(200);
                s1.connect(new InetSocketAddress(serverAddr,SERVERPORT), 200);
                Framework.getLogger().info("Connected to server on ip: "+serverAddr+":"+SERVERPORT);
            } catch (IOException e1) {
                Framework.getLogger().severe("FATAL ERROR, SERVER NOT FOUND.");
                e1.printStackTrace();
            }
        }

        public void send(JSONObject json){
            try (OutputStreamWriter out = new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8)) {
                out.write(json.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /*Work out post communication. Also get but that goes in the server.
    Also this has to auto generate every single method by the Network Component and overwrite it
    with a general post message in the CommunicationManager.
    */
}
