package com.wind.applock.activity;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.wind.applock.Wind;

/*
 * 
 * 定义基本activity类，通用操作
 * */
public abstract class BaseActivity extends FragmentActivity {
	private static final String TAG = "BaseActivity";

	protected void MyToast(Context context, String str) {
		Wind.Log(TAG, " MyToast str = " + str);
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
}
