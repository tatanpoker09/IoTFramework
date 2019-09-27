package iotframework.camera;

import java.util.ArrayList;
import java.util.List;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.packets.CallMethodPacket;

public class CameraStub extends Camera {
    public CameraStub(int id, int layout) throws InvalidIDException {
        super(id, layout, null);
    }

    @Override
    public void cameraTest() {
        System.out.println("Camera is working through network");
    }

    /*@Override
    public void increaseNumber(Integer amount) {
        List<Object> params = new ArrayList<>();
        params.add(amount);
        CallMethodPacket methodPacket = new CallMethodPacket(Framework.getNetwork().getId(), getId(),"increaseNumber",params);
        Framework.getNetwork().getClient().sendPacket(methodPacket);
        System.out.println("Camera is working through network");
    }*/
    @Override
    public void changeText(String text) {
        List<Object> params = new ArrayList<>();
        params.add(text);
        CallMethodPacket methodPacket = new CallMethodPacket(Framework.getNetwork().getId(), getId(), "changeText", params);
        Framework.getNetwork().getClient().sendPacket(methodPacket);
        System.out.println("Camera is working through network");
    }
}
