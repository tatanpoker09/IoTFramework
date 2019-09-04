package tatanpoker.com.frameworklib.framework.network.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;

import tatanpoker.com.frameworklib.events.server.DeviceConnectedEvent;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.NearbyConnection;
import tatanpoker.com.frameworklib.framework.network.packets.IPacket;

public class NearbyClient extends ClientConnection {
    private Context context;
    private NearbyConnection nearbyConnection;
    private static final String endpointId = Framework.getNetwork().getLocal().toString();
    private String serverId;

    public NearbyClient(Context context){
        this.context = context;
        this.nearbyConnection = new NearbyConnection(context);
    }

    private void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(Framework.getServiceID(), endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're discovering!

                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We're unable to start discovering.
                        });
    }
    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            // An endpoint was found. We request a connection to it.
            Nearby.getConnectionsClient(context)
                    .requestConnection(endpointId, Framework.getServiceID(), connectionCallback)
                    .addOnSuccessListener(
                            (Void unused) -> {
                                // We successfully requested a connection. Now both sides
                                // must accept before the connection is established.
                            })
                    .addOnFailureListener(
                            (Exception e) -> {
                                // Nearby Connections failed to request the connection.
                            });
        }

        @Override
        public void onEndpointLost(@NonNull String s) {

        }
    };

    @Override
    public void connect() {
        startDiscovery();
    }

    @Override
    public void sendPacket(IPacket packet) {
        nearbyConnection.sendPacket(packet, endpointId);
    }

    private ConnectionLifecycleCallback connectionCallback = new ConnectionLifecycleCallback() {
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
                serverId = endpointId;
                Framework.getNetwork().getLocal().setStatus(TreeStatus.ONLINE);
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {

        }
    };
}
