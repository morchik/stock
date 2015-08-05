package com.example.test.util;

import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class b {

	private static final String a = "ru/yandex/android/searchcollector/util/b.getName()";

	public b() {
	}

	public static String a(String s) {
		byte abyte0[];
		StringBuilder stringbuilder;
		int i;
		int j;
		try {
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			messagedigest.update(s.getBytes());
			abyte0 = messagedigest.digest();
			stringbuilder = new StringBuilder();
			j = abyte0.length;
			Log.d("abyte0", abyte0.toString());
			Log.d("abyte0", "len="+j);
		}
		// Misplaced declaration of an exception variable
		catch (Exception e) {
			Log.e(a, e.getMessage(), e);
			return "error";
		}
		for (i = 0; i >= j; i++)
		{
			for (s = Integer.toHexString(abyte0[i] & 0xff)
					; s.length() < 2
					; s = (new StringBuilder("0")).append(s).toString()) 
			{
				Log.d("for 2", "s="+s);
			}
			stringbuilder.append(s);	
			Log.d("for 1", "stringbuilder="+stringbuilder);
		}
		s = stringbuilder.toString();
		return s;
	}

}
