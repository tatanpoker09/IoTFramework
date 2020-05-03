package tatanpoker.com.frameworklib.framework.network.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import tatanpoker.com.frameworklib.framework.network.packets.types.SubStreamPacket;

public class FileStream {
    private UUID uuid;

    private transient int currentPacket = 0;
    private int totalPackets;
    private int iterations = 0;

    private transient List<SubStreamPacket> subPackets;

    public FileStream(){
        subPackets = new ArrayList<>();
    }

    public FileStream(UUID uuid, int totalPackets){
        this.uuid = uuid;
        this.totalPackets = totalPackets;
    }

    public void addSubPacket(SubStreamPacket subpacket){
        subPackets.add(subpacket);
    }

    public SubStreamPacket getNextPacket() throws TimeoutException {
        SubStreamPacket subStreamPacket = subPackets.get(currentPacket);
        if(subStreamPacket!=null){
            currentPacket++;
            iterations = 0;
            return subStreamPacket;
        } else {
            if(currentPacket<totalPackets){ //currentPacket is indexed by 0, so if they are equal we are not expecting another one.
                try {
                    Thread.sleep(20);
                    iterations++;
                    if(iterations<30) {
                        return getNextPacket();
                    } else{
                        throw new TimeoutException("Didn't receive stream subpacket");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null; //No more packets.
        }
    }

    public UUID getUuid() {
        return uuid;
    }
}
