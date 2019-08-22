package tatanpoker.com.frameworklib.framework.network;

import android.util.Pair;
import android.util.SparseArray;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import tatanpoker.com.frameworklib.components.Device;
import tatanpoker.com.frameworklib.components.Server;
import tatanpoker.com.frameworklib.events.Event;
import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Component;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.packets.IPacket;
import tatanpoker.com.frameworklib.framework.network.packets.RecognizeDevicePacket;

import static tatanpoker.com.frameworklib.components.Server.SERVERPORT;

/**
 * This is the network.
 */
public class Tree implements ITree{
    /*
    0 = server
    1 = camera
    2 = alert.
     */
    private int id;
    private static Tree instance;
    private Server server;
    private NetworkComponent local;
    private SparseArray<EventTriggerInfo> events; //key = hashcode for the event object.
    public static final String SERVER_IP = "192.168.1.134";

    private Socket socket;
    private ConnectionThread clientThread;
    private List<NetworkComponent> components;

    private Semaphore semaphore;


    public Tree(int id) throws InvalidIDException {
        this(id, new Server());
    }

    public Tree(int id, Server server){
        this.id = id;
        this.server = server;
        components = new ArrayList<>();
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
     * Called whenever the server has been enabled.
     */
    @Override
    public void onEnable(){
        instance = this;
        registerComponents(Framework.getComponents());
        for(Component component : components){
            component.onEnable();
        }
        System.out.println("Finished tree onenable");
        if(id != 0) { //If we're not the server. We connect to the server.
            connect();
        }
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
                        component = (NetworkComponent) classe.getSuperclass().getDeclaredConstructor(int.class, int.class).newInstance(annotation.id(), layout);
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
    public ConnectionThread getClientConnectionThread() {
        return clientThread;
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

    public Server getServer() {
        return server;
    }

    public Socket getServerConnection() {
        return socket;
    }

    /**
     * Connects to the server.
     */
    private void connect(){
        semaphore = new Semaphore(0);

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            Framework.getLogger().info(String.format("Attempting to connect to %s:%s", SERVER_IP, SERVERPORT));
            socket = new Socket(serverAddr, SERVERPORT);
            Framework.getLogger().info("Successfully connected to server on ip: " + SERVER_IP + ":" + SERVERPORT);
            Framework.getLogger().info("Sending Recognize Packet with id: " + getId());
            IPacket recognizePacket = new RecognizeDevicePacket(Framework.getNetwork().getId(), Objects.requireNonNull(Framework.getNetwork().getComponent(Framework.getNetwork().getId())).getClass().getName());
            this.clientThread = new ConnectionThread(socket);

            this.clientThread.sendPacket(recognizePacket);

            semaphore.acquire(); //This releases when the server returns a response after all devices connected.
        } catch (InterruptedException | IOException | InvalidIDException e) {
            e.printStackTrace();
        }
        this.clientThread.start();
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
