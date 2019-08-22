package iotframework.components.alarm;


import tatanpoker.com.frameworklib.components.Device;
import tatanpoker.com.frameworklib.components.Vector3;
import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventPriority;
import tatanpoker.com.frameworklib.events.alarm.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;

import static tatanpoker.com.frameworklib.framework.Framework.ALARM_ID;

@Device(id=ALARM_ID)
public class Alarm extends NetworkComponent {
    public Alarm(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    public void trigger(Vector3 movement) {
        /*
        TODO CALL EVERY EVENT OF ALARMTRIGGER.
         */
    }

    public void testAlarm(){
        System.out.println("Alarm is working locally");
    }

    @EventInfo(priority = EventPriority.HIGH, id= Framework.ALARM_ID)
    public void printOnScreen(AlarmTriggerEvent eventInfo){
        //TextView tv = (TextView)mainActivity.findViewById(R.id.textView);
        //tv.setText(R.string.cameraPrintDefault);
        System.out.println("We got this! "+eventInfo.getText());
    }

    public void onServerReady(){

    }
}