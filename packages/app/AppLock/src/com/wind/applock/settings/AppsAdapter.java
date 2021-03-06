package com.wind.applock.settings;

import java.util.ArrayList;

import com.wind.applock.R;
import com.wind.applock.Wind;

import android.content.ComponentName;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AppsAdapter extends BaseAdapter {

	private static final String TAG = "AppsAdapter";

	private ArrayList<AppInfo> mApplist;
	private Context mContext;
	private AppLockUtil mAppLockUtil;

	public AppsAdapter(Context context, ArrayList<AppInfo> appInfos) {
		mContext = context;
		mApplist = appInfos;
		mAppLockUtil = new AppLockUtil(context);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mApplist.size();
	}

	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public View getView(int pos, View conventView, ViewGroup parent) {
		Wind.Log(TAG, "getView pos=" + pos);

		ViewHolder holder;
		if (conventView == null) {
			conventView = LayoutInflater.from(mContext).inflate(
					R.layout.app_item, null);
			holder = new ViewHolder(conventView);
			conventView.setTag(holder);
		} else {
			holder = (ViewHolder) conventView.getTag();
		}

		holder.resetViews(mApplist.get(pos),
				mAppLockUtil.getAppLockState(mApplist.get(pos).mComponentName),
				mApplist.get(pos).mComponentName);
		return conventView;
	}

	private class ViewHolder {

		private View view;
		private TextView tvAppName;
		private ImageView ivAppIcon;
		private ToggleButton mSwitch;
		private ComponentName mComponentName;

		public ViewHolder(View v) {
			view = v;
			ivAppIcon = (ImageView) view.findViewById(R.id.iv);
			tvAppName = (TextView) view.findViewById(R.id.tv);
			mSwitch = (ToggleButton) view.findViewById(R.id.id_switch);
			view.setTag(this);
		}

		public void resetViews(AppInfo appInfo, boolean mbIsLock,
				ComponentName componentName) {
			mComponentName = componentName;
			tvAppName.setText(appInfo.getTitle());
			ivAppIcon.setBackground(appInfo.getAppIcon());

			mSwitch.setChecked(mbIsLock);

			mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton compound,
						boolean bset) {
					if (bset) {
						mAppLockUtil.storeLockApp(mComponentName);
					} else {
						mAppLockUtil.removeLockApp(mComponentName);
					}

				}

			});
		}
	}
}
