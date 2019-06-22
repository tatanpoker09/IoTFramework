package tatanpoker.com.frameworklib.framework;

public interface Component {
    Component getComponent();
    int getId();

    int getLayout();
    void onEnable();
}
