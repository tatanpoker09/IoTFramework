package tatanpoker.com.frameworklib.framework.network.packets;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.StreamPacket;

public class StreamFilePacket extends StreamPacket {


    private String fileName;
    private Context context;

    public StreamFilePacket(String fileName, Context context) {
        super(EncryptionType.AES);
        this.fileName = fileName;
        this.context = context;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        //We don't even receieve this full packet tbh.
    }
}
