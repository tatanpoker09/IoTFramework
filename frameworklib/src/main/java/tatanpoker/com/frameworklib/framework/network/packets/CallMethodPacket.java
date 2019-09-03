package tatanpoker.com.frameworklib.framework.network.packets;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class CallMethodPacket implements IPacket {
    private int id_from;
    private int id_to;
    private String method;

    private List<Object> parameters;

    public CallMethodPacket(int id_from, int id_to, String method){
        this(id_from, id_to,method, new ArrayList<>());
    }

    public CallMethodPacket(int id_from, int id_to, String method, List<Object> parameters){
        this.id_from = id_from;
        this.id_to = id_to;
        this.method = method;
        this.parameters = parameters;
    }

    @Deprecated
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_from", id_from);
            jsonObject.put("id_to", id_to);
            jsonObject.put("method", method);
            jsonObject.put("parameters", parameters.toArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void recieve(Socket socket, ConnectionThread clientThread) {
        recieve();
    }

    @Override
    public void recieve(String endpointId) {
        recieve();
    }

    private void recieve(){
        Framework.getLogger().info("Recieved CallMethodPacket!");
        //Depends if we're the "id_to", or not.
        if(Framework.getNetwork().getId() == id_to){
            //We've arrived!
            try {
                NetworkComponent component = Framework.getNetwork().getComponent(id_to);
                /* TODO
                //GET METHOD SIGNATURE FROM PARAMETERS MAPPING TO CLASS.
                //THEN I NEED TO GET THE PARAMETERS (THROW NON SERIALIZABLE IF FAILED).
                */

                Framework.getLogger().info("CallMethodPacket arrived, "+component.getClass().getName()+","+id_to+","+method);
                Class[] types = new Class[parameters.size()];
                for (int i = 0; i < parameters.size(); i++) {
                    Object object = parameters.get(i);
                    types[i] = object.getClass();
                }
                component.getClass().getMethod(method, types).invoke(component,parameters.toArray());
            } catch (InvalidIDException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Framework.getLogger().info("Redirecting CallMethodPacket!");
            try {
                NetworkComponent component = Framework.getNetwork().getComponent(id_to);
                component.getClientThread().sendPacket(this); //Resend to component.
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

    public String getMethod() {
        return method;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%d -> %d '%s': {%s}", id_from, id_to,method, parameters);
    }
}
