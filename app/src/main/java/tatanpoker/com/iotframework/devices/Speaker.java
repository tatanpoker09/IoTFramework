package tatanpoker.com.iotframework.devices;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.iotframework.R;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.streaming.FileStream;
import tatanpoker.com.tree.annotations.Device;

import static tatanpoker.com.iotframework.devices.Speaker.SPEAKER_ID;

@Device(id = SPEAKER_ID, layout = R.layout.activity_speaker)
public class Speaker extends NetworkComponent {
    static final int SPEAKER_ID = 2;

    public Speaker(int id, int layout) {
        super(id, layout);
    }

    public void play(FileStream fileStream) {
        fileStream = Framework.getNetwork().getStreamingManager().getFileStream(fileStream.getUuid());//TODO MAKE THIS LINE AUTOMATIC LATER ON WITH CUSTOM ANNOTATION PROCESSOR.
        Framework.getLogger().info("Yay this works!");

        Activity activity = (Activity) Framework.getNetwork().getContext();
        activity.runOnUiThread(() -> {
            TextView textView = activity.findViewById(R.id.textView2);
            textView.setText("Current Situation: MY BALLS ARE NOT BEING CUT TODAY");
        });
        Player player = new Player(fileStream);
        new Thread(player).start();
    }
}


class Player implements Runnable{
    private InputStream inputStream;
    private int frequency = 44100;
    public boolean isPlaying = false;
    private Decoder mDecoder;
    private AudioTrack mAudioTrack;

    public Player(InputStream inputStream){
        this.inputStream = inputStream;
        /*try {
            this.inputStream = new URL("http://icecast.omroep.nl:80/radio1-sb-mp3")
                    .openConnection()
                    .getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void run() {
        try {
            decode(0,300000);
        } catch (IOException | DecoderException e) {
            e.printStackTrace();
        }
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
                for(; framesReaded++ <= READ_THRESHOLD && (header = bitstream.readFrame()) != null;) {
                    SampleBuffer sampleBuffer = (SampleBuffer) mDecoder.decodeFrame(header, bitstream);
                    short[] buffer = sampleBuffer.getBuffer();
                    System.out.println(buffer.length);
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

    public byte[] decode(int startMs, int maxMs)
            throws IOException, DecoderException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);

        float totalMs = 0;
        boolean seeking = true;
        InputStream inputStream = new BufferedInputStream(this.inputStream, 8 * 1024);
        try {
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();

            boolean done = false;
            while (! done) {
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) {
                    done = true;
                } else {
                    totalMs += frameHeader.ms_per_frame();

                    if (totalMs >= startMs) {
                        seeking = false;
                    }

                    if (! seeking) {
                        SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);

                        if (output.getSampleFrequency() != 44100
                                || output.getChannelCount() != 2) {
                            throw new DecoderException("mono or non-44100 MP3 not supported", null);
                        }

                        short[] pcm = output.getBuffer();
                        for (short s : pcm) {
                            outStream.write(s & 0xff);
                            outStream.write((s >> 8 ) & 0xff);
                        }
                    }

                    if (totalMs >= (startMs + maxMs)) {
                        done = true;
                    }
                }
                bitstream.closeFrame();
            }

            return outStream.toByteArray();
        } catch (BitstreamException e) {
            throw new IOException("Bitstream error: " + e);
        } catch (DecoderException e) {
            throw new DecoderException("Decoder error", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}