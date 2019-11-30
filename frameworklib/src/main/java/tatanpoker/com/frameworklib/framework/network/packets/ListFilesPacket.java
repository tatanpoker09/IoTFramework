package tatanpoker.com.frameworklib.framework.network.packets;

import java.io.File;
import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;


public class ListFilesPacket extends SimplePacket {
    private final int id_from;
    private final File[] files;

    public ListFilesPacket(int id_from, File[] files) {
        super(EncryptionType.AES);
        this.id_from = id_from;
        this.files = files;
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        try {
            NetworkComponent senderComponent = Framework.getNetwork().getComponent(id_from);
            senderComponent.setFileList(this.files);
            senderComponent.getSemaphore().release();
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
    }
}
