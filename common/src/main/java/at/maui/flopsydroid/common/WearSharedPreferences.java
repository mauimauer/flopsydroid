package at.maui.flopsydroid.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by maui on 10.09.2014.
 */
public class WearSharedPreferences implements SharedPreferences, GoogleApiClient.ConnectionCallbacks, DataApi.DataListener {

    public static final String TAG = "WearSharedPreferences";

    private Context mContext;
    private String mName;
    private ArrayList<OnSharedPreferenceChangeListener> mListeners;
    private GoogleApiClient mGoogleApiClient;

    private SharedPreferences mLocalPreferences;

    public WearSharedPreferences(Context ctx, String name, int mode) {
        mContext = ctx;
        mName = name;

        mListeners = new ArrayList<OnSharedPreferenceChangeListener>();
        mLocalPreferences = ctx.getSharedPreferences(mName, mode);

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public Map<String, ?> getAll() {
        return mLocalPreferences.getAll();
    }

    @Override
    public String getString(String key, String defValue) {
        return mLocalPreferences.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return mLocalPreferences.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mLocalPreferences.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mLocalPreferences.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mLocalPreferences.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mLocalPreferences.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return mLocalPreferences.contains(key);
    }

    @Override
    public Editor edit() {
        return new Editor(this);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mLocalPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mLocalPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void apply(Editor changes) {
        new AsyncTask<Editor, Void, Void>() {

            @Override
            protected Void doInBackground(Editor... params) {
                Editor changes = params[0];
                SharedPreferences.Editor localEditor = mLocalPreferences.edit();

                PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/"+mName);
                DataMap dataMap = dataMapRequest.getDataMap();

                if(changes.doClear()) {
                    localEditor.clear();
                    dataMap.clear();
                } else if(changes.getToBeRemoved().size() > 0) {

                    for(String key : changes.getToBeRemoved()) {
                        localEditor.remove(key);
                        dataMap.remove(key);
                    }
                }

                for(String key : changes.getChanges().keySet()) {
                    Object value = changes.getChanges().get(key);

                    if(value instanceof  Integer) {
                        localEditor.putInt(key, (Integer)value);
                        dataMap.putInt(key, (Integer)value);
                    } else if(value instanceof Long) {
                        localEditor.putLong(key, (Long) value);
                        dataMap.putLong(key, (Long) value);
                    } else if(value instanceof Float) {
                        localEditor.putFloat(key, (Float) value);
                        dataMap.putFloat(key, (Float) value);
                    } else if(value instanceof Boolean) {
                        localEditor.putBoolean(key, (Boolean) value);
                        dataMap.putBoolean(key, (Boolean) value);
                    } else if(value instanceof String) {
                        localEditor.putString(key, (String) value);
                        dataMap.putString(key, (String) value);
                    } else if(value instanceof ArrayList) {
                        Set<String> set = new HashSet<String>();
                        set.addAll((ArrayList <String>) value);
                        localEditor.putStringSet(key, set);
                        dataMap.putStringArrayList(key, (ArrayList<String>) value);
                    }
                }

                localEditor.commit();

                PutDataRequest putRequest = dataMapRequest.asPutDataRequest();
                PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                        .putDataItem(mGoogleApiClient, putRequest);

                return null;
            }
        }.execute(changes);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        SharedPreferences.Editor localEditor = mLocalPreferences.edit();

        for (DataEvent event : dataEvents) {
            if(event.getDataItem().getUri().toString().startsWith("/"+mName)) {
                if (event.getType() == DataEvent.TYPE_DELETED) {
                    localEditor.clear();
                } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                    DataMap map = DataMap.fromByteArray(event.getDataItem().getData());

                    for(String key : map.keySet()) {
                        Object value = map.get(key);
                        if(value instanceof  Integer) {
                            localEditor.putInt(key, (Integer)value);
                        } else if(value instanceof Long) {
                            localEditor.putLong(key, (Long) value);
                        } else if(value instanceof Float) {
                            localEditor.putFloat(key, (Float) value);
                        } else if(value instanceof Boolean) {
                            localEditor.putBoolean(key, (Boolean) value);
                        } else if(value instanceof String) {
                            localEditor.putString(key, (String) value);
                        } else if(value instanceof ArrayList) {
                            Set<String> set = new HashSet<String>();
                            set.addAll((ArrayList <String>) value);
                            localEditor.putStringSet(key, set);
                        }
                    }
                }
            }
        }
    }

    public class Editor implements SharedPreferences.Editor {

        private WearSharedPreferences mPreferences;

        private Bundle mChanges;
        private ArrayList<String> mToBeRemoved;
        private boolean mClear = false;

        private Editor(WearSharedPreferences preferences) {
            mPreferences = preferences;
            mToBeRemoved = new ArrayList<String>();
            mChanges = new Bundle();
        }

        @Override
        public SharedPreferences.Editor putString(String key, String value) {
            mChanges.putString(key, value);
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            ArrayList<String> list = new ArrayList<String>();
            list.addAll(values);
            mChanges.putStringArrayList(key, list);
            return this;
        }

        @Override
        public SharedPreferences.Editor putInt(String key, int value) {
            mChanges.putInt(key, value);
            return this;
        }

        @Override
        public SharedPreferences.Editor putLong(String key, long value) {
            mChanges.putLong(key, value);
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat(String key, float value) {
            mChanges.putFloat(key, value);
            return this;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            mChanges.putBoolean(key, value);
            return this;
        }

        @Override
        public SharedPreferences.Editor remove(String key) {
            mToBeRemoved.add(key);
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            mClear = true;
            return this;
        }

        @Override
        public boolean commit() {
            return false;
        }

        @Override
        public void apply() {
            mPreferences.apply(this);
        }

        private boolean doClear() {
            return mClear;
        }

        private ArrayList<String> getToBeRemoved() {
            return mToBeRemoved;
        }

        private Bundle getChanges() {
            return mChanges;
        }
    }
}
