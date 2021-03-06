package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppLockReceiver extends BroadcastReceiver {
	private static final String TAG = "AppLockReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		android.util.Log.i("wind/" + TAG, "onReceive " + intent.getAction());
		if (intent.getAction().equals("com.wind.applock.action.SET_NEW_LOCK")) {
			Intent sintent = new Intent();
			sintent.setClassName("com.android.settings",
					"com.android.settings.ChooseLockGeneric");
			sintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sintent.putExtra("app_lock_config", true);
			context.startActivity(sintent);
		}
	}

}