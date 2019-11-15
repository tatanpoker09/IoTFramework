package tatanpoker.com.frameworklib.framework;

import java.security.PublicKey;

import javax.crypto.SecretKey;

import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.server.Server;

public abstract class NetworkComponent implements Component, EventTrigger {

    private int id;
    private int layout;
    private ConnectionThread clientThread;
    private String ipAddress;

    private PublicKey publicKey;
    private SecretKey symmetricKey;
    private TreeStatus treeStatus = TreeStatus.STARTING;
    private OnNodeConnectionListener connectionListener;


    public NetworkComponent(int id, int layout) throws InvalidIDException {
        if(id==0 && !(this instanceof Server)){
            throw new InvalidIDException("ID 0 is reserved for the SocketServer!");
        }
        this.id = id;
        this.layout = layout;
    }

    public void setPublicKey(PublicKey publicKey){
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public SecretKey getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(SecretKey symmetricKey) {
        this.symmetricKey = symmetricKey;
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
            return Framework.getNetwork().getLocal() == this;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void setOnNodeConnectionListener(OnNodeConnectionListener listener) {
        connectionListener = listener;
    }

    public OnNodeConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public TreeStatus getStatus() {
        return treeStatus;
    }
}
