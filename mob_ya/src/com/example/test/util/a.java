package com.example.test.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

//Referenced classes of package ru.yandex.android.searchcollector.util:
//         b, UpdateSessionReceiver

public class a {

	public static final String a = "ru/yandex/android/searchcollector/util/a.getName()";

	public a() {
	}

	public static String a(Context context) {
		return context.getSharedPreferences("settings", 0).getString("login",
				"");
	}

	public static String a(String s, String s1) {
		s = (new StringBuilder())
				.append(String.valueOf((System.currentTimeMillis() / 1000L - 5L) / 10L))
				.append(s).append(s1).toString();
		Log.d(a, (new StringBuilder("[getPassword]: salt=")).append(s)
				.toString());
		return b.a(s).substring(0, 6);
	}

	static void a(Context context, Long long1) {
		Log.d(a, "addUpdateTime is called.");
		// PendingIntent pendingintent = PendingIntent.getBroadcast(context, 0,
		// new Intent(context,
		// ru/yandex/android/searchcollector/util/UpdateSessionReceiver), 0);
		// ((AlarmManager)context.getSystemService("alarm")).set(0,
		// long1.longValue(), pendingintent);
	}

	public static void a(Context context, String s, String s1, String s2) {
		context.getSharedPreferences("settings", 0).edit()
				.putString("authCookie", s).putString("authCookieSecure", s1)
				.putString("authYandexLoginCookie", s2)
				.putLong("authCookieSaveTime", System.currentTimeMillis())
				.commit();
	}

	public static void a(Context context, String s, String s1, String s2,
			String s3, String s4) {
		Log.d(a, "User logined");
		context.getSharedPreferences("settings", 0).edit()
				.putString("login", s).putString("secretCode", s1).commit();
		a(context, s2, s3, s4);
		a(context, Long.valueOf(e(context) + 0x1d4c0L));
	}

	public static String b(Context context) {
		return context.getSharedPreferences("settings", 0).getString(
				"secretCode", "");
	}

	public static void c(Context context) {
		Log.d(a, "logout called");
		context.getSharedPreferences("settings", 0).edit().remove("authCookie")
				.remove("authCookieSecure").remove("authYandexLoginCookie")
				.commit();
	}

	public static boolean d(Context context) {
		return !TextUtils.isEmpty(f(context));
	}

	public static long e(Context context) {
		return context.getSharedPreferences("settings", 0).getLong(
				"authCookieSaveTime", System.currentTimeMillis());
	}

	public static String f(Context context) {
		return context.getSharedPreferences("settings", 0).getString(
				"authCookie", "");
	}

	public static String g(Context context) {
		return context.getSharedPreferences("settings", 0).getString(
				"authCookieSecure", "");
	}

	public static String h(Context context) {
		return context.getSharedPreferences("settings", 0).getString(
				"authYandexLoginCookie", "");
	}

}
