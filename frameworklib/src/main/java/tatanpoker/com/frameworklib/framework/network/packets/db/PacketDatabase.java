package tatanpoker.com.frameworklib.framework.network.packets.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PacketEntity.class}, version = 1, exportSchema = false)
public abstract class PacketDatabase extends RoomDatabase {
    public abstract PacketDao packetDao();
}