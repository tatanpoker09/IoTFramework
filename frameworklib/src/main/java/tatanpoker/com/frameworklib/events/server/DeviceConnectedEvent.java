package tatanpoker.com.frameworklib.events.server;

import java.net.InetAddress;

import tatanpoker.com.frameworklib.events.Event;
import tatanpoker.com.frameworklib.framework.Framework;

public class DeviceConnectedEvent extends Event {
    private InetAddress address;
    private String endPointName;

    public DeviceConnectedEvent(InetAddress address) {
        super("device_connected");
        this.address = address;
        Framework.getLogger().info("Component connected with ip "+address.toString());
    }
    public DeviceConnectedEvent(String endpointId){
        super("device_connected");
        this.endPointName = endpointId;
        Framework.getLogger().info("Component connected with name "+endpointId);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getEndPointName() {
        return endPointName;
    }
}
