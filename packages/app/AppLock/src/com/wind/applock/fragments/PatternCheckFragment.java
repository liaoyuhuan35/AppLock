package com.wind.applock.fragments;

import com.wind.applock.ILockResult;
import com.wind.applock.Wind;
import com.wind.applock.R;
import com.wind.applock.pattern.KeyguardPatternView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PatternCheckFragment extends Fragment{
    private static final String TAG = "PatternCheckFragment";
    private KeyguardPatternView mPatternView;
    private View mMainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Wind.Log(TAG, "onCreate");
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Wind.Log(TAG, "onCreateView");
        mMainView = inflater.inflate(R.layout.pattern_check_view, container,false);
        mPatternView = (KeyguardPatternView)mMainView.findViewById(R.id.keyguard_pattern_view);
        if (mPatternView != null)
            mPatternView.setLockCallback(mILockResult);
        return mMainView;
    }
    
    @Override
    public void onPause() {
        Wind.Log(TAG, "onPause");
        super.onPause();
    }
    
    @Override
    public void onResume() {
        Wind.Log(TAG, "onResume");
        super.onResume();
    }
    @Override
    public void onStop() {
        Wind.Log(TAG, "onStop");
        super.onStop();
    }
    @Override
    public void onDestroy() {
        Wind.Log(TAG, "onDestroy");
        super.onDestroy();
    }

    private ILockResult mILockResult;
    public void setLockCallback(ILockResult call) {
        Wind.Log(TAG, "setLockCallback mPatternView="+mPatternView );
        mILockResult = call;
    }
}
