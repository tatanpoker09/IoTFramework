package tatanpoker.com.frameworklib.framework;

import tatanpoker.com.frameworklib.components.Server;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;

public abstract class NetworkComponent implements Component {
    private int id;
    private int layout;

    public NetworkComponent(int id, int layout) throws InvalidIDException {
        if(id==0 && !(this instanceof Server)){
            throw new InvalidIDException("ID 0 is reserved for the Server!");
        }
        this.id = id;
        this.layout = layout;
    }

    @Override
    public Component getComponent() {
        if(isLocal()){ //Working locally.
            return this;
        } else {
            //Returns a NetworkStub (which in turn is just this but on a different layer.
            NetworkStub stub = (NetworkStub) this;
            return stub;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getLayout() {
        return layout;
    }

    @Override
    public void onEnable(){
        //Has to be overriden. This doesn't force the user to provide an implementation.
    }

    public boolean isLocal() {
        if(Framework.getNetwork()!=null) {
            return Framework.getNetwork().getId() == this.id;
        }
        return false;
    }
}
