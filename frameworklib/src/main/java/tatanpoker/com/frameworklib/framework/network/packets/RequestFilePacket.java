package tatanpoker.com.frameworklib.framework.network.packets;

import java.io.File;
import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class RequestFilePacket extends SimplePacket {
    private String fileName;
    private int id_to;
    private int id_from;

    public RequestFilePacket() {
        super(EncryptionType.AES);
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        int local_id = Framework.getNetwork().getLocal().getId();
        if (local_id == id_to) {
            //We have to request here.
            File file = new File(fileName);
            if (file.exists()) {
                TransferFilePacket transferFilePacket = new TransferFilePacket(id_to, id_from, file);
                try {
                    Framework.getNetwork().sendPacket(id_from, transferFilePacket);
                } catch (InvalidIDException e) {
                    e.printStackTrace();
                }
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
