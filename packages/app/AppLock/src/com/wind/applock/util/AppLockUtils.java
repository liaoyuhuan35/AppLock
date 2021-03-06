package com.wind.applock.util;

import com.wind.applock.Wind;

import android.content.Context;
import android.content.SharedPreferences;

public class AppLockUtils {
	private static final String TAG = "AppLockUtils";

	public static final String APPLOCK_PREFERENCES_NAME = "LockAppPreferences";
	public static final String APP_ALREADY_UNLOCKED_PREFERENCES_NAME = "AppAlreadyUnlockedPreferences";
	private static final byte[] lock = new byte[0];

	protected Context mContext;

	private AppLockUtils(Context context) {
		mContext = context;
	}

	private static AppLockUtils sAppLockUtils;

	public static AppLockUtils getInstance(Context context) {
		Wind.Log(TAG, "getInstance");
		if (sAppLockUtils == null) {
			sAppLockUtils = new AppLockUtils(context);
		}
		return sAppLockUtils;
	}

	public void setAppAlreadyUnlocked(Context context, String pkg) {
		setAppUnlockedFlag(context, pkg, 1);
	}

	public static void removeAlreadyUnlockedPreference(Context context) {
		synchronized (lock) {
			Wind.Log(TAG, "removeAlreadyUnlockedPreference");
			context.getSharedPreferences(APP_ALREADY_UNLOCKED_PREFERENCES_NAME,
					Context.MODE_PRIVATE).edit().clear().commit();
		}
	}

	// return true if the unlocked flag is 1, otherwise return false;
	public static boolean isAppAlreadyUnlocked(Context context, String pkg) {
		synchronized (lock) {
//			Wind.Log(TAG, "isAppAlreadyUnlocked pkg = " + pkg);
			SharedPreferences sp = context
					.getSharedPreferences(
							APP_ALREADY_UNLOCKED_PREFERENCES_NAME,
							Context.MODE_PRIVATE);
			int unlocked = sp.getInt(pkg, 0);
			if (unlocked == 1) {
				return true;
			}
			return false;
		}
	}

	// flag: 1 is unlocked, otherwise is locked
	private void setAppUnlockedFlag(Context context, String pkg, int flag) {
		Wind.Log(TAG, "setAppUnlockedFlag pkg = " + pkg + ",flag = " + flag);
		synchronized (lock) {
			SharedPreferences sp = context
					.getSharedPreferences(
							APP_ALREADY_UNLOCKED_PREFERENCES_NAME,
							Context.MODE_PRIVATE);
			int unlocked = sp.getInt(pkg, 0);
			SharedPreferences.Editor editor = sp.edit();
			Wind.Log(TAG, "setAppUnlockedFlag pkg = " + pkg + ",unlocked = "
					+ unlocked);
			if (unlocked != 0) {
				editor.remove(pkg);
				editor.commit();
			}
			editor.putInt(pkg, flag);
			editor.commit();
		}
	}

	public static boolean isAppNeedLock(Context context, String pkg) {
//		Wind.Log(TAG, "isAppNeedLock context = " + context + ":" + pkg);
		synchronized (lock) {
			// change Context.MODE_WORLD_READABLE to Context.MODE_PRIVATE
			SharedPreferences sp = context.getSharedPreferences(
					APPLOCK_PREFERENCES_NAME, Context.MODE_PRIVATE);
			String needLocked = sp.getString(pkg, "");
			Wind.Log(TAG, "isAppNeedLock needLocked = " + needLocked);
			if (needLocked.equals(pkg)) {
				return true;
			}
			return false;
		}
	}

	public void clearAppAlreadyUnlocked(Context context, String pkg) {
		setAppUnlockedFlag(context, pkg, 0);
	}

}
