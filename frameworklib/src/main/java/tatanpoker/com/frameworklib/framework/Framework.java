package tatanpoker.com.frameworklib.framework;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import tatanpoker.com.frameworklib.components.Server;
import tatanpoker.com.frameworklib.components.camera.CameraStub;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.ITree;
import tatanpoker.com.frameworklib.framework.network.Tree;

/**
 * Represents the framework, which deals with automation and encapsulation, code generation, etc.
 */
public class Framework {
    public static final int CAMERA_ID = 1;
    public static final int ALARM_ID = 2;
    private static ITree network; //This represents the IoT network. Singleton
    private static List<Pair<Class, Integer>> devices;
    public static ITree getNetwork() {
        return network;
    }
    private static Logger logger;
    public static void startNetwork(int id) {
        if(network == null){ //Singleton.
            Server server = null;
            try {
                server = new Server();
            } catch (InvalidIDException e) {
                getLogger().severe("Error creating server");
                e.printStackTrace();
                return;
            }
            network = new Tree(id, server);
        }
    }

    public static void networkEnable(){
        for(NetworkComponent component : network.getComponents()){
            network.registerEvents(component);
        }
        network.onEnable();
        System.out.println("Finished onenable");
    }



    public static Logger getLogger() {
        if(logger == null){
            logger = Logger.getLogger("FrameworkLib");
        }
        return logger;
    }

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
}
