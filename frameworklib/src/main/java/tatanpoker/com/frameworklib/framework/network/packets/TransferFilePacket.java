package tatanpoker.com.frameworklib.framework.network.packets;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.FilePacket;

public class TransferFilePacket extends FilePacket {
    private final int id_from;
    private final int id_to;
    private final File file;

    public TransferFilePacket(int id_from, int id_to, File file) {
        super(EncryptionType.AES);
        this.id_from = id_from;
        this.id_to = id_to;
        this.file = file;
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        int local_id = Framework.getNetwork().getLocal().getId();
        if (local_id == id_to) {
            //File arrived!
            Semaphore semaphore = Framework.getNetwork().getLocal().getSemaphore();
            semaphore.release();
            try {
                boolean newFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Framework.getNetwork().getLocal().getSemaphore().release();
        } else {
            try {
                Framework.getNetwork().getComponent(id_to).getClientThread().sendPacket(this);
            } catch (DeviceOfflineException e) {
                e.printStackTrace();
            } catch (InvalidIDException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {
        super.sendPacket(dataOutputStream, connectionThread);
    }
}