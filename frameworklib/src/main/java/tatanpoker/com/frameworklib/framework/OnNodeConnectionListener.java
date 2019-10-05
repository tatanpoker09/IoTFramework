package tatanpoker.com.frameworklib.framework;

public interface OnNodeConnectionListener {
    void onNodeConnected(NetworkComponent component);

    void onNodeDisconnected(NetworkComponent component);
}
