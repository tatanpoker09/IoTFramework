package tatanpoker.com.frameworklib.framework;

import android.content.Context;

import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.server.Server;

public abstract class NetworkComponent implements Component, EventTrigger {

    private int id;
    private int layout;
    private ConnectionThread clientThread;

    protected Context context;

    private TreeStatus treeStatus = TreeStatus.STARTING;


    public NetworkComponent(int id, int layout) throws InvalidIDException {
        if(id==0 && !(this instanceof Server)){
            throw new InvalidIDException("ID 0 is reserved for the SocketServer!");
        }
        this.id = id;
        this.layout = layout;
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

    public void setStatus(TreeStatus treeStatus) {
        this.treeStatus = treeStatus;
    }
}
