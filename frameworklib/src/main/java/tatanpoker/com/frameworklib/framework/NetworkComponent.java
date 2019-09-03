package tatanpoker.com.frameworklib.framework;

import android.content.Context;

import tatanpoker.com.frameworklib.framework.network.NearbyConnection;
import tatanpoker.com.frameworklib.framework.network.server.SocketServer;
import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public abstract class NetworkComponent implements Component, EventTrigger {

    private int id;
    private int layout;
    private ConnectionThread clientThread;

    private boolean connected;
    protected Context context;


    public NetworkComponent(int id, int layout, Context context) throws InvalidIDException {
        if(id==0 && !(this instanceof SocketServer)){
            throw new InvalidIDException("ID 0 is reserved for the SocketServer!");
        }
        this.id = id;
        this.layout = layout;
        this.context = context;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getLayout() {
        return layout;
    }

    @Override
    public void onEnable(){
        //Has to be overriden. This doesn't force the user to provide an implementation.
    }

    public boolean isLocal() {
        if(Framework.getNetwork()!=null) {
            return Framework.getNetwork().getId() == this.id;
        }
        return false;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public ConnectionThread getClientThread() {
        return clientThread;
    }

    public void setClientThread(ConnectionThread clientThread) {
        this.clientThread = clientThread;
    }

    public void onServerReady(){

    }

    @Override
    public String toString() {
        return getClass().getName();
    }
}
