package tatanpoker.com.frameworklib.framework.network.streaming;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import tatanpoker.com.frameworklib.framework.network.packets.types.SubStreamPacket;

public class FileStream {
    private UUID uuid;

    private transient int currentPacket = 0;
    private transient List<SubStreamPacket> subPackets;

    public FileStream(){
        subPackets = new ArrayList<>();
    }

    public FileStream(UUID uuid){
        this.uuid = uuid;
    }

    public void addSubPacket(SubStreamPacket subpacket){
        subPackets.add(subpacket);
    }

    public SubStreamPacket getNextPacket(){
        return subPackets.get(currentPacket++);
    }

    public UUID getUuid() {
        return uuid;
    }
}
