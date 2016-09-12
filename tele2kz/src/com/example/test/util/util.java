package com.example.test.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class util {
	public static long dtime = 1473570508627l;
	public static String adv = " http://u.to/aVw8DQ";
	public static double minus_one = 1000.0*60*60*24*3; // in N days
	public static long left_sms = 1;
	public static String search_sms ="(https://iself.tele2.kz): ";
	
	@SuppressLint("SimpleDateFormat") 
	public static String calc_m(String sl){	
		long irsl = Integer.valueOf(sl);
		long now = (new Date()).getTime() - dtime;
		System.out.println("now="+now);
		irsl = irsl - Math.round(1*(now/(minus_one)));
		if (irsl < 0 || now < 1) // if wrong date
			irsl = 0;
		left_sms = irsl;
		String Dtime = new SimpleDateFormat("HH:mm")
				.format(new Date());	
		return " ("+irsl + "/" + sl+ ") "+ Dtime;
	}
	
	public static String calc_t(String t){	
		if (t != null && t.length() > 0)
			if (160-t.length() >= adv.length())
				return t + adv;
		return t;
	}
	
}
