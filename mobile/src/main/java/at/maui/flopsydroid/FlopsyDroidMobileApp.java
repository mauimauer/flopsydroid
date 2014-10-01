package at.maui.flopsydroid;

import android.app.Application;
import android.content.Context;

import at.maui.flopsydroid.common.WearSharedPreferences;

/**
 * Created by maui on 10.09.2014.
 */
public class FlopsyDroidMobileApp extends Application {

    private WearSharedPreferences mPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        mPreferences = new WearSharedPreferences(this, "flopsy_droid", Context.MODE_PRIVATE);

        if(mPreferences.getInt("app_version_mobile", 0) < BuildConfig.VERSION_CODE) {
            // Upgrade

        }
    }
}