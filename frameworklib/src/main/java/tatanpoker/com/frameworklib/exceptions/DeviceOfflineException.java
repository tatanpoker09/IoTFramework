package tatanpoker.com.frameworklib.exceptions;

public class DeviceOfflineException extends Throwable {
    private String addressId;
    public DeviceOfflineException(String addressId){
        this.addressId = addressId;
    }


    public String getAddressId() {
        return addressId;
    }
}
