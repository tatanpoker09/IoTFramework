package tatanpoker.com.frameworklib.framework.network.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import tatanpoker.com.frameworklib.framework.network.packets.types.SubStreamPacket;

public class FileStream extends InputStream implements Serializable {
    private UUID uuid;

    private transient int currentPacketIndex = 0;
    private int totalPackets;
    private int iterations = 0;

    private transient List<SubStreamPacket> subPackets;
    private int markedPosition = -1;
    private int readlimit;

    public FileStream(){
        subPackets = new ArrayList<>();
    }

    private transient int currentDataPos = 0;
    private transient SubStreamPacket currentPacket;
    private transient Logger log;
    @Override
    public int read() throws IOException {
        if(readlimit--<=0){
            markedPosition = -1;
            //mark invalidated.
        }
        try {
            if(currentPacket == null){
                currentPacket = getNextPacket();
                currentDataPos = 0;
            }
            byte[] data = currentPacket.getData();
            if (currentDataPos >= data.length) {
                currentPacket = getNextPacket();
                currentDataPos = 0;
            }
            return data[currentDataPos++];
        } catch (TimeoutException e) {
            e.printStackTrace();
            log.info("Timeout happened.");
        }
        log.info("Returning -1");
        return -1;
    }

    public FileStream(UUID uuid, int totalPackets){
        this.uuid = uuid;
        this.totalPackets = totalPackets;
        subPackets = new ArrayList<>();
    }

    public void addSubPacket(SubStreamPacket subpacket){
        subPackets.add(subpacket);
        if(subPackets.size()==1 && currentPacket == null){
            currentPacket = subpacket;
        }
        /*System.out.println("New Packet: ");
        int i = 0;
        for(byte data : subpacket.getData()){
            System.out.print(data+",");
            if(i++==200) {
                System.out.println();
                i = 0;
            }
        }*/
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.readlimit = readlimit;
        markedPosition = currentDataPos;
    }

    @Override
    public synchronized void reset() throws IOException {
        if(markedPosition!=-1) {
            currentDataPos = markedPosition;
            markedPosition = -1;
        }
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
                c = read();
                b[off + i] = (byte)c;
            }
        } catch (IOException ee) {
        }
        return i;
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

    public SubStreamPacket getCurrentPacket() {
        return currentPacket;
    }
}
