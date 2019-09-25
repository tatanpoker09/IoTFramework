package tatanpoker.com.frameworklib.framework.network.packets;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface PacketDao {
    @Query("SELECT * FROM packets")
    List<PacketEntity> getAll();


    @Query("SELECT * FROM packets where status=0")
    List<PacketEntity> getNonProcessed();

    @Query("SELECT * FROM packets where status=1")
    PacketEntity getProcessed();


    @Query("SELECT * FROM packets where pid=:uuid")
    List<PacketEntity> getByUUID(String uuid);


    @Insert
    void insert(PacketEntity packet);

    @Delete
    void delete(PacketEntity packet);
}
