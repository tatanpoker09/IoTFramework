package tatanpoker.com.frameworklib.framework.network.server;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Strategy;

import tatanpoker.com.frameworklib.events.server.DeviceConnectedEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.NearbyConnection;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public class NearbyServer extends Server{
    private static final String NICKNAME = "IOTFRAMEWORK";
    private Context context;
    private NearbyConnection nearbyConnection;
    private static final String endpointId = "Server";

    public NearbyServer(Context context) throws InvalidIDException {
        super(0, -10000);
        this.context = context;
        this.nearbyConnection = new NearbyConnection(context);
    }

    @Override
    void sendPacket(Packet serverReadyPacket) {
        for(NetworkComponent component : Framework.getNetwork().getComponents()){
            if(!(component instanceof SocketServer)) {
                nearbyConnection.sendPacket(serverReadyPacket, component.getClass().getName());
            }
        }
    }

    @Override
    protected void startServer() {
        startAdvertising();
    }

    private void startAdvertising() {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(context)
                .startAdvertising(NICKNAME, Framework.getServiceID(), mConnectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        unusedResult -> {
                            Framework.getLogger().info("Now advertising endpoint " + endpointId);
                            onAdvertisingStarted();
                        })
                .addOnFailureListener(
                        e -> {
                            // We were unable to start advertising.
                            Framework.getLogger().severe("Failure to advertise on Nearby Server");
                        });
    }

    private void onAdvertisingStarted() {
        Framework.getLogger().info("Nearby Server is Advertising");
        Framework.getNetwork().getLocal().setStatus(TreeStatus.CONNECTING);
    }

    private ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            new AlertDialog.Builder(context)
                    .setTitle("Accept connection to " + connectionInfo.getEndpointName())
                    .setMessage("Confirm the code matches on both devices: " + connectionInfo.getAuthenticationToken())
                    .setPositiveButton(
                            "Accept",
                            (DialogInterface dialog, int which) ->
                                    // The user confirmed, so we can accept the connection.
                                    Nearby.getConnectionsClient(context)
                                            .acceptConnection(endpointId, nearbyConnection))
                    .setNegativeButton(
                            android.R.string.cancel,
                            (DialogInterface dialog, int which) ->
                                    // The user canceled, so we should reject the connection.
                                    Nearby.getConnectionsClient(context).rejectConnection(endpointId))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
            if(connectionResolution.getStatus()== Status.RESULT_SUCCESS){
                Framework.getNetwork().callEvent(new DeviceConnectedEvent(endpointId));
                Framework.getLogger().info(String.format("Device %s connected! (%d/%d)", endpointId, Framework.getNetwork().getServer().devices, Framework.getNetwork().getComponents().size()));
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {

        }
    };
}
