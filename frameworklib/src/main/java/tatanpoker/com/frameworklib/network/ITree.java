package tatanpoker.com.frameworklib.network;

import android.app.Activity;

import tatanpoker.com.frameworklib.components.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.events.Event;
import tatanpoker.com.frameworklib.events.EventTrigger;

public interface ITree {
    void onEnable();
    void onDisable();
    int getId();

    void registerEvents(EventTrigger eventObserver);

    void callEvent(Event event);
}
