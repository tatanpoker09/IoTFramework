package tatanpoker.com.frameworklib.components;


import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkStub;

public class Alarm extends NetworkStub {
    public Alarm(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    public void trigger(Vector3 movement) {
        /*
        TODO CALL EVERY EVENT OF ALARMTRIGGER.
         */
    }

    public void printOnScreen(Vector3 movement) {
        AlarmTriggerEvent event = new AlarmTriggerEvent(movement.toString());
        Framework.getNetwork().callEvent(event);
    }
}
