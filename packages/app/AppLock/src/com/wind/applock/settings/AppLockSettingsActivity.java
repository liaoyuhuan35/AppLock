package com.wind.applock.settings;

import com.wind.applock.GlobalVars;
import com.wind.applock.R;
import com.wind.applock.Wind;
import com.wind.applock.activity.AppLockActivity;
import com.wind.applock.service.AppLockService;
import com.wind.applock.util.LockStyleUtil;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class AppLockSettingsActivity extends Activity {
	private static final String TAG = "AppLockSettingsActivity";
	private ListView mList;
	private ArrayList<AppInfo> mApplist;
	private Context mContext;
	private LockStyleUtil mLockStyleUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_settings);

		mList = (ListView) findViewById(R.id.list);
		AllAppInfos mAllAppInfos = new AllAppInfos(this);
		mApplist = mAllAppInfos.getAppsInfos();
		mList.setAdapter(new AppsAdapter(this, mApplist));
		mLockStyleUtil = LockStyleUtil.getInstance(mContext
				.getApplicationContext());

		if (!isCurrentLockSupportAppLock()) {
			mLockStyleUtil.resetNewPasswd(mContext);
			this.finish();
		}
		
        //startService();
	}

	@Override
	protected void onPause() {
		Wind.Log(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		Wind.Log(TAG, "onResume");
		super.onResume();
	}
	
    protected void startLockActivity(String topPackageName) {
        Wind.Log(TAG, "startLockActivity  packageName: " );
        Intent intent = new Intent(mContext,
                AppLockActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        GlobalVars.getInstance().updateLockAppName(topPackageName);
        startActivity(intent);
    }

	protected boolean isCurrentLockSupportAppLock() {
		return mLockStyleUtil.isCurrentLockSupportAppLock();
	}

	private void startService() {
		Wind.Log(TAG, "startService");
		Intent hallServiceIntent = new Intent(mContext,
				AppLockService.class);
		mContext.startService(hallServiceIntent);
	}
}
