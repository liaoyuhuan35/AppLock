package com.wind.applock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wind.applock.service.AppLockService;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";
    private Context mContext;

    private void startAppLockService() {
        Wind.Log(TAG, "startAppLockService");
        Intent hallServiceIntent = new Intent(mContext, AppLockService.class);
        mContext.startService(hallServiceIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
        Wind.Log(TAG, "onReceive action=" + action);
        if (action == null) {
            return;
        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            startAppLockService();
        } 
    }

}
