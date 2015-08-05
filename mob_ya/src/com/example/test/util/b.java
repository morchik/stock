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
			Log.d(a, abyte0.toString());
		}
		// Misplaced declaration of an exception variable
		catch (Exception e) {
			Log.e(a, e.getMessage(), e);
			return "";
		}
		i = 0;
		if (i >= j) {
			;// break; /* Loop/switch isn't completed */
		}
		for (s = Integer.toHexString(abyte0[i] & 0xff); s.length() < 2; s = (new StringBuilder(
				"0")).append(s).toString()) {
		}
		stringbuilder.append(s);
		i++;
		/*
		 * if (true) goto _L2 else goto _L1; _L2: break MISSING_BLOCK_LABEL_33;
		 * _L1:
		 */
		s = stringbuilder.toString();
		return s;
	}

}
