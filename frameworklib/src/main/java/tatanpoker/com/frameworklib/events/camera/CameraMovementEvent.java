package tatanpoker.com.frameworklib.events.camera;

import tatanpoker.com.frameworklib.components.Vector3;
import tatanpoker.com.frameworklib.events.Event;

public class CameraMovementEvent extends Event {
    private Vector3 movement;
    public CameraMovementEvent(Vector3 movement) {
        super("camera_movement");
        this.movement = movement;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Vector3 getMovement() {
        return movement;
    }
}
