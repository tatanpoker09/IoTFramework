package tatanpoker.com.frameworklib.framework.network.packets;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "packets")
public class PacketEntity {
    /*
    Packet ID
     */
    @PrimaryKey
    public String pid;

    @ColumnInfo(name = "packetName")
    public String packetName;

    @ColumnInfo(name = "status")
    public boolean status; //This can be either false for not recieved, or true for recieved.

    public PacketEntity(String pid, String packetName, boolean status) {
        this.pid = pid;
        this.packetName = packetName;
        this.status = status;
    }

    public PacketEntity(String packetName) {
        this(UUID.randomUUID().toString(), packetName, false);
    }
}
