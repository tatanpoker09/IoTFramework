package tatanpoker.com.frameworklib.components.camera;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.NetworkStub;

public class Camera extends NetworkStub {
    public Camera(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }
}
