package com.example.test;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import kz.alfa.map.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.example.test.util.Log;
import android.view.View;

public class AboutActivity extends Activity {
	private SharedPreferences sp;
	//private TextView tvAbout;

	public static String tele2_url_check = "http://tele2.redirectme.net/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		sp = PreferenceManager.getDefaultSharedPreferences(this);

		//tvAbout = (TextView) findViewById(R.id.tvAbout);
	}

	public void click_close(View view) {
		finish();
	}

	public void click_update(View view) {
		new synLogIn().execute();
		SendSmsActivity.doAd = null;
	}

	
	private class synLogIn extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			Log.v("synLogIn", "onPreExecute start ");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.v("synLogIn", "doInBackground start");
			try {
				CookieManager cookieManager = new CookieManager();
				CookieHandler.setDefault(cookieManager);
				String data = ((new HttpClient()).getPOSTAJAX(
						tele2_url_check,
						json_par.create_auth(sp.getString("edNumb", ""),
								sp.getString("edPass", ""))));
				Log.v("synLogIn", "result " + data);
				return data;
			} catch (Exception e) {
				e.printStackTrace();
				Log.v("synLogIn", "doInBackground error  " + e.toString());
			} finally {
				Log.v("synLogIn", "doInBackground finally  ");
			}
			return "";
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.v("synLogIn", "onPostExecute start " + result);
			if (result != null && result.length() > 0) {
				String res = json_par.test_a(result);
				Log.v("synLogIn", "onPostExecute res= " + res);
				if (res.equals("true")) {
					Editor ed = sp.edit();
					String Dtime = new SimpleDateFormat("MM.dd HH:mm")
							.format(new Date());
					ed.putString("tvLastLogIn", Dtime);
					ed.commit();

					onResume();
					startActivity(new Intent(getBaseContext(),
							SendSmsActivity.class));
				} else
					; //tvErrorLogIn.setVisibility(TextView.VISIBLE);
			} else {
				//tvErrorLogIn.setVisibility(TextView.VISIBLE);
			}
		}
	}
}
