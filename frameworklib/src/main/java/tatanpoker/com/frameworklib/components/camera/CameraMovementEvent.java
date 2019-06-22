package tatanpoker.com.frameworklib.components.camera;

import tatanpoker.com.frameworklib.components.Vector3;
import tatanpoker.com.frameworklib.events.Event;

public class CameraMovementEvent extends Event {
    private Vector3 movement;
    public CameraMovementEvent() {
        super("camera_movement");
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void setMovement(Vector3 movement) {
        this.movement = movement;
    }

    public Vector3 getMovement() {
        return movement;
    }
}
