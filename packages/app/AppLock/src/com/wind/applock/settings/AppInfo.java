package com.wind.applock.settings;

import com.wind.applock.R;
import com.wind.applock.Wind;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class AppInfo {
	private static final String TAG = "AppInfo";
	private Context mContext;

	private Drawable mAppDrawable;

	private ResolveInfo mInfo;
	public ComponentName mComponentName;
	private String mTitle;
	private PackageManager mPm;

	public AppInfo(Context context, ResolveInfo info) {
		this.mContext = context;
		this.mInfo = info;
		mPm = mContext.getPackageManager();
		final String packageName = info.activityInfo.applicationInfo.packageName;
		this.mComponentName = new ComponentName(packageName,
				info.activityInfo.name);
		mTitle = info.loadLabel(mPm).toString();
		mAppDrawable = getFullResIcon(info.activityInfo);
	}

	public String getTitle() {
		return mTitle;
	}

	public Drawable getAppIcon() {
		return mAppDrawable;
	}

	public Drawable getFullResIcon(ActivityInfo info) {
//		Wind.Log(TAG, "getFullResIcon");
		Resources res;
		try {
			res = mPm.getResourcesForApplication(info.applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			res = null;
			Wind.Log(TAG, "getFullResIcon" + e.toString());
		}

		if (res != null) {
			int iconId = info.getIconResource();
			if (iconId != 0) {
				return getFullResIcon(res, iconId);
			}
		}
		return getFullResDefaultActivityIcon();
	}

	public Drawable getFullResIcon(Resources res, int iconId) {
		Wind.Log(TAG, "getFullResIcon");
		Drawable d;
		try {
			d = res.getDrawable(iconId);
			// d = res.getDrawableForDensity(iconId, mIcon);
		} catch (Resources.NotFoundException e) {
			Wind.Log(TAG, "getFullResIcon" + e.toString());
			d = null;
		}
		return (d != null) ? d : getFullResDefaultActivityIcon();
	}

	public Drawable getFullResDefaultActivityIcon() {
		Wind.Log(TAG, "getFullResDefaultActivityIcon");
		return getFullResIcon(Resources.getSystem(),
				android.R.mipmap.sym_def_app_icon);
	}

}
