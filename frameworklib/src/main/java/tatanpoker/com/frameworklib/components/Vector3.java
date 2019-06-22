package tatanpoker.com.frameworklib.components;

public class Vector3 {
    private final double x;
    private final double y;
    private final double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Getters...

    public Vector3 add(Vector3 addend) {
        return new Vector3(x + addend.x, y + addend.y, z + addend.z);
    }

    // Other operations...
}
