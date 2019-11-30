package tatanpoker.com.frameworklib.framework.network.packets;

import java.io.File;
import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;


/*
TODO FINISH THIS PACKET. CREATE PACKET SENDING A LIST WITH EVERYTHING BACK.
 */
public class RequestFileListPacket extends SimplePacket {
    private int id_from;
    private int id_to;
    private String fileDir;

    public RequestFileListPacket(int id_from, int id_to, String fileDir) {
        super(EncryptionType.AES);
        this.id_from = id_from;
        this.id_to = id_to;
        this.fileDir = fileDir;
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        File directory = new File(fileDir);
        File[] files = directory.listFiles();
        ListFilesPacket listFilesPacket = new ListFilesPacket(id_to, id_from, files);
        try {
            Framework.getNetwork().sendPacket(id_from, listFilesPacket);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
    }
}