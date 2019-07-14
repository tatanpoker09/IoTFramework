package tatanpoker.com.iotframework.alarm;

import java.io.IOException;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.packets.CallMethodPacket;

/**
 * Network reference to the component.
 */
public class AlarmStub extends Alarm {
    public AlarmStub(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void testAlarm(){
        try {
            CallMethodPacket methodPacket = new CallMethodPacket(Framework.getNetwork().getId(), getId(),"testAlarm");
            Framework.getNetwork().getClientConnectionThread().sendPacket(methodPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Alarm is working through network");
    }

    /*Work out post communication. Also get but that goes in the server.
    Also this has to auto generate every single method by the Network Component and overwrite it
    with a general post message in the CommunicationManager.
    */
}
