package tatanpoker.com.frameworklib.framework;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import tatanpoker.com.frameworklib.components.Alarm;
import tatanpoker.com.frameworklib.components.Server;
import tatanpoker.com.frameworklib.components.camera.Camera;
import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.network.ITree;
import tatanpoker.com.frameworklib.network.Tree;

/**
 * Represents the framework, which deals with automation and encapsulation, code generation, etc.
 */
public class Framework {
    private static ITree network; //This represents the IoT network. Singleton

    public static ITree getNetwork() {
        return network;
    }
    private static List<NetworkComponent> components;
    private static Logger logger;

    public static void startNetwork(int id, Activity mainActivity, Server server) throws InvalidIDException {
        if(network == null){ //Singleton.
            network = new Tree(id, mainActivity, server);
            network.onEnable();
        }
    }


    public static void registerComponents(NetworkComponent... components){
        /*TODO*/
        if(Framework.components == null)
            Framework.components = new ArrayList<>();
        Framework.components.addAll(Arrays.asList(components));
    }

    public static List<NetworkComponent> getComponents() {
        return components;
    }

    public static Logger getLogger() {
        if(logger == null){
            logger = Logger.getLogger("FrameworkLib");
        }
        return logger;
    }
}
