package tatanpoker.com.frameworklib.framework;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import tatanpoker.com.frameworklib.framework.network.server.NearbyServer;
import tatanpoker.com.frameworklib.framework.network.server.Server;
import tatanpoker.com.frameworklib.framework.network.server.SocketServer;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;

/**
 * Represents the framework, which deals with automation and encapsulation, code generation, etc.
 */
public class Framework {
    public static final int CAMERA_ID = 1;
    public static final int ALARM_ID = 2;
    private static final String SERVICE_ID = "tatanpoker09.com.FrameworkIoT";
    private static ITree network; //This represents the IoT network. Singleton
    private static List<Pair<Class, Integer>> devices;

    private static Logger logger;

    public static final boolean NEARBY = true;

    public static void startNetwork(Context context, int id) {
        if(network == null){ //Singleton.
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
            network = new Tree(context, id, server);
        }
    }

    public static void networkEnable(){
        /*
        TODO THERE'S A BUG HERE. WHEN REGISTERING EVENTS COMPONENTS WILL NOT EXIST YET.
         */
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

    /*
    TODO FIX THE PAIR THING.
     */
    public static void registerComponents(Pair<Class,Integer>... components) {
        if (devices == null) {
            devices = new ArrayList<>();
        }
        devices.addAll(Arrays.asList(components));
    }

    public static List<Pair<Class,Integer>> getComponents() {
        return devices;
    }

    public static void registerComponent(Class<?> stubClass, int layout) {
        if (devices == null) {
            devices = new ArrayList<>();
        }
        devices.add(new Pair<>(stubClass, layout));
    }

    public static String getServiceID() {
        return SERVICE_ID;
    }

    public static ITree getNetwork() {
        return network;
    }
}
