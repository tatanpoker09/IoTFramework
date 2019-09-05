package tatanpoker.com.frameworklib.framework;

import java.util.List;

import tatanpoker.com.frameworklib.events.Event;
import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.client.ClientConnection;
import tatanpoker.com.frameworklib.framework.network.server.Server;

public interface ITree {
    void onEnable();
    void onDisable();
    int getId();

    void registerEvents(EventTrigger eventObserver);

    void callEvent(Event event);
    NetworkComponent getComponent(int id) throws InvalidIDException;
    NetworkComponent getComponent(ConnectionThread thread) throws InvalidIDException;

    List<NetworkComponent> getComponents();

    NetworkComponent getLocal();

    Server getServer();

    ClientConnection getClient();
}
