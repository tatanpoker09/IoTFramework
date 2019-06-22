package tatanpoker.com.frameworklib.network;

import android.app.Activity;
import android.util.SparseArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tatanpoker.com.frameworklib.components.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.components.Server;
import tatanpoker.com.frameworklib.events.Event;
import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Component;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;

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


    public Tree(int id, Activity mainActivity) throws InvalidIDException {
        this(id, mainActivity, new Server());
    }
    public Tree(int id, Activity mainActivity, Server server){
        this.id = id;
        this.server = server;
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
        for(Component component : Framework.getComponents()){
            component.onEnable();
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
            for (NetworkComponent component : Framework.getComponents()) {
                if (component.getId() == id) {
                    local = component;
                    break;
                }
            }
        }
        return local;
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
