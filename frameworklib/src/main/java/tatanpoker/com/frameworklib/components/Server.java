package tatanpoker.com.frameworklib.components;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;


/*
Based on https://examples.javacodegeeks.com/android/core/socket-core/android-socket-example/
 */
public class Server extends NetworkComponent {


    private ServerSocket serverSocket;

    private Thread serverThread = null;

    public static final int SERVERPORT = 12453;


    public Server() throws InvalidIDException {
        super(0, -10000);
    }
    @Override
    public void onEnable(){
        if(isLocal()) {
            this.serverThread = new Thread(new ServerThread());
            this.serverThread.start();
        }
    }

    public void closeServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket;
            try {
                Framework.getLogger().info("Starting server...");
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    System.out.println("Component connected with ip "+socket.getInetAddress());
                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class CommunicationThread implements Runnable {
        private Socket clientSocket;
        private BufferedReader input;
        CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String read = input.readLine();
                    //This is what we recieve.

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
