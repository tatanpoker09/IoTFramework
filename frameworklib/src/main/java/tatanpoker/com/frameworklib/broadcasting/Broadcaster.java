package tatanpoker.com.frameworklib.broadcasting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;


public class Broadcaster implements Runnable {
    private String address;
    /**
     * True if its a server. Then we want to broadcast its ip.
     */
    private boolean broadcast;
    private volatile boolean active;
    private final long period = 1000;
    private final int port = 55557;

    public Broadcaster(String address, boolean broadcast) {
        this.address = address;
        this.broadcast = broadcast;
        this.active = false;
    }

    public void broadcast(String localAddress) {
        this.active = true;
        try (DatagramSocket enviador = new DatagramSocket()) {
            enviador.setBroadcast(true);
            // El dato a enviar, como array de bytes.
            NetworkComponent local = Framework.getNetwork().getLocal();
            while (active) {
                BroadcastingPacket broadcastingPacket = new BroadcastingPacket(local.getId(), local.getClass().getSimpleName(), localAddress);

                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                ObjectOutput oo = new ObjectOutputStream(bStream);
                oo.writeObject(broadcastingPacket);


                byte[] serializedMessage = bStream.toByteArray();

                String ip = localAddress.substring(0, localAddress.lastIndexOf(".") + 1) + "255";

                DatagramPacket dgp = new DatagramPacket(serializedMessage, serializedMessage.length, InetAddress.getByName(ip), port);

                // envío del paquete
                enviador.send(dgp);
                Thread.sleep(period);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void recieve() {

        try {
            DatagramSocket escucha = new DatagramSocket(port);
            Framework.getLogger().info("Listening for servers on port: " + port);
            // Un array de bytes lo suficientemente grande para contener
            // cualquier dato que podamos recibir.
            byte[] dato = new byte[1024];
            DatagramPacket dgp = new DatagramPacket(dato, dato.length);
            escucha.receive(dgp);
            byte[] datos = dgp.getData();
            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(datos));
            BroadcastingPacket broadcastingPacket = (BroadcastingPacket) iStream.readObject();
            System.out.println("Received data: "+broadcastingPacket.getInetAddress());
            Framework.getNetwork().componentRecieved(broadcastingPacket);
            iStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parser() {

    }

    @Override
    public void run() {
        if (broadcast) {
            broadcast(address);
        } else {
            recieve();
        }
    }
}
