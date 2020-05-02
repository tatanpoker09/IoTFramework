package tatanpoker.com.frameworklib.framework.network.packets.types;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.DataOutputStream;
import java.io.IOException;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;
import tatanpoker.com.frameworklib.security.EncryptionUtils;

public abstract class SimplePacket extends Packet {
    public SimplePacket(EncryptionType encryptionType) {
        super(PacketType.SIMPLE, encryptionType);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public final void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {

        byte[] data;
        Framework.getLogger().info("Sending packet: " + getClass().getName() + " through socket.");
        NetworkComponent component;
        try {
            component = Framework.getNetwork().getComponent(connectionThread);
        } catch (DeviceOfflineException e) {
            e.printStackTrace();
            return;
        }
        try {
            data = EncryptionUtils.encrypt(component, this, this.getEncryptionType());
            dataOutputStream.writeInt(data.length); //Write length
            dataOutputStream.writeInt(this.getEncryptionType().ordinal());
            dataOutputStream.writeInt(Framework.getNetwork().getLocal().getId());
            dataOutputStream.write(data); //Write data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
