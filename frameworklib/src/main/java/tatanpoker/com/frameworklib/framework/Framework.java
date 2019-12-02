package tatanpoker.com.frameworklib.framework;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import java.util.logging.Logger;

import tatanpoker.com.frameworklib.framework.network.packets.db.PacketDatabase;

/**
 * Represents the framework, which deals with automation and encapsulation, code generation, etc.
 */
public class Framework {
    private static final String SERVICE_ID = "tatanpoker09.com.FrameworkIoT";
    private static Tree network; //This represents the IoT network. Singleton
    private static PacketDatabase db;
    private static Logger logger;
    private static TreeDeviceManager deviceManager;

    static final boolean NEARBY = false;

    public static void startNetwork(Context context) {
        if(network == null){ //Singleton.
            initializeDatabase(context);
            network = new Tree(context);
        }
    }


    private static void initializeDatabase(Context context) {
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
        network.setServer(build.server);
        deviceManager = build;
        return build;
    }

    @NonNull
    private static <T extends TreeDeviceManager> Tree.DevicesBuilder<T> getDevicesBuilder(
            @NonNull Class<T> deviceManager) {
        return new Tree.DevicesBuilder<>(deviceManager);
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

    public static TreeDeviceManager getDeviceManager() {
        return deviceManager;
    }
}
