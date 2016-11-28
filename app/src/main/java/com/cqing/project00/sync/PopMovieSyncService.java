package com.cqing.project00.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Cqing on 2016/11/25.
 */

public class PopMovieSyncService extends Service{
    private static final Object sSyncAdapterLock = new Object();
    private static PopMovieSyncAdapter sPopMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("PopMovieSyncService", "onCreate - PopMovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (sPopMovieSyncAdapter == null) {
                sPopMovieSyncAdapter = new PopMovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopMovieSyncAdapter.getSyncAdapterBinder();
    }
}
