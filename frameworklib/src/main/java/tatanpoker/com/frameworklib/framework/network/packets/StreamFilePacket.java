package tatanpoker.com.frameworklib.framework.network.packets;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.UUID;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.StreamPacket;

public class StreamFilePacket extends StreamPacket {
    private String fileName;
    private Context context;

    public StreamFilePacket(String fileName, Context context, UUID uniqueStreamID) {
        super(EncryptionType.AES, uniqueStreamID);
        this.fileName = fileName;
        this.context = context;
    }

    @Override
    protected void process(String endpointId) {

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
    public long getSize() {
        AssetFileDescriptor fd = null;
        try {
            fd = context.getAssets().openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert fd != null;
        return fd.getLength();
    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        //We don't even receieve this full packet tbh.
    }
}
