package at.maui.flopsydroid.android.service;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import at.maui.flopsydroid.android.activity.MenuActivity;

/**
 * Created by maui on 08.07.2014.
 */
public class DeviceDataListenerService extends WearableListenerService {

    private static final String TAG = "DeviceDataListenerService";
    private static final String START_ACTIVITY_PATH = "/flopsydroid/wear";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived()");
        if(messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            Log.d(TAG, "Got request to startup FlopsyDroid");
            Intent watchIntent = new Intent(this, MenuActivity.class);
            watchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(watchIntent);
        }
    }
}