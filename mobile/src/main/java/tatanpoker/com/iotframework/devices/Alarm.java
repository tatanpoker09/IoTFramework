package tatanpoker.com.iotframework.devices;


import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventPriority;
import tatanpoker.com.frameworklib.events.alarm.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.iotframework.R;
import tatanpoker.com.tree.annotations.Device;

import static tatanpoker.com.iotframework.MainActivity.ALARM_ID;

@Device(id = ALARM_ID, layout = R.layout.alarm_layout)
public class Alarm extends NetworkComponent {
    public Alarm(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    public void testAlarm() {
        System.out.println("Alarm is working locally");
    }

    @EventInfo(priority = EventPriority.HIGH, id = ALARM_ID)
    public void printOnScreen(AlarmTriggerEvent eventInfo) {
        //TextView tv = (TextView)mainActivity.findViewById(R.id.textView);
        //tv.setText(R.string.cameraPrintDefault);
        System.out.println("We got this! " + eventInfo.getText());
    }

    public void test(String test, int id) {

    }

    public void test(String test) {

    }
}
