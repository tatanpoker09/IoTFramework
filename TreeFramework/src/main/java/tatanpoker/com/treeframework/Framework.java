package tatanpoker.com.treeframework;

public class Framework {
    private static final int id = 1; //ID Representing what component this is run on.
    private static Tree network; //This represents the IoT network. Singleton

    public Framework(){
        //This will be our initialize.
        startSystem();
    }
    private void startSystem(){
        if(network == null){ //Singleton.
            network = new Tree(id);
        }
    }

    public Tree getNetwork() {
        return network;
    }
}
