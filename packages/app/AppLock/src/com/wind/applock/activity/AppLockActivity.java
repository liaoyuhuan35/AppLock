package com.wind.applock.activity;

import com.wind.applock.GlobalVars;
import com.wind.applock.ILockResult;
import com.wind.applock.R;
import com.wind.applock.Wind;
import com.wind.applock.fragments.PINCheckFragment;
import com.wind.applock.fragments.PasswdCheckFragment;
import com.wind.applock.fragments.PatternCheckFragment;
import com.wind.applock.passwd.KeyguardPasswordView;
import com.wind.applock.pattern.KeyguardPatternView;
import com.wind.applock.pin.PINCheckView;
import com.wind.applock.service.AppLockService;
import com.wind.applock.settings.AppLockUtil;
import com.wind.applock.util.LockStyleUtil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class AppLockActivity extends BaseActivity implements ILockResult {
    private static final String TAG = "AppLockActivity";
    protected Context mContext;
    protected LockStyleUtil mLockStyleUtil;
    protected AppLockUtil mAppLockUtil;

    protected Fragment mPINCheckFragment;
    protected Fragment mPasswdCheckFragment;
    protected Fragment mPatternCheckFragment;
    protected FragmentManager mFragmentManager;

    protected String mLockPkg;
    

    private BroadcastReceiver mFingerReceiver;
    private static final String MSG_FINGER_SUCCESS = "com.wind.msg.fingerprintservice";


    protected void initLockParams() {
        Wind.Log(TAG, "initLockParams");
        mPasswdCheckFragment = new PasswdCheckFragment();
        mPatternCheckFragment = new PatternCheckFragment();
        mPINCheckFragment = new PINCheckFragment();
        if (null != mPasswdCheckFragment)
            ((PasswdCheckFragment) mPasswdCheckFragment).setLockCallback(this);
        if (null != mPatternCheckFragment)
            ((PatternCheckFragment) mPatternCheckFragment)
                    .setLockCallback(this);
        if (null != mPINCheckFragment)
            ((PINCheckFragment) mPINCheckFragment).setLockCallback(this);
        
        mFragmentManager = getSupportFragmentManager();
    }
    

    protected void initLockFragment() {
        Wind.Log(TAG, "initLockFragment");
        mLockStyleUtil = LockStyleUtil.getInstance(mContext);
        int nLockStyle = mLockStyleUtil.getCurrentLockStyle();
        if (nLockStyle == LockStyleUtil.KEY_UNLOCK_SET_PASSWORD) {
            replaceFragment(mPasswdCheckFragment);
        } else if (nLockStyle == LockStyleUtil.KEY_UNLOCK_SET_PIN) {
            replaceFragment(mPINCheckFragment);
        } else if (nLockStyle == LockStyleUtil.KEY_UNLOCK_SET_PATTERN) {
            replaceFragment(mPatternCheckFragment);
        } else {
            mLockStyleUtil.resetNewPasswd(mContext);
        }
    }

    protected void replaceFragment(Fragment fragment) {
        Wind.Log(TAG, "replaceFragment fragment=" + fragment);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.id_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mAppLockUtil = new AppLockUtil(this.getApplicationContext());
        Wind.Log(TAG, "onCreate mLockPkg=" + mLockPkg);

//        if (false) {
//            initLayoutParams();
//            initLockUI();
//            mContainer = new FrameLayout(mContext);
//            mContainer.addView(mLockUI, mParams);
//            setContentView(mContainer, mContParams);
//        } else {
            setContentView(R.layout.activity_app_lock);
            initLockParams();
            

            mFingerReceiver = new FingerReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(MSG_FINGER_SUCCESS);
            registerReceiver(mFingerReceiver, filter);
//        }
    }

    @Override
    protected void onRestart() {
        Wind.Log(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Wind.Log(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Wind.Log(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        mLockPkg = GlobalVars.getInstance().getLockAppName();
        initLockFragment();
        Wind.Log(TAG, "onResume mLockPkg=" + mLockPkg);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Wind.Log(TAG, "onBackPressed");
        backToHome(mContext);
    }

    @Override
    public void lock() {
        Wind.Log(TAG, "onBackPressed");
    }

    @Override
    public void unlock() {
        Wind.Log(TAG, "unlock mLockPkg=" + mLockPkg);

        mAppLockUtil.setAppUnlocked(mContext, mLockPkg);
        this.finish();
    }

    @Override
    public boolean callKeycode(int keyCode) {
        Wind.Log(TAG, "callKeycode keyCode=" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToHome(mContext);
            return true;
        }
        return false;
    }

    protected void backToHome(Context context) {
        Wind.Log(TAG, "backToHome  sleep 300");
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            Wind.Log(TAG, "backToHome  sleep " + e);
        } finally {
            finish();
        }
    }

    private class FingerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Wind.Log(TAG, "handleMessage " + action);
            if(MSG_FINGER_SUCCESS.equals(action)){
                int fingerid = intent.getIntExtra("fingerid", 0);
                Wind.Log(TAG, "handleMessage fingerid=" + fingerid);
                unlock();
            }
        }

    }
    

/*
    protected FrameLayout mContainer;
    protected LayoutParams mContParams;
    protected LayoutParams mParams;
    protected LinearLayout mLockUI;
    protected KeyguardPasswordView mKeyguardPasswordView;
    protected KeyguardPatternView mKeyguardPatternView;
    protected PINCheckView mPINCheckView;
    
    protected void initLayoutParams() {
        Wind.Log(TAG, "initLayoutParams");
        mParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mContParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    protected void initLockUI() {
        Wind.Log(TAG, "initLockUI");
        mLockStyleUtil = LockStyleUtil.getInstance(mContext);
        int nLockStyle = mLockStyleUtil.getCurrentLockStyle();
        if (nLockStyle == LockStyleUtil.KEY_UNLOCK_SET_PASSWORD) {
            mLockUI = (LinearLayout) LayoutInflater.from(mContext).inflate(
                    R.layout.passwd_check_view, null);
            mKeyguardPasswordView = (KeyguardPasswordView) mLockUI
                    .findViewById(R.id.keyguard_password_view);
            mKeyguardPasswordView.setLockCallback(this);
        } else if (nLockStyle == LockStyleUtil.KEY_UNLOCK_SET_PIN) {
            mLockUI = (LinearLayout) LayoutInflater.from(mContext).inflate(
                    R.layout.pin_check_view, null);
            mPINCheckView = (PINCheckView) mLockUI
                    .findViewById(R.id.keyguard_pin_view);
            mPINCheckView.setLockCallback(this);
        } else if (nLockStyle == LockStyleUtil.KEY_UNLOCK_SET_PATTERN) {
            mLockUI = (LinearLayout) LayoutInflater.from(mContext).inflate(
                    R.layout.pattern_check_view, null);
            mKeyguardPatternView = (KeyguardPatternView) mLockUI
                    .findViewById(R.id.keyguard_pattern_view);
            mKeyguardPatternView.setLockCallback(this);
        } else {
            mLockStyleUtil.resetNewPasswd(mContext);
            mLockUI = null;
        }
        Wind.Log(TAG, "initLockUI nLockStyle=" + nLockStyle);
    }
*/
}
