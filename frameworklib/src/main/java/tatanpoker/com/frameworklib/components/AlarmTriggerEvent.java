package tatanpoker.com.frameworklib.components;

import tatanpoker.com.frameworklib.events.Event;

public class AlarmTriggerEvent extends Event {
    private String text;
    public AlarmTriggerEvent(String text) {
        super("alarm_trigger");
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
