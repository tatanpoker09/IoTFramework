package tatanpoker.com.treeframework;

/**
 * Todo, check if I can autogenerate here some methods.
 */
public class Tree implements ITree{
    /*
    0 = server
    1 = camera
    2 = alert.
     */
    private int id;
    private static Tree instance;

    public Tree(int id) {
        this.id = id;
    }


    public static Tree getInstance() {
        return instance;
    }

    /**
     * Called whenever the server has been enabled.
     */
    @Override
    public void onEnable(){
        instance = this;
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public int getId() {
        return id;
    }
}
