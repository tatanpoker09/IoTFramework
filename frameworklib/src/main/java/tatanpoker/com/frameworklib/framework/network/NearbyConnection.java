package tatanpoker.com.frameworklib.framework.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.packets.IPacket;

public class NearbyConnection extends PayloadCallback {
    private Context context;

    public NearbyConnection(Context context){
        this.context = context;
    }

    @Override
    public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
        try {
            IPacket packet = (IPacket) deserialize(payload.asBytes());
            packet.recieve(endpointId);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(IPacket packet, String endpointId){
        Framework.getLogger().info("Sending packet: "+packet.getClass().getName()+" through Nearby.");
        try {
            Nearby.getConnectionsClient(context).sendPayload(endpointId, Payload.fromBytes(serialize(packet)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

    }

    /** Helper class to serialize and deserialize an Object to byte[] and vice-versa **/
    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // transform object to stream and then to a byte array
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }
}
