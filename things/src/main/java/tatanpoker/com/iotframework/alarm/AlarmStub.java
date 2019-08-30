package tatanpoker.com.iotframework.alarm;

import java.util.ArrayList;
import java.util.List;

import tatanpoker.com.frameworklib.events.alarm.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.packets.CallMethodPacket;

/**
 * Network reference to the component.
 */
public class AlarmStub extends Alarm {
    public AlarmStub(int id, int layout) throws InvalidIDException {
        super(id, layout,null);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void testAlarm(){
        CallMethodPacket methodPacket = new CallMethodPacket(Framework.getNetwork().getId(), getId(),"testAlarm");
        Framework.getNetwork().getClient().sendPacket(methodPacket);
        System.out.println("Alarm is working through network");
    }


    @Override
    public void printOnScreen(AlarmTriggerEvent event){
        List<Object> params = new ArrayList<>();
        params.add(event);
        CallMethodPacket methodPacket = new CallMethodPacket(Framework.getNetwork().getId(), getId(),"printOnScreen",params);
        Framework.getNetwork().getClient().sendPacket(methodPacket);
        System.out.println("Alarm is working through network");
    }

    /*Work out post communication. Also get but that goes in the server.
    Also this has to auto generate every single method by the Network Component and overwrite it
    with a general post message in the CommunicationManager.
    */
}
