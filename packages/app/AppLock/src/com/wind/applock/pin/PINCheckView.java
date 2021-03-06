package com.wind.applock.pin;

import com.wind.applock.ILockResult;
import com.wind.applock.R;
import com.wind.applock.Wind;
import com.wind.applock.pattern.LockPatternChecker;
import com.wind.applock.pattern.LockPatternUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

public class PINCheckView extends LinearLayout implements View.OnKeyListener {
	private static final String TAG = "PINCheckView";
	protected static final int MINIMUM_PASSWORD_LENGTH_BEFORE_REPORT = 3;

	private Context mContext;
	protected AsyncTask<?, ?, ?> mPendingLockCheck;
	protected LockPatternUtil mLockPatternUtils;

	protected PasswordTextView mPasswordEntry;
	private View mOkButton;
	private View mDeleteButton;
	private View mButton0;
	private View mButton1;
	private View mButton2;
	private View mButton3;
	private View mButton4;
	private View mButton5;
	private View mButton6;
	private View mButton7;
	private View mButton8;
	private View mButton9;

	public PINCheckView(Context context) {
		this(context, null);
	}

	public PINCheckView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PINCheckView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		mLockPatternUtils = new LockPatternUtil(mContext);
	}

	@Override
	protected void onFinishInflate() {
		Wind.Log(TAG, "onFinishInflate");

		mPasswordEntry = (PasswordTextView) findViewById(R.id.pinEntry);
		mPasswordEntry.setOnKeyListener(this);

		// Set selected property on so the view can send accessibility events.
		mPasswordEntry.setSelected(true);

		mOkButton = findViewById(R.id.key_enter);
		if (mOkButton != null) {
			mOkButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// doHapticKeyClick();
					if (mPasswordEntry.isEnabled()) {
						Wind.Log(TAG,
								"mOkButton.onClick() is called, set PwEntry false.");
						setPasswordEntryEnabled(false);
						verifyPasswordAndUnlock();
					}
				}
			});
		}

		mDeleteButton = findViewById(R.id.delete_button);
		mDeleteButton.setVisibility(View.VISIBLE);
		mDeleteButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mPasswordEntry.isEnabled()) {
					mPasswordEntry.deleteLastChar();
				}
			}
		});
		mDeleteButton.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				if (mPasswordEntry.isEnabled()) {
					resetPasswordText(true /* animate */);
				}
				return true;
			}
		});

		mButton0 = findViewById(R.id.key0);
		mButton1 = findViewById(R.id.key1);
		mButton2 = findViewById(R.id.key2);
		mButton3 = findViewById(R.id.key3);
		mButton4 = findViewById(R.id.key4);
		mButton5 = findViewById(R.id.key5);
		mButton6 = findViewById(R.id.key6);
		mButton7 = findViewById(R.id.key7);
		mButton8 = findViewById(R.id.key8);
		mButton9 = findViewById(R.id.key9);

		//mPasswordEntry.requestFocus();

		super.onFinishInflate();
	}

	protected void resetPasswordText(boolean animate) {
		mPasswordEntry.reset(animate);
	}

	protected void setPasswordEntryEnabled(boolean enabled) {
		mPasswordEntry.setEnabled(enabled);
	}

	protected void setPasswordEntryInputEnabled(boolean enabled) {
		mPasswordEntry.setEnabled(enabled);
	}

	protected String getPasswordText() {
		return mPasswordEntry.getText();
	}

	private void onPasswordChecked(boolean matched, int timeoutMs,
			boolean isValidPassword) {
		if (matched) {
			// mCallback.dismiss(true);
			unlock();
		} else {
			// if (isValidPassword) {
			// mCallback.reportUnlockAttempt(false, timeoutMs);
			// if (timeoutMs > 0) {
			// long deadline = mLockPatternUtils
			// .setLockoutAttemptDeadline(
			// KeyguardUpdateMonitor.getCurrentUser(),
			// timeoutMs);
			// handleAttemptLockout(deadline);
			// }
			// }
			// if (timeoutMs == 0) {
			// mSecurityMessageDisplay.setMessage(getWrongPasswordStringId(),
			// true);
			// }
		}
		resetPasswordText(true /* animate */);
	}

	protected void verifyPasswordAndUnlock() {
		final String entry = getPasswordText();
		setPasswordEntryInputEnabled(false);
		if (mPendingLockCheck != null) {
			mPendingLockCheck.cancel(false);
		}

		if (entry.length() <= MINIMUM_PASSWORD_LENGTH_BEFORE_REPORT) {
			// to avoid accidental lockout, only count attempts that are long
			// enough to be a
			// real password. This may require some tweaking.
			setPasswordEntryInputEnabled(true);
			onPasswordChecked(false /* matched */, 0, false /*
															 * not valid - too
															 * short
															 */);
			return;
		}

		mPendingLockCheck = LockPatternChecker.checkPassword(mLockPatternUtils,
				entry, android.os.UserHandle.myUserId(),
				new LockPatternChecker.OnCheckCallback() {
					@Override
					public void onChecked(boolean matched, int timeoutMs) {
						setPasswordEntryInputEnabled(true);
						mPendingLockCheck = null;
						onPasswordChecked(matched, timeoutMs, true /* isValidPassword */);
					}
				});
		// onPasswordChecked(true, 0, true /* isValidPassword */);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Wind.Log(TAG,
				"onKeyDown keyCode=" + keyCode + " event=" + event.toString());
		
		if(mCall.callKeycode(event.getKeyCode())){
		    return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		Wind.Log(TAG, "onKey keyCode=" + keyCode + " event=" + event.toString());
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			onKeyDown(keyCode, event);

			return true;
		}else 

		return false;
	}
	
    private ILockResult mCall;
    public void unlock(){
        Wind.Log(TAG, "unlock");
        mCall.unlock();
    }
    public void setLockCallback(ILockResult call){
        Wind.Log(TAG, "setLockCallback");
        mCall = call;
    }
}
