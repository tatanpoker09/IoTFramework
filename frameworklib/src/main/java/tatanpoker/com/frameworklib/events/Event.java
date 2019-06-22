package tatanpoker.com.frameworklib.events;

public abstract class Event {
    private String name;
    public Event(String eventName) {
        this.name = eventName;
    }
}
