package tatanpoker.com.frameworklib.framework;

import java.io.File;
import java.security.PublicKey;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import javax.crypto.SecretKey;

import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.streaming.FileStream;
import tatanpoker.com.frameworklib.framework.network.packets.RequestFileListPacket;
import tatanpoker.com.frameworklib.framework.network.packets.RequestFilePacket;
import tatanpoker.com.frameworklib.framework.network.packets.StreamFilePacket;

public abstract class NetworkComponent implements Component, EventTrigger {

    private int id;
    private int layout;
    private ConnectionThread clientThread;
    private String ipAddress;

    private PublicKey publicKey;
    private SecretKey symmetricKey;
    private TreeStatus treeStatus = TreeStatus.STARTING;
    private OnNodeConnectionListener connectionListener;
    private File[] files;
    private Semaphore semaphore;


    public NetworkComponent(int id, int layout) {
        /*if(id==0 && !(this instanceof Server)){
            throw new InvalidIDException("ID 0 is reserved for the SocketServer!");
        }*/
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

    public File[] listFiles(String directory) {
        if (isLocal()) {
            File directoryFile = new File(directory);
            return directoryFile.listFiles();
        } else {
            int localID = Framework.getNetwork().getLocal().getId();
            int to_id = getId();
            RequestFileListPacket fileListRequestPacket = new RequestFileListPacket(localID, to_id, directory);
            Framework.getNetwork().sendPacket(this, fileListRequestPacket);
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return files;
        }
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


    public Semaphore getSemaphore() {
        return semaphore;
    }

    public final void setFileList(File[] files) {
        this.files = files;
    }

    public File transferFile(String fileName, NetworkComponent to) {
        RequestFilePacket requestFilePacket = new RequestFilePacket(fileName, getId(), Framework.getNetwork().getLocal().getId(), to.getId());
        Framework.getNetwork().sendPacket(to, requestFilePacket);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new File(fileName);
    }



    public FileStream streamFile(String fileName, int componentToID) {
        NetworkComponent component = null;
        try {
            component = Framework.getNetwork().getComponent(componentToID);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
        if(component!=null) {
            UUID uniqueStreamID = UUID.randomUUID();
            NetworkComponent finalComponent = component;
            StreamFilePacket streamFilePacket = new StreamFilePacket(fileName, Framework.getNetwork().getContext(), uniqueStreamID);
            new Thread(() -> Framework.getNetwork().sendPacket(finalComponent, streamFilePacket)).start();
            return new FileStream(uniqueStreamID, streamFilePacket.getPacketCount());
        } else {
            return null;
        }
    }

    public void onDevicesRegistered() {

    }
}