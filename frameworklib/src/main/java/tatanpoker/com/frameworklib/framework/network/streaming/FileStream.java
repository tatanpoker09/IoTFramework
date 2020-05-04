package tatanpoker.com.frameworklib.framework.network.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import tatanpoker.com.frameworklib.framework.network.packets.types.SubStreamPacket;

public class FileStream extends InputStream implements Serializable {
    private UUID uuid;

    private transient int currentPacketIndex = 0;
    private int totalPackets;
    private int iterations = 0;

    private transient List<SubStreamPacket> subPackets;

    public FileStream(){
        subPackets = new ArrayList<>();
    }

    private transient int currentDataPos = 0;
    private transient SubStreamPacket currentPacket;

    @Override
    public int read() throws IOException {
        try {
            if(currentPacket!=null) {
                byte[] data = currentPacket.getData();
                if (currentDataPos >= data.length) {
                    currentPacket = getNextPacket();
                    currentDataPos = 0;
                }
                return data[currentDataPos++];
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public FileStream(UUID uuid, int totalPackets){
        this.uuid = uuid;
        this.totalPackets = totalPackets;
        subPackets = new ArrayList<>();
    }

    public void addSubPacket(SubStreamPacket subpacket){
        subPackets.add(subpacket);
    }

    public SubStreamPacket getNextPacket() throws TimeoutException {
        try{
            SubStreamPacket subStreamPacket = subPackets.get(currentPacketIndex);
            currentPacketIndex++;
            iterations = 0;
            return subStreamPacket;
        } catch(IndexOutOfBoundsException e){
            if(currentPacketIndex<totalPackets){ //currentPacket is indexed by 0, so if they are equal we are not expecting another one.
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                iterations++;
                    if(iterations<30) {
                        return getNextPacket();
                    } else{
                        throw new TimeoutException("Didn't receive stream subpacket");
                    }
            }
            return null; //No more packets.
        }
    }

    public UUID getUuid() {
        return uuid;
    }
}
