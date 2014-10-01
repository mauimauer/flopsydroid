package at.maui.flopsydroid.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import at.maui.flopsydroid.R;

public class AboutActivity extends FlopsyDroidActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AboutActivity";
    private GoogleApiClient mGoogleAppiClient;

    ImageView mBtnOnWearable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mBtnOnWearable = (ImageView) findViewById(R.id.startWearable);
        mBtnOnWearable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNodes(new OnGotNodesListener() {
                    @Override
                    public void onGotNodes(ArrayList<String> nodes) {
                        if(nodes.size() > 0) {
                            Wearable.MessageApi.sendMessage(
                                    getGoogleApiClient(), nodes.get(0), "/flopsydroid/wear", null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult result) {
                                    if (!result.getStatus().isSuccess()) {
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        mGoogleAppiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected GoogleApiClient getGoogleApiClient() {
        return mGoogleAppiClient;
    }


    @Override
    protected void onStart() {
        super.onStart();

        mGoogleAppiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mGoogleAppiClient.isConnected()) {
            mGoogleAppiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to Google Services");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to GMS failed. "+GooglePlayServicesUtil.getErrorString(connectionResult.getErrorCode()));

        if(connectionResult.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            GooglePlayServicesUtil.showErrorDialogFragment(connectionResult.getErrorCode(), this, 123);
        }
    }

    private void getNodes(final OnGotNodesListener cb) {
        Wearable.NodeApi.getConnectedNodes(getGoogleApiClient())
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        ArrayList<String> results = new ArrayList<String>();

                        for (Node node : nodes.getNodes()) {
                            if (!results.contains(node.getId())) results.add(node.getId());
                        }

                        cb.onGotNodes(results);
                    }
                }, 3, TimeUnit.SECONDS);
    }

    public interface OnGotNodesListener {
        public void onGotNodes(ArrayList<String> nodes);
    }
}
