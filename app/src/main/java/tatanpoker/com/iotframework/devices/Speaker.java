package tatanpoker.com.iotframework.devices;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.iotframework.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.streaming.FileStream;
import tatanpoker.com.tree.annotations.Device;

import static tatanpoker.com.iotframework.devices.Speaker.SPEAKER_ID;

@Device(id = SPEAKER_ID, layout = R.layout.microphone_layout)
public class Speaker extends NetworkComponent {
    static final int SPEAKER_ID = 2;

    public Speaker(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    public void play(FileStream fileStream) {
        fileStream = Framework.getNetwork().getStreamingManager().getFileStream(fileStream.getUuid());//TODO MAKE THIS LINE AUTOMATIC LATER ON WITH CUSTOM ANNOTATION PROCESSOR.
        Framework.getLogger().info("Yay this works!");
        Player player = new Player(fileStream);
        new Thread(player).start();
    }
}


class Player implements Runnable {
    private InputStream inputStream;
    private int frequency = 44100;
    public boolean isPlaying = false;
    private Decoder mDecoder;
    private AudioTrack mAudioTrack;
    private int bufferSize = 4096;
    private byte[] data;
    private FileStream fileStream;

    public Player(FileStream fileStream) {
        this.fileStream = fileStream;
        data = new byte[bufferSize];
        this.inputStream = new ByteArrayInputStream(data);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void run() {
        play();
    }

    public void write(byte[] array){

    }

    private void play() {
        isPlaying = true;


        final int sampleRate = 44100;
        final int minBufferSize = AudioTrack.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM);

        mDecoder = new Decoder();
        Thread thread = new Thread(() -> {
            isPlaying = true;
            try {
                InputStream in = Player.this.inputStream;
                Bitstream bitstream = new Bitstream(in);

                final int READ_THRESHOLD = 2147483647;
                int framesReaded = 0;

                Header header;
                for (; framesReaded++ <= READ_THRESHOLD && (header = bitstream.readFrame()) != null; ) {
                    SampleBuffer sampleBuffer = (SampleBuffer) mDecoder.decodeFrame(header, bitstream);
                    short[] buffer = sampleBuffer.getBuffer();
                    mAudioTrack.write(buffer, 0, buffer.length);
                    bitstream.closeFrame();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            isPlaying = false;
        });
        thread.start();

        mAudioTrack.play();
    }
}
