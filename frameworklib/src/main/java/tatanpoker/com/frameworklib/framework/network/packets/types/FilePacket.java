package tatanpoker.com.frameworklib.framework.network.packets.types;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public abstract class FilePacket extends Packet {
    private File file;

    public FilePacket(EncryptionType encryptionType) {
        super(PacketType.FILE, encryptionType);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
