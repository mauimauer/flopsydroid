package at.maui.flopsydroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

import at.maui.flopsydroid.R;

public class MyActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleAppiClient;

    ImageView mBtnOnWearable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

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
        getMenuInflater().inflate(R.menu.my, menu);
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

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void getNodes(final OnGotNodesListener cb) {
        Wearable.NodeApi.getConnectedNodes(getGoogleApiClient()).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                ArrayList<String> results= new ArrayList<String>();

                for (Node node : nodes.getNodes()) {
                    if(!results.contains(node.getId())) results.add(node.getId());
                }

                cb.onGotNodes(results);
            }
        });
    }

    public interface OnGotNodesListener {
        public void onGotNodes(ArrayList<String> nodes);
    }
}
