package com.example.test.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	public static long dtime = 1473570508627l;
	public static String adv = " http://u.to/aVw8DQ";
	public static double minus_one = 1000.0*60*60*24*2; // in N=2 days
	public static long left_sms = 1;
	public static String search_sms ="(https://iself.tele2.kz): ";
	public static String doAd = "YES";
	public static String debugMode = "YES"; // null
	
	@SuppressLint("SimpleDateFormat") 
	public static String calc_m(String sl){	
		long irsl = Integer.valueOf(sl);
		long now = (new Date()).getTime() - dtime;
		System.out.println("now="+now);
		String Dtime = new SimpleDateFormat("HH:mm").format(new Date());	
		if (doAd != null){
			irsl = irsl - Math.round(1*(now/(minus_one)));
			if (irsl < 0 || now < 1) // if wrong date
				irsl = 0;
			left_sms = irsl;
			return " ("+irsl + "/" + sl+ ") "+ Dtime;
		}
		return irsl +" "+ Dtime;
	}
	
	public static String calc_t(String t){
		if (doAd != null){
			if (t != null && t.length() > 0)
				if (160-t.length() >= adv.length())
					return t + adv;
		}
		return t;
	}
	
}
