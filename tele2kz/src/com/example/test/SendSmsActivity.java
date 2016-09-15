package com.example.test;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.example.test.util.MyRsa;
import com.example.test.util.util;

import kz.alfa.map.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import com.example.test.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizLogLevel;

public class SendSmsActivity extends Activity {
	private EditText edSend, edText;
	private SharedPreferences sp;
	private TextView tvSmsLeft, tvCharLeft, tvErrorSend, tvLastSent,
			tvWaitSend;
	private Button btnSend;

	public static String tele2_url_send = "http://www.almaty.tele2.kz/WebServices/smsService.asmx/SendSms";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_sms);
		sp = PreferenceManager.getDefaultSharedPreferences(this);

		edSend = (EditText) findViewById(R.id.edSend);
		edText = (EditText) findViewById(R.id.edText);

		tvSmsLeft = (TextView) findViewById(R.id.tvSmsLeft);
		tvCharLeft = (TextView) findViewById(R.id.tvCharLeft);
		tvErrorSend = (TextView) findViewById(R.id.tvErrorSend);
		tvLastSent = (TextView) findViewById(R.id.tvLastSent);
		tvWaitSend = (TextView) findViewById(R.id.tvWaitSend);

		btnSend = (Button) findViewById(R.id.btnSend);

		edText.addTextChangedListener(new SmsTextWatcher());
		new synCheck().execute();

		AdBuddiz.setPublisherKey("c3cf0cc9-0ce4-4e55-8148-341c5ec11e1d"); // adisha
		AdBuddiz.setTestModeActive();
		AdBuddiz.setLogLevel(AdBuddizLogLevel.Info);
		AdBuddiz.cacheAds(this);
	}

	// log in start process
	public void click(View view) {
		onPause();
		if (util.left_sms > 0)
			new synSend().execute();
		else
			adv();
		// startActivity(new Intent(getBaseContext(),
		// FullscreenActivity.class));
	}

	// log out start process
	public void click_logout(View view) {
		// forgot all sessions
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		finish();
	}

	protected void onResume() {
		edSend.setText(sp.getString("edSend", ""));
		edText.setText(sp.getString("edText", ""));
		String ll = sp.getString("tvLastSent", "");
		if (ll.length() > 1) {
			tvLastSent.setText(getString(R.string.label_last_sent) + ll);
			tvLastSent.setVisibility(TextView.VISIBLE);
		}
		String sl = sp.getString("tvSmsLeft", "");
		if (sl.length() >= 1) {
			tvSmsLeft.setText(getString(R.string.label_sms_left) + sl);
			tvSmsLeft.setVisibility(TextView.VISIBLE);
		}
		super.onResume();
	}

	protected void onPause() {
		Editor ed = sp.edit();
		ed.putString("edSend", edSend.getText().toString());
		ed.putString("edText", edText.getText().toString());
		ed.commit();
		super.onPause();
	}

	private class SmsTextWatcher implements TextWatcher {
		public void afterTextChanged(Editable s) {
			String text = s.toString();
			// Log.v("synSend", "addTextChangedListener afterTextChanged " +
			// text);
			tvCharLeft.setText(getString(R.string.label_chars_left)
					+ (160 - (text.length())));
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	}

	public void adv() {
		AdvTask advTask = new AdvTask();
		advTask.a = this;
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(advTask,
				Math.round(1000 * 6 * 60 * Math.random()),
				Math.round(60 * 60 * 1000 * Math.random()));
	}

	private class AdvTask extends TimerTask {
		public Activity a;

		public void run() {
			if (AdBuddiz.isReadyToShowAd(a)) {
				AdBuddiz.showAd(a);
			}
		}
	}

	private class synSend extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			Log.v("synSend", "onPreExecute start ");
			tvWaitSend.setVisibility(TextView.VISIBLE);
			tvLastSent.setVisibility(TextView.GONE);
			tvErrorSend.setVisibility(TextView.GONE);
			btnSend.setEnabled(false);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.v("synSend", "doInBackground start");
			try {
				String data = ((new HttpClient()).getPOSTAJAX(
						tele2_url_send,
						json_par.create_sms(sp.getString("edSend", ""),
								util.calc_t(sp.getString("edText", "")))));
				Log.v("synSend", "result " + data);
				return data;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			tvWaitSend.setVisibility(TextView.GONE);
			btnSend.setEnabled(true);
			Editor ed = sp.edit();
			Log.v("synSend", "onPostExecute start " + result);
			if (result != null && result.length() > 0) {
				String sl = json_par.get_AmountSmsLeft(result);
				if (sl.indexOf("error") == -1) {
					ed.putString("tvSmsLeft", util.calc_m(sl));
				}
				String res = json_par.test_s(result);
				Log.v("synSend", "onPostExecute res= " + res);
				if (res.equals("true")) {
					String Dtime = new SimpleDateFormat("MM.dd HH:mm")
							.format(new Date());
					ed.putString("tvLastSent", Dtime);
					save_sms(
							sp.getString("edText", "") + " <-("
									+ sp.getString("lastNumb", "") + " " + sl
									+ ")", sp.getString("edSend", ""));

					adv();
				} else {
					tvErrorSend.setVisibility(TextView.VISIBLE);
					ed.putString("tvLastSent", "");
					ed.putString("tvSmsLeft", "");
				}
			} else {
				tvErrorSend.setVisibility(TextView.VISIBLE);
				ed.putString("tvLastSent", "");
				ed.putString("tvSmsLeft", "");
			}
			ed.commit();
			onResume();
		}
	}

	public void save_sms(String sms_text, String sms_number) {
		final String SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19 = "content://sms/sent";
		try{
		ContentValues values = new ContentValues();
		values.put("address", sms_number);
		values.put("body", sms_text);
		this.getContentResolver().insert(
				Uri.parse(SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19), values);
		} catch (Exception e){
			Log.e("save_sms", e.toString());
			e.printStackTrace();
		}
	}

	// check sms left
	private class synCheck extends AsyncTask<Void, Void, String> {
		public static final String tele2_url_form = "http://www.almaty.tele2.kz/ru/private_clients/SmsForm.aspx";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.v("synCheck", "doInBackground start");
			try {
				String data = ((new HttpClient()).getData(tele2_url_form));
				Log.v("synCheck", "result length" + data.length());
				return data;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				Editor ed = sp.edit();
				if (result != null && result.length() > 10) {
					int idx = result.indexOf("amountSmsLeft");
					if (idx > 1) {
						Log.v("synSend", "onPostExecute amountSmsLeft "
								+ result.substring(idx, idx + 30));
						idx = result.indexOf(">", idx) + 1;
						int end_idx = result.indexOf("<", idx);
						String sl = result.substring(idx, end_idx);

						ed.putString("tvSmsLeft", util.calc_m(sl));
					} else {
						ed.putString("tvSmsLeft", "");
						click_logout(null);
					}
				} else {
					ed.putString("tvSmsLeft", "");
				}
				ed.commit();
				onResume();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
