package com.wind.applock.pattern;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;

import com.google.android.collect.Lists;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.internal.widget.LockPatternUtils.RequestThrottledException;
import com.android.internal.widget.ILockSettings;

public class LockPatternUtil {
	private ILockSettings mLockSettingsService;
	private final Context mContext;
	private final ContentResolver mContentResolver;

	public LockPatternUtil(Context context) {
		mContext = context;
		mContentResolver = context.getContentResolver();
	}

	public static String patternToString(List<LockPatternView.Cell> pattern) {
		if (pattern == null) {
			return "";
		}
		final int patternSize = pattern.size();

		byte[] res = new byte[patternSize];
		for (int i = 0; i < patternSize; i++) {
			LockPatternView.Cell cell = pattern.get(i);
			res[i] = (byte) (cell.getRow() * 3 + cell.getColumn() + '1');
		}
		return new String(res);
	}

	public static List<LockPatternView.Cell> stringToPattern(String string) {
		if (string == null) {
			return null;
		}

		List<LockPatternView.Cell> result = Lists.newArrayList();

		final byte[] bytes = string.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			byte b = (byte) (bytes[i] - '1');
			result.add(LockPatternView.Cell.of(b / 3, b % 3));
		}
		return result;
	}

	private ILockSettings getLockSettings() {
		if (mLockSettingsService == null) {
			ILockSettings service = ILockSettings.Stub
					.asInterface(ServiceManager.getService("lock_settings"));
			mLockSettingsService = service;
		}
		return mLockSettingsService;
	}

	public boolean checkPattern(List<LockPatternView.Cell> pattern, int userId)
			throws RequestThrottledException {
		try {
			VerifyCredentialResponse response = getLockSettings().checkPattern(
					patternToString(pattern), userId);

			if (response.getResponseCode() == VerifyCredentialResponse.RESPONSE_OK) {
				return true;
			} else if (response.getResponseCode() == VerifyCredentialResponse.RESPONSE_RETRY) {
				throw new RequestThrottledException(response.getTimeout());
			} else {
				return false;
			}
		} catch (RemoteException re) {
			return true;
		}
	}

	/**
	 * Check to see if a password matches the saved password. If password
	 * matches, return an opaque attestation that the challenge was verified.
	 * 
	 * @param password
	 *            The password to check.
	 * @param challenge
	 *            The challenge to verify against the password
	 * @return the attestation that the challenge was verified, or null.
	 */
	public byte[] verifyPassword(String password, long challenge, int userId)
			throws RequestThrottledException {
		try {
			VerifyCredentialResponse response = getLockSettings()
					.verifyPassword(password, challenge, userId);

			if (response.getResponseCode() == VerifyCredentialResponse.RESPONSE_OK) {
				return response.getPayload();
			} else if (response.getResponseCode() == VerifyCredentialResponse.RESPONSE_RETRY) {
				throw new RequestThrottledException(response.getTimeout());
			} else {
				return null;
			}
		} catch (RemoteException re) {
			return null;
		}
	}

	/**
	 * Check to see if a password matches the saved password. If no password
	 * exists, always returns true.
	 * 
	 * @param password
	 *            The password to check.
	 * @return Whether the password matches the stored one.
	 */
	public boolean checkPassword(String password, int userId)
			throws RequestThrottledException {
		try {
			VerifyCredentialResponse response = getLockSettings()
					.checkPassword(password, userId);
			if (response.getResponseCode() == VerifyCredentialResponse.RESPONSE_OK) {
				return true;
			} else if (response.getResponseCode() == VerifyCredentialResponse.RESPONSE_RETRY) {
				throw new RequestThrottledException(response.getTimeout());
			} else {
				return false;
			}
		} catch (RemoteException re) {
			return true;
		}
	}

	/**
	 * @return Whether tactile feedback for the pattern is enabled.
	 */
	public boolean isTactileFeedbackEnabled() {
		return Settings.System.getIntForUser(mContentResolver,
				Settings.System.HAPTIC_FEEDBACK_ENABLED, 1,
				UserHandle.USER_CURRENT) != 0;
	}
}
