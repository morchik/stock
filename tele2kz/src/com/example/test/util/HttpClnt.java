package com.example.test.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

public class HttpClnt {
	public static final String tele2_url_form = "http://www.almaty.tele2.kz/ru/private_clients/SmsForm.aspx";

	// imitate chrome
	public void setRequestProperty(HttpURLConnection con) {
		con.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		con.setRequestProperty("Accept-Encoding", "deflate");
		con.setRequestProperty("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
		con.setRequestProperty("Cache-Control", "no-cache");
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("DNT", "1");
		con.setRequestProperty("Pragma", "no-cache");
		con.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.131 Safari/537.36");
	}

	public String getData(String l_url) {
		HttpURLConnection con = null;
		InputStream is = null;

		try {
			con = (HttpURLConnection) (new URL(l_url)).openConnection();
			con.setRequestMethod("GET");
			setRequestProperty(con);
			con.setDoInput(true);
			con.setDoOutput(false);
			con.setConnectTimeout(60000);
			con.connect();
			int stt = con.getResponseCode();
			// Log.v("HttpClient", "html getResponseCode " + stt+" "+l_url);
			StringBuffer buffer = new StringBuffer();
			if (stt == 200) {
				is = con.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null)
					buffer.append(line + "\r\n");

				is.close();
				con.disconnect();
			}
			Log.v("HttpClnt",
					"html getResponseCode "
							+ stt
							+ " "
							+ l_url
							+ " data: "
							+ buffer.toString().substring(0,
									Math.min(buffer.length(), 120)));
			return buffer.toString();
		} catch (Throwable t) {
			t.printStackTrace();
			Log.e("HttpClient html", l_url + " error " + t.toString());
			try {
			} catch (Throwable e) {
				e.printStackTrace();
				Log.e("HttpClient http updUS ",
						l_url + " error " + e.toString());
			}
		} finally {
			try {
				is.close();
			} catch (Throwable t) {
			}
			try {
				con.disconnect();
			} catch (Throwable t) {
			}
		}
		return null;
	}

}
