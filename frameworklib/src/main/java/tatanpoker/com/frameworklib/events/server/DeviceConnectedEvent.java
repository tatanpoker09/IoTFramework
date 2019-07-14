package tatanpoker.com.frameworklib.events.server;

import java.net.InetAddress;

import tatanpoker.com.frameworklib.events.Event;

public class DeviceConnectedEvent extends Event {
    private InetAddress address;

    public DeviceConnectedEvent(InetAddress address) {
        super("device_connected");
        this.address = address;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public InetAddress getAddress() {
        return address;
    }
}
