package tatanpoker.com.iotframework;

import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventPriority;
import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.events.server.DeviceConnectedEvent;
import tatanpoker.com.frameworklib.framework.Framework;

public class ServerEvents implements EventTrigger {
    @EventInfo(priority = EventPriority.MEDIUM,id=0)
    public void onDeviceConnected(DeviceConnectedEvent event){
        Framework.getLogger().info("IP Connected: " + event.getAddress().getHostAddress());
    }
}
