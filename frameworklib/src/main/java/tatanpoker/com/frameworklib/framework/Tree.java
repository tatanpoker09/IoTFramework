package tatanpoker.com.frameworklib.framework;

import android.content.Context;
import android.util.Pair;
import android.util.SparseArray;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import tatanpoker.com.frameworklib.components.Device;
import tatanpoker.com.frameworklib.framework.network.client.NearbyClient;
import tatanpoker.com.frameworklib.framework.network.client.SocketClient;
import tatanpoker.com.frameworklib.framework.network.server.Server;
import tatanpoker.com.frameworklib.framework.network.server.SocketServer;
import tatanpoker.com.frameworklib.events.Event;
import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.client.ClientConnection;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.IPacket;
import tatanpoker.com.frameworklib.framework.network.packets.RecognizeDevicePacket;

import static tatanpoker.com.frameworklib.framework.Framework.NEARBY;
import static tatanpoker.com.frameworklib.framework.network.server.SocketServer.SERVERPORT;

/**
 * This is the network.
 */
public class Tree implements ITree{
    /*
    0 = socketServer
    1 = camera
    2 = alert.
     */
    private int id;
    private static Tree instance;
    private NetworkComponent local;
    private SparseArray<EventTriggerInfo> events; //key = hashcode for the event object.

    public static final String SERVER_IP = "192.168.1.28";

    private List<NetworkComponent> components;
    private ClientConnection client;

    private Semaphore semaphore;
    private Server server;

    private Context context;


    public Tree(Context context, int id, Server server) {
        this.context = context;
        this.id = id;
        components = new ArrayList<>();
        this.server = server;
        components.add(server);
    }


    @Override
    public void registerEvents(EventTrigger eventObserver) {
        if(events == null)
            events = new SparseArray<>();
        Method[] methods = eventObserver.getClass().getDeclaredMethods();
        for(Method method : methods){
            if(method.isAnnotationPresent(EventInfo.class)){
                Class<?>[] types = method.getParameterTypes();
                if(Event.class.isAssignableFrom(types[0])){
                    if(method.getAnnotation(EventInfo.class).id()==id) {
                        Framework.getLogger().info("Registering event: " + method.getName() + " with TYPE " + types[0].getName());
                        EventTriggerInfo eventTriggers = events.get(types[0].hashCode());
                        if (eventTriggers == null) {
                            events.put(types[0].hashCode(), new EventTriggerInfo(eventObserver));
                        }
                        events.get(types[0].hashCode()).addInvoke(method);
                    }
                }
            }
        }
    }

    @Override
    public void callEvent(Event event) {
        EventTriggerInfo methods = events.get(event.getClass().hashCode());
        if(methods != null) {
            try {
                methods.call(event);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Tree getInstance() {
        return instance;
    }

    /**
     * Called whenever the socketServer has been enabled.
     */
    @Override
    public void onEnable(){
        instance = this;
        registerComponents(Framework.getComponents());
        for(Component component : components){
            component.onEnable();
        }
        System.out.println("Finished tree onenable");
        if(id != 0) { //If we're not the socketServer. We connect to the socketServer.
            Framework.getLogger().info("Connecting to server...");
            if(NEARBY){
                client = new NearbyClient(context);
            } else {
                client = new SocketClient();
            }
            connect();
        }
        System.out.println("We're here");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @Override
    public int getId() {
        return id;
    }

    /*
    Works with kind of a dynamic programming function.
     */
    public NetworkComponent getLocal() {
        if (local == null) {
            for (NetworkComponent component : components) {
                if (component.getId() == id) {
                    local = component;
                    break;
                }
            }
        }
        return local;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public ClientConnection getClient() {
        return client;
    }


    private void registerComponents(List<Pair<Class, Integer>> devices){
        /*TODO*/
        Framework.getLogger().info("REGISTERING COMPONENTS");
        if(components == null)
            components = new ArrayList<>();

        for(Pair<Class, Integer> classIntegerPair : devices){
            Class classe = classIntegerPair.first;
            int layout = classIntegerPair.second;
            if(classe.getSuperclass().isAnnotationPresent(Device.class)) {
                Device annotation = (Device) classe.getSuperclass().getAnnotation(Device.class);
                NetworkComponent component = null;
                if(annotation.id()==this.getId()) {
                    //We create a device
                    try {
                        component = (NetworkComponent) classe.getSuperclass().getDeclaredConstructor(int.class, int.class, Context.class).newInstance(annotation.id(), layout, context);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else {
                    //We generate a stub
                    try {
                        component = (NetworkComponent) classe.getDeclaredConstructor(int.class, int.class).newInstance(annotation.id(), layout);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                assert component != null;
                //component.setLocal(annotation.id()==this.getId());
                components.add(component);
                Framework.getLogger().info( "Adding component: "+component.getId());
            }
        }
    }


    public NetworkComponent getComponent(int id) throws InvalidIDException {
        NetworkComponent component = getComponentById(id);
        if(component!=null) {
            return component;
        }
        throw new InvalidIDException("Couldn't find a component by that id.");
    }

    @Override
    public NetworkComponent getComponent(ConnectionThread thread) throws InvalidIDException {
        for(NetworkComponent component : components){
            if(component.getClientThread()!=null) {
                if (component.getClientThread().equals(thread)) {
                    return component;
                }
            }
        }
        throw new InvalidIDException("Couldn't find a component by that id.");
    }

    @Override
    public Semaphore getSemaphore() {
        return semaphore;
    }

    private NetworkComponent getComponentById(int id){
        for(NetworkComponent component : components){
            if(component.getId() == id){
                return component;
            }
        }
        return null;
    }


    public List<NetworkComponent> getComponents() {
        return components;
    }


    /**
     * Connects to the server.
     */
    private void connect(){
        semaphore = new Semaphore(0);
        client.connect();
        try {
            semaphore.acquire(); //This releases when the socketServer returns a response after all devices connected.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class EventTriggerInfo{
    private List<Method> invokes;
    private EventTrigger _class;

    public EventTriggerInfo(EventTrigger _class){
        this._class = _class;
        this.invokes = new ArrayList<>();
    }

    public void addInvoke(Method method){
        invokes.add(method);
    }

    public void call(Event event) throws InvocationTargetException, IllegalAccessException {
        for(Method method : invokes){
            method.invoke(_class, event);
        }
    }
}
