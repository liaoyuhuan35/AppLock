package com.wind.applock.util;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

import com.android.internal.widget.LockPatternUtils;
import com.wind.applock.Wind;

public class LockStyleUtil {
	private static final String TAG = "LockStyleUtil";

	private LockPatternUtils mLockPatternUtils;
	private static LockStyleUtil mLockStyleUtil;
	private Context mContext;

	public static final int KEY_UNLOCK_SET_OFF = 0;
	public static final int KEY_UNLOCK_SET_PATTERN = 1;
	public static final int KEY_UNLOCK_SET_PIN = 2;
	public static final int KEY_UNLOCK_SET_PASSWORD = 3;
	public static final int KEY_UNLOCK_SET_NONE = 4;
	public static final int KEY_UNLOCK_SET_VOICE_WEAK = 5;

	private LockStyleUtil(Context context) {
		mContext = context;
		mLockPatternUtils = new LockPatternUtils(mContext);
	}

	public static LockStyleUtil getInstance(Context context) {
		Wind.Log(TAG, "getInstance");
		if (mLockStyleUtil == null) {
			mLockStyleUtil = new LockStyleUtil(context);
		}
		return mLockStyleUtil;
	}

	public int getCurrentLockStyle() {
		Wind.Log(TAG, "getCurrentLockStyle");

		if (mLockPatternUtils.isLockScreenDisabled(UserHandle.myUserId())) {
			return KEY_UNLOCK_SET_OFF;
		}
		// / M: set voice unlock to current screen lock @{
		if (mLockPatternUtils.usingVoiceWeak()) {
			return KEY_UNLOCK_SET_VOICE_WEAK;
		}
		// / @}
		switch (mLockPatternUtils.getKeyguardStoredPasswordQuality(UserHandle
				.myUserId())) {
		case DevicePolicyManager.PASSWORD_QUALITY_SOMETHING:
			return KEY_UNLOCK_SET_PATTERN;
		case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC:
		case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX:
			return KEY_UNLOCK_SET_PIN;
		case DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC:
		case DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC:
			return KEY_UNLOCK_SET_PASSWORD;
		case DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED:
			return KEY_UNLOCK_SET_NONE;
		}
		return KEY_UNLOCK_SET_OFF;
	}

	public boolean isCurrentLockSupportAppLock() {
		int nStyle = getCurrentLockStyle();
		switch (nStyle) {
		case KEY_UNLOCK_SET_PATTERN:
		case KEY_UNLOCK_SET_PIN:
		case KEY_UNLOCK_SET_PASSWORD:
			Wind.Log(TAG, "isCurrentLockSupportAppLock true");
			return true;
		default:
			break;
		}
		Wind.Log(TAG, "isCurrentLockSupportAppLock false");
		return false;
	}

	public void resetNewPasswd(Context context) {
		Wind.Log(TAG, "resetNewPasswd");
		Intent intent = new Intent();
		intent.setPackage("com.android.settings");
		intent.setAction("com.wind.applock.action.SET_NEW_LOCK");
		context.sendBroadcast(intent);

		// Intent intent = new Intent();
		// intent.putExtra(ChooseLockGeneric.CONFIRM_FP, true);
		// intent.putExtra(ChooseLockGeneric.CONFIRM_APP_FP, true);
		// intent.setClassName("com.android.settings",
		// "com.android.settings.ChooseLockGeneric");
		// startActivityForResult(intent, FALLBACK_REQUEST_FP);
	}
}
