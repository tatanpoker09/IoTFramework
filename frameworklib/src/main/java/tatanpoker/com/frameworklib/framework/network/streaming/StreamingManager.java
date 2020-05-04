package tatanpoker.com.frameworklib.framework.network.streaming;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StreamingManager {
    private List<FileStream> fileStreams;

    public StreamingManager(){
        fileStreams = new ArrayList<>();
    }

    public void addFileStream(FileStream fileStream){
        this.fileStreams.add(fileStream);
    }

    public FileStream getFileStream(@NonNull UUID uuid){
        for(FileStream fileStream : fileStreams){
            if(fileStream.getUuid().equals(uuid)){
                return fileStream;
            }
        }
        return null;
    }
}
