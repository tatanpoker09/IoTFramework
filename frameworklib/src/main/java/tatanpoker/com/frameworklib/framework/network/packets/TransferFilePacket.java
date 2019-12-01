package tatanpoker.com.frameworklib.framework.network.packets;

import java.io.File;
import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.FilePacket;

public class TransferFilePacket extends FilePacket {
    private final int id_initialrequest;
    private final int id_to;

    public TransferFilePacket(int id_initialrequest, int id_to, File file) {
        super(EncryptionType.AES, file);
        this.id_initialrequest = id_initialrequest;
        this.id_to = id_to;
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        int local_id = Framework.getNetwork().getLocal().getId();
        if (local_id == id_to) {
            //File arrived!
            ReleaseSemaphorePacket releaseSemaphorePacket = new ReleaseSemaphorePacket();
            try {
                Framework.getNetwork().sendPacket(id_initialrequest, releaseSemaphorePacket);
            } catch (InvalidIDException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Framework.getNetwork().sendPacket(id_to, this);
            } catch (InvalidIDException e) {
                e.printStackTrace();
            }
        }
    }
}