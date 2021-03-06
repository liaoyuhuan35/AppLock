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
import com.wind.applock.settings.AppLockSettingsActivity;
import com.wind.applock.settings.AppLockUtil;
import com.wind.applock.util.LockStyleUtil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
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

public class SettingsCheckActivity extends AppLockActivity {
    private static final String TAG = "AppLockActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mLockStyleUtil = LockStyleUtil.getInstance(mContext);
        if (!mLockStyleUtil.isCurrentLockSupportAppLock()) {
            mLockStyleUtil.resetNewPasswd(mContext);
            SettingsCheckActivity.this.finish();
            return;
        }
        
        mAppLockUtil = new AppLockUtil(this.getApplicationContext());
        setContentView(R.layout.activity_app_lock);
        initLockParams();
    }

    @Override
    public void lock() {
        Wind.Log(TAG, "lock");
    }

    @Override
    public void unlock() {
        Wind.Log(TAG, "unlock mLockPkg=" + mLockPkg);
        startAppLockSettingsActivity();
        SettingsCheckActivity.this.finish();
    }

    protected void startAppLockSettingsActivity() {
        Wind.Log(TAG, "startAppLockSettingsActivity");
        Intent intent = new Intent(mContext, AppLockSettingsActivity.class);
        intent.setAction("com.wind.applock.settings.AppLockSettingsActivity");
        mContext.startActivity(intent);
    }

}
