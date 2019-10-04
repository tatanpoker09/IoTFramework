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


public class Broadcaster {
    public void broadcast(InetAddress localAddress) {
        try (DatagramSocket enviador = new DatagramSocket()) {
            enviador.setBroadcast(true);
            // El dato a enviar, como array de bytes.
            NetworkComponent local = Framework.getNetwork().getLocal();

            BroadcastingPacket broadcastingPacket = new BroadcastingPacket(local.getId(), local.getClass().getSimpleName(), localAddress.toString());

            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(broadcastingPacket);
            oo.close();

            byte[] serializedMessage = bStream.toByteArray();

            String ip = localAddress.toString().substring(0, localAddress.toString().lastIndexOf(".") + 1) + "255";

            DatagramPacket dgp = new DatagramPacket(serializedMessage, serializedMessage.length, InetAddress.getByName(ip), 55557);

            // envío del paquete
            enviador.send(dgp);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recieve() {
        try {
            DatagramSocket escucha = new DatagramSocket(55557);
            // Un array de bytes lo suficientemente grande para contener
            // cualquier dato que podamos recibir.
            byte[] dato = new byte[1024];

            DatagramPacket dgp = new DatagramPacket(dato, dato.length);
            escucha.receive(dgp);
            byte[] datos = dgp.getData();
            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(datos));
            BroadcastingPacket broadcastingPacket = (BroadcastingPacket) iStream.readObject();
            Framework.getNetwork().componentRecieved(broadcastingPacket);
            iStream.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parser() {

    }
}
