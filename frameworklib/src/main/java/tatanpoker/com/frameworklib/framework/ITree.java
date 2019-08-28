package tatanpoker.com.frameworklib.framework;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.Semaphore;

import tatanpoker.com.frameworklib.framework.network.server.SocketServer;
import tatanpoker.com.frameworklib.events.Event;
import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public interface ITree {
    void onEnable();
    void onDisable();
    int getId();

    void registerEvents(EventTrigger eventObserver);

    void callEvent(Event event);

    SocketServer getSocketServer();

    Socket getServerConnection();
    NetworkComponent getComponent(int id) throws InvalidIDException;
    NetworkComponent getComponent(ConnectionThread thread) throws InvalidIDException;

    ConnectionThread getClientConnectionThread();

    Semaphore getSemaphore();

    List<NetworkComponent> getComponents();

    NetworkComponent getLocal();
}
