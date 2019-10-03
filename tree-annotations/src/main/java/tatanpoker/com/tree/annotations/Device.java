package tatanpoker.com.tree.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
//TODO Check retention to make sure it works only at compile time.
@Target(ElementType.TYPE)
public @interface Device {
    int id();

    boolean stub();

    int layout();
}
