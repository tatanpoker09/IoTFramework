package tatanpoker.com.iotframework;

import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventPriority;
import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.events.server.DeviceConnectedEvent;

public class ServerEvents implements EventTrigger {

    @EventInfo(priority = EventPriority.MEDIUM,id=0)
    public void onDeviceConnected(DeviceConnectedEvent event){
        System.out.println("IP Connected: "+event.getAddress().getHostAddress());
    }
}
