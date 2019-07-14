package tatanpoker.com.iotframework.camera;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;

public class CameraStub extends Camera {
    public CameraStub(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    @Override
    public void cameraTest() {
        System.out.println("Camera is working through network");
    }
}
