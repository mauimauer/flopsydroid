package at.maui.flopsydroid.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

/**
 * Created by maui on 10.09.2014.
 */
public class FlopsyDroidActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
    }
}
