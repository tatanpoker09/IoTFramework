package tatanpoker.com.frameworklib.framework.network.packets;

import android.annotation.SuppressLint;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class CallMethodPacket extends SimplePacket {
    private int id_from;
    private int id_to;
    private int methodID;

    private List<Object> parameters;

    public CallMethodPacket(int id_from, int id_to, int methodID) {
        this(id_from, id_to, methodID, new ArrayList<>());
    }

    public CallMethodPacket(int id_from, int id_to, int methodID, List<Object> parameters) {
        super(EncryptionType.AES);
        this.id_from = id_from;
        this.id_to = id_to;
        this.methodID = methodID;
        this.parameters = parameters;
    }


    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        process();
    }

    @Override
    public void process(String endpointId) {
        process();
    }

    private void process() {
        Framework.getLogger().info("Recieved CallMethodPacket!");
        //Depends if we're the "id_to", or not.
        if (Framework.getNetwork().getLocal().getId() == id_to) {
            //We've arrived!
            try {
                NetworkComponent component = Framework.getNetwork().getComponent(id_to);
                /*
                 *   TODO USE CUSTOM ANNOTATION PROCESSOR HERE TO AVOID USING REFLECTION.
                 */

                Framework.getLogger().info("CallMethodPacket arrived, " + component.getClass().getName() + "," + id_to + "," + methodID);
                Class[] types = new Class[parameters.size()];
                for (int i = 0; i < parameters.size(); i++) {
                    Object object = parameters.get(i);
                    types[i] = object.getClass();
                }
                Framework.getDeviceManager().callByID(methodID, (Object[]) parameters.toArray(new Object[0]));
            } catch (InvalidIDException e) {
                e.printStackTrace();
            }
        } else {
            Framework.getLogger().info("Redirecting CallMethodPacket!");
            try {
                NetworkComponent component = Framework.getNetwork().getComponent(id_to);
                Framework.getNetwork().sendPacket(component, this);//Resend to component.
            } catch (InvalidIDException e) {
                e.printStackTrace();
            }
        }
    }

    public int getIdFrom() {
        return id_from;
    }

    public int getIdTo() {
        return id_to;
    }

    public int getMethodID() {
        return methodID;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%d -> %d '%s': {%s}", id_from, id_to, methodID, parameters);
    }
}
