package com.example.test.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Net {

	// ��������
	// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"
	// />

	public boolean isOnline(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting())
				return true;
			
		} catch (Exception e) {
			Log.d("NET",
					"Error: could not getActiveNetworkInfo " + e.getMessage());
		}
		return false;
	}

	public static String getInetType(Context context) {
		try {
			// context.isDeviceConnectedToInternet();
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
				return "MOBILE";
			if (connectivityManager.getNetworkInfo(
					ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
				return "WIFI";
			if (connectivityManager.getNetworkInfo(
					ConnectivityManager.TYPE_ETHERNET).getState() == NetworkInfo.State.CONNECTED)
				return "ETHERNET";
		} catch (Exception e) {
			Log.d("NET",
					"Error: could not ACCESS_NETWORK_STATE " + e.getMessage());
			return "ERROR";
		}
		return "";
	}
}
