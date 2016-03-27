package com.ktkt.crpt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String getMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			// Now we need to zero pad it if you actually want the full 32
			// chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getYaMD5(String str) {
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");
			instance.update(str.getBytes());
			byte[] digest = instance.digest();
			StringBuilder stringBuilder = new StringBuilder();
			for (byte b : digest) {
				String toHexString = Integer.toHexString(b & 255);
				while (toHexString.length() < 2) {
					toHexString = "0" + toHexString;
				}
				stringBuilder.append(toHexString);
			}
			return stringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getSaltOtp(String saltStr) {
		return getMD5(saltStr);
	}

	public static String getOtp(String str, String str2) {
		String str3 = String
				.valueOf(((System.currentTimeMillis() / 1000) - 5) / 10)
				+ str
				+ str2;
		//System.out.println("getOtp [getPassword]: salt=" + str3);
		return getSaltOtp(str3).substring(0, 6);
	}

	public static void main() throws NoSuchAlgorithmException {
		System.out.println("MD5: ");
		System.out.println(getMD5("Javarmi.com"));
		System.out.println(getOtp("eb4777777thoew4a", "7777"));
	}
}
