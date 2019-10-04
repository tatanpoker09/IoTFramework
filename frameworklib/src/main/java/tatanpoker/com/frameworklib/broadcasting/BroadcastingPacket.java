package tatanpoker.com.frameworklib.broadcasting;

import java.io.Serializable;

//This is a UDP Packet.
public class BroadcastingPacket implements Serializable {
    private int id;
    private String name;
    private String inetAddress;

    public BroadcastingPacket(int id, String name, String inetAddress) {
        this.id = id;
        this.name = name;
        this.inetAddress = inetAddress;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInetAddress() {
        return inetAddress;
    }

}
