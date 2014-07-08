package at.maui.flopsydroid.android.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import at.maui.flopsydroid.android.R;

public class HighScoreActivity extends Activity {

    private TextView mHighScoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        final SharedPreferences mPreferences = getSharedPreferences("floppsy_droid", MODE_PRIVATE);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mHighScoreView = (TextView) stub.findViewById(R.id.highscore);
                mHighScoreView.setText(""+mPreferences.getInt("highscore", 0));
            }
        });
    }
}
