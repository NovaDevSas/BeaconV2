package com.beaconapp.plugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.altbeacon.beacon.*;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BeaconPlugin extends CordovaPlugin implements BeaconConsumer {
    private BeaconManager beaconManager;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startScan")) {
            if (ContextCompat.checkSelfPermission(cordova.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(cordova.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            } else {
                this.startScan(callbackContext);
            }
            return true;
        }
        return false;
    }

    private void startScan(CallbackContext callbackContext) {
        beaconManager = BeaconManager.getInstanceForApplication(cordova.getActivity().getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.bind(this);
        callbackContext.success("Beacon scanning started");
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons) {
                    Log.d("BeaconPlugin", "Beacon detected: " + beacon.toString());
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return cordova.getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection connection) {
        cordova.getActivity().unbindService(connection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection connection, int mode) {
        return cordova.getActivity().bindService(intent, connection, mode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (beaconManager != null) {
            beaconManager.unbind(this);
        }
    }
}