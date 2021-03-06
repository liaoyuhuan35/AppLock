package com.wind.applock.service;

import com.wind.applock.GlobalVars;
import com.wind.applock.R;
import com.wind.applock.Wind;
import com.wind.applock.WindApp;
import com.wind.applock.activity.AppLockActivity;
import com.wind.applock.settings.AllAppInfos;
import com.wind.applock.settings.AppLockUtil;
import com.wind.applock.test.ConfirmPasswordActivity;
import com.wind.applock.util.LockStyleUtil;

import android.app.IProcessObserver;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

public class AppLockService extends LockBaseService implements Handler.Callback {
	private static final String TAG = "AppLockService";

	private static final byte[] lock = new byte[0];
	public static boolean sForgroundChangeFromPWD = false;
	private String[] mForegroundPkgs;
	private ActivityManager mActivityManager;

	private static final int SHOW_PASSWORD_ACTIVITY_DELAY = 100;
	private int mCurrentPid = -1;

	// appLockUI
	WindowManager.LayoutParams mParams;

	// 取得当前锁屏方式
	protected LockStyleUtil mLockStyleUtil;
	protected AppLockUtil mAppLockUtil;

	private BroadcastReceiver mAppLockReceiver;

	// 该应用是否需要上锁
    protected boolean isAppNeedLock(String pkg) {
        boolean isNeedLock = mAppLockUtil.isAppNeedLock(mContext, pkg);
        boolean isAlreadUnlocked = mAppLockUtil.isAppAlreadyUnlocked(mContext,
                pkg);
//        Wind.Log(TAG, "isAppNeedLock pkg=" + pkg + " isNeedLock=" + isNeedLock
//                + " !isAlreadUnlocked=" + !isAlreadUnlocked);
        return isNeedLock && !isAlreadUnlocked;
    }

	// 应用锁开关是否打开
	protected boolean isAppLockOn() {
		return true;
	}

	@Override
	protected void initVariables() { 
		Wind.Log(TAG, "initVariables ");
		mContext = this;
		mHandler = new LockHandler();
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		mAppLockUtil = new AppLockUtil(this.getApplicationContext());
		
		mAppLockUtil.clearAppAlreadyUnlocked(mContext);
//		mAppLockUtil.storeLockApp(new ComponentName("com.wind.applock","com.wind.applock.settings.AppLockSettingsActivity"));
//        mAppLockUtil.removeLockApp(new ComponentName("com.wind.applock","com.wind.applock.settings.AppLockSettingsActivity"));

		WindApp.getInstance(mContext.getApplicationContext());

		mAppLockReceiver = new AppLockReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mAppLockReceiver, filter);
	}

	protected void initNewLayoutParams() {
		Wind.Log(TAG, "initNewLayoutParams ");
		mParams = new WindowManager.LayoutParams();
		mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		mParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				// | LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_TRANSLUCENT_STATUS
				| LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| LayoutParams.FLAG_LAYOUT_NO_LIMITS
				;
		mParams.width = LayoutParams.MATCH_PARENT;
	}

	@Override
	protected void registerObservers() {
		Wind.Log(TAG, "registerObservers ");
		try {
			ActivityManagerNative.getDefault().registerProcessObserver(
					new IProcessObserver.Stub() {

						@Override
						public void onForegroundActivitiesChanged(int pid,
								int uid, boolean foregroundActivities)
								throws RemoteException {
							synchronized (lock) {
								if (foregroundActivities) {
								    //检测是否需要上锁
                                    boolean isCurStyleNeedLock = LockStyleUtil.getInstance(mContext).isCurrentLockSupportAppLock();
                                    if(!isCurStyleNeedLock)
                                        return;
								    
									PackageManager pm = getPackageManager();
									mForegroundPkgs = pm.getPackagesForUid(uid);
									mCurrentPid = pid;
									mHandler.removeCallbacks(mShowPwdRunnable);
									mHandler.postDelayed(mShowPwdRunnable,
											SHOW_PASSWORD_ACTIVITY_DELAY);
								}
							}
						}

						@Override
						public void onProcessStateChanged(int pid, int uid,
								int procState) throws RemoteException {
						}

						@Override
						public void onProcessDied(int pid, int uid)
								throws RemoteException {
						}

					});
		} catch (RemoteException e) {
			Wind.Log(TAG, "registerObservers " + e.toString());
		}
	}
	protected Runnable mShowPwdRunnable = new Runnable() {
		public void run() {
			synchronized (lock) {
				Wind.Log(TAG, "mShowPwdRunnable");
				if (mForegroundPkgs == null) {
					sForgroundChangeFromPWD = false;
					return;
				}
				int len = mForegroundPkgs.length;
				String runningTopPackage = mActivityManager.getRunningTasks(1)
						.get(0).topActivity.getPackageName();

				boolean mbLocked = false;
				for (int i = 0; i < len; i++) {
					if (isAppNeedLock(runningTopPackage) && isAppLockOn()) {
					    startLockActivity(runningTopPackage);
						mbLocked = true;
						break;
					}
				}
				sForgroundChangeFromPWD = false;
			}
		}
	};

    protected void startLockActivity(String topPackageName) {
        Wind.Log(TAG, "startLockActivity  packageName: " );
        Intent intent = new Intent(mContext,
                AppLockActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        GlobalVars.getInstance().updateLockAppName(topPackageName);
        startActivity(intent);
    }

	@Override
	public boolean handleMessage(Message msg) {
		Wind.Log(TAG, "handleMessage " + msg.what);
		switch (msg.what) {
		default:
			break;
		}
		return false;
	}


	private class AppLockReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Wind.Log(TAG, "handleMessage " + intent.getAction());
			
			if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
			    mAppLockUtil.clearAppAlreadyUnlocked(mContext);
			}
		}

	}
}
