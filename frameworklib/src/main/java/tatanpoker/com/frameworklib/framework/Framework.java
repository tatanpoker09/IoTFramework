package tatanpoker.com.frameworklib.framework;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.packets.PacketDatabase;
import tatanpoker.com.frameworklib.framework.network.server.NearbyServer;
import tatanpoker.com.frameworklib.framework.network.server.Server;
import tatanpoker.com.frameworklib.framework.network.server.SocketServer;

/**
 * Represents the framework, which deals with automation and encapsulation, code generation, etc.
 */
public class Framework {
    public static final int CAMERA_ID = 1;
    public static final int ALARM_ID = 2;
    private static final String SERVICE_ID = "tatanpoker09.com.FrameworkIoT";
    private static Tree network; //This represents the IoT network. Singleton
    private static PacketDatabase db;
    private static List<Pair<Class, Integer>> devices;

    private static Logger logger;

    public static final boolean NEARBY = false;

    public static void startNetwork(Context context) {
        if(network == null){ //Singleton.
            initalizeDatabase(context);
            Server server;
            try {
                if(NEARBY) {
                    server = new NearbyServer(context);
                } else {
                    server = new SocketServer();
                }
            } catch (InvalidIDException e) {
                getLogger().severe("Error creating socketServer");
                e.printStackTrace();
                return;
            }
            network = new Tree(context, server);
        }
    }

    private static void initalizeDatabase(Context context) {
        db = Room.databaseBuilder(context,
                PacketDatabase.class, "PacketPersistance").build();
    }

    public static void networkEnable(){
        for(NetworkComponent component : network.getComponents()){
            network.registerEvents(component);
        }
        network.onEnable();
        Framework.getLogger().info("Finished onenable");
    }



    public static Logger getLogger() {
        if(logger == null){
            logger = Logger.getLogger("FrameworkLib");
        }
        return logger;
    }

    public static <T extends TreeDeviceManager> T registerComponents(Class<T> deviceManagerClass) {
        T build = getDevicesBuilder(deviceManagerClass).build();
        if (build.local != null) {
            network.setLocal(build.local);
        } else {
            network.setLocal(network.getServer());
        }
        network.addComponents(build.devices);
        return build;
    }

    /*
    TODO FIX THE PAIR THING.
     */


    @NonNull
    private static <T extends TreeDeviceManager> Tree.DevicesBuilder<T> getDevicesBuilder(
            @NonNull Class<T> deviceManager) {
        if (devices == null) {
            devices = new ArrayList<>();
        }

        return new Tree.DevicesBuilder<>(deviceManager);
    }


    public static List<Pair<Class,Integer>> getComponents() {
        return devices;
    }


    public static String getServiceID() {
        return SERVICE_ID;
    }

    public static Tree getNetwork() {
        return network;
    }

    public static PacketDatabase getDatabase() {
        return db;
    }
}
