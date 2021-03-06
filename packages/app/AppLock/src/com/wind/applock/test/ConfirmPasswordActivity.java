package com.wind.applock.test;

import com.wind.applock.R;
import com.wind.applock.Wind;
//import com.wind.applock.interfaces.ILock;
import com.wind.applock.util.AppLockUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;

public class ConfirmPasswordActivity extends unlockBaseActivity{

	private static final String TAG = "ConfirmPasswordActivity";
	private static final int MODE_CHECK = 0;
	private static final int MODE_FIRST_INPUT = 1;
	private static final int MODE_CONFIRM_PWD = 2;
	private EditText mText;
	private int mMode = 0; // 0: check mode; 1: first input; 2: input confirm;
	private String mFirstPwd;

	private boolean mIdentifying = false;
	private PowerManager mPowerManager;
	private Handler mHandler = new NewHandler();

	private int originalVKState = 0;
	private boolean mNeedRestore = false;

	protected static final int MSG_LCEAR_APP_LOCK = 100;

	private String mCheckedPackage;
	private KeyguardManager mKeyguardManager;
	private LockPatternUtils mLockPatternUtils;

	@Override
	protected void initViews() {
		mContext = this;
		setContentView(R.layout.activity_appunlock);

		mText = (EditText) findViewById(R.id.password_txt);
		findViewById(R.id.button_ok).setOnClickListener(
				new Button.OnClickListener() {
					public void onClick(View view) {
						SharedPreferences mySharedPreferences = getSharedPreferences(
								"data", Activity.MODE_PRIVATE);
						switch (mMode) {
						case 0:
							String pwd = mText.getText().toString();
							Wind.Log(TAG, "onClick  pwd = " + pwd);
							startCheckPassword(pwd);
							break;
						case 1:
							mFirstPwd = mText.getText().toString();
							if (mFirstPwd.length() < 4) {
								MyToast(mContext,
										getString(R.string.fp_password_too_short));
								mFirstPwd = "";
								mText.setText("");
							} else {
								mText.setText("");
							}
							return;
						case 2:
							String secondPwd = mText.getText().toString();
							if (mFirstPwd.equals(secondPwd)) {
								SharedPreferences.Editor editor = mySharedPreferences
										.edit();
								editor.putString("pwd", secondPwd);
								editor.commit();
							} else {
								MyToast(mContext,
										getString(R.string.fp_password_not_the_same));
								mFirstPwd = "";
								mText.setText("");
								return;
							}
							break;
						default:
							break;
						}
						finish();
					}
				});
		findViewById(R.id.button_cancel).setOnClickListener(
				new Button.OnClickListener() {
					public void onClick(View view) {
//						backToHome(mContext);
					}
				});
	}

	@Override
	protected void initServices() {
		mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
	}

	@Override
	protected void initLocks() {
		mLockPatternUtils = new LockPatternUtils(this);
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		Intent intent = getIntent();
		if (intent != null) {
			mCheckedPackage = intent.getStringExtra("pkg_name");
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	protected void clearAppAlreadyUnlocked() {
		AppLockUtils.getInstance(mContext).clearAppAlreadyUnlocked(
				ConfirmPasswordActivity.this, mCheckedPackage);
	}

	protected void startCheckPassword(final String pin) {
		Wind.Log(TAG, "startCheckPassword " + pin);
		final int localEffectiveUserId = android.os.UserHandle.myUserId();
		mPendingLockCheck = LockPatternChecker.checkPassword(mLockPatternUtils,
				pin, localEffectiveUserId,
				new LockPatternChecker.OnCheckCallback() {
					@Override
					public void onChecked(boolean matched, int timeoutMs) {
						Wind.Log(TAG, "startCheckPassword matched=" + matched
								+ " timeoutMs=" + timeoutMs);
						mPendingLockCheck = null;
						if (matched) {
							mHandler.sendEmptyMessageDelayed(
									MSG_LCEAR_APP_LOCK, timeoutMs);
						}
					}
				});
	}

	class NewHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ConfirmPasswordActivity.MSG_LCEAR_APP_LOCK) {
				unlock();
			} else {
				super.handleMessage(msg);
			}
		}
	}

	@Override
	protected void unlock() {
		mHandler.post(new Runnable() {
			public void run() {
				Wind.Log(TAG, "clear  pkg unlock " + mCheckedPackage);
				clearAppAlreadyUnlocked();
			}
		});
	}

}
