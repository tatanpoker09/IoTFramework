package tatanpoker.com.iotframework;

import android.app.Activity;
import android.widget.TextView;

import tatanpoker.com.frameworklib.components.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventPriority;
import tatanpoker.com.frameworklib.events.EventTrigger;

public class AlarmEvents implements EventTrigger {
    private Activity mainActivity;
    public AlarmEvents(Activity mainActivity){
        this.mainActivity = mainActivity;
    }

    @EventInfo(priority = EventPriority.HIGH, id=MainActivity.ALARM_ID)
    public void printOnScreen(AlarmTriggerEvent eventInfo){
        TextView tv = (TextView)mainActivity.findViewById(R.id.textView);
        tv.setText(R.string.cameraPrintDefault);
        System.out.println("We got this! "+eventInfo.getText());
    }
}
