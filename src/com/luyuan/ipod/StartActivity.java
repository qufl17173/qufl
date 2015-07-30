package com.luyuan.ipod;

import com.luyuan.mcu.IIpod;
import com.luyuan.mcu.IIpodCallback;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StartActivity extends Activity {
	private final static String TAG = "StartActivity";

	private final static boolean LOGD = true;

	private String mIpodActionName = "com.luyuan.mcu.service.IpodService";

	private IIpod mIpodService = null;

	boolean mIsIpodServiceBind = false;

	IpodServiceConnection mIpodServiceConnection = new IpodServiceConnection();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.start_main);

		Intent intent = new Intent(mIpodActionName);
		bindService(intent, mIpodServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mIsIpodServiceBind)
			unbindService(mIpodServiceConnection);

	}

	private class IpodServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "connection success ...");
			mIpodService = IIpod.Stub.asInterface(service);
			if (mIpodService == null) {
				if (LOGD) {
					Log.d(TAG, "IpodService connect failed");
				}
				return;
			}
			mIsIpodServiceBind = true;

			try {
				boolean status = mIpodService.getConnectionMode();
				if (status == false) {
					Toast.makeText(StartActivity.this, R.string.ipod_connect,
							Toast.LENGTH_SHORT).show();
				} else {
					startActivity(new Intent(
							"com.luyuan.mcu.intent.action.IPOD_ACTIVITY"));
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			finish();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected...");
			mIsIpodServiceBind = false;
			mIpodService = null;
		}
	}

}
