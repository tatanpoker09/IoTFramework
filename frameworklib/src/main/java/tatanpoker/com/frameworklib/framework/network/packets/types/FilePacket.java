package tatanpoker.com.frameworklib.framework.network.packets.types;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.annotation.CallSuper;
import androidx.annotation.RequiresApi;

import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;
import tatanpoker.com.frameworklib.security.EncryptionUtils;


public abstract class FilePacket extends Packet {
    protected File file;

    public FilePacket(EncryptionType encryptionType, File file) {
        super(PacketType.FILE, encryptionType);
        this.file = file;
    }

    @CallSuper
    @Override
    public void preprocess(DataInputStream dataInputStream) {
        try {
            int fileLength = dataInputStream.readInt();
            byte[] fileData = new byte[fileLength];
            dataInputStream.readFully(fileData, 0, fileData.length); // read the message
            FileUtils.writeByteArrayToFile(file, fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
            NetworkComponent component = Framework.getNetwork().getComponent(connectionThread);
            byte[] encryptedFile = EncryptionUtils.encrypt(component, fileData, getEncryptionType());
            if (encryptedFile != null) {
                dataOutputStream.write(encryptedFile.length);
                dataOutputStream.write(encryptedFile); //Write data
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
    }
}
