package tatanpoker.com.frameworklib.events;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventInfo {
    EventPriority priority() default EventPriority.MEDIUM;

    int id();
}
