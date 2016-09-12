package com.example.test;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.test.util.util;

import kz.alfa.map.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import com.example.test.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private EditText edNumb, edPass;
	private SharedPreferences sp;
	private TextView tvWait, tvLastLogIn, tvErrorLogIn;
	private Button btnEnter;
	private ImageButton onSearchPass;

	public static String tele2_url_auth = "http://www.almaty.tele2.kz/WebServices/authenticate.asmx/Authenticate";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.context = getBaseContext(); 
		setContentView(R.layout.activity_login);
		sp = PreferenceManager.getDefaultSharedPreferences(this);

		edNumb = (EditText) findViewById(R.id.edNumb);
		edPass = (EditText) findViewById(R.id.edPass);

		tvWait = (TextView) findViewById(R.id.tvWait);
		tvLastLogIn = (TextView) findViewById(R.id.tvLastLogIn);
		tvErrorLogIn = (TextView) findViewById(R.id.tvErrorLogIn);
		btnEnter = (Button) findViewById(R.id.btn_enter);
		onSearchPass = (ImageButton) findViewById(R.id.onSearchPass);
		
		// ModuleEngine.load_all("", getBaseContext() );
		// java.net.URLClassLoader("http://192.168.8.228/tst/class/ModulePrinter.class");
		// (new
		// ModuleEngine()).save_url("http://192.168.8.228/tst/class/ModulePrinter.class",
		// getBaseContext() );
	}

	// log in start process
	public void click(View view) {
		onPause();
		new synLogIn().execute();
	}

	protected void onResume() {
		edNumb.setText(sp.getString("edNumb", ""));
		edPass.setText(sp.getString("edPass", ""));
		String ll = sp.getString("tvLastLogIn", "");
		if (ll.length() > 0)
			tvLastLogIn.setText(getString(R.string.label_last_login) + ll);
		tvLastLogIn.setVisibility(TextView.VISIBLE);
		onClickViewPass(null);
		super.onResume();
	}

	protected void onPause() {
		Editor ed = sp.edit();
		ed.putString("edNumb", edNumb.getText().toString());
		ed.putString("edPass", edPass.getText().toString());
		ed.commit();
		super.onPause();
	}

	public void onClickViewPass(View v) {
		if (edPass.getTransformationMethod() != PasswordTransformationMethod
				.getInstance())
			edPass.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		else
			edPass.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
	}
	
	public void onClickSearchPass(View v) {
		onSearchPass.setEnabled(false);
		//if (edPass.getText().toString().length() == 0) 
		try {
			// public static final String INBOX = "content://sms/inbox";
			// public static final String SENT = "content://sms/sent";
			// public static final String DRAFT = "content://sms/draft";
			Cursor cursor = getContentResolver().query(
					Uri.parse("content://sms/inbox"), null, "address = 'Tele2' and body like ? "
						, new String[]{"%" + util.search_sms + "%"}, " date ");
			String lastSms = "";
			int cnt = 0;
			if (cursor.moveToFirst()) { // must check the result to prevent
				do {
					for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
						if (cursor.getColumnName(idx).equals("body")){
							Log.v("onClickViewPass", cnt+"  " + cursor.getColumnName(idx)
									+" = "+cursor.getString(idx));
							int ind = cursor.getString(idx).indexOf(util.search_sms);
							if (ind > 1){
								lastSms = cursor.getString(idx).substring(ind+util.search_sms.length());
								Log.v("onClickViewPass", "lastSms body = " + lastSms);
							}
						}
					}
					cnt++;
					
				} while (cursor.moveToNext() || cnt > 10);
				Log.v("onClickViewPass", "lastSms = " + lastSms);
				if (lastSms.length()>=4)
					edPass.setText(lastSms);
			} else {
				// empty box, no SMS
				 startActivity(new Intent(getBaseContext(), RegActivity.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("onClickViewPass", "onClickViewPass error  " + e.toString());
		}
		onSearchPass.setEnabled(true);
	}

	private class synLogIn extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			Log.v("synLogIn", "onPreExecute start ");
			tvWait.setVisibility(TextView.VISIBLE);
			tvErrorLogIn.setVisibility(TextView.GONE);
			btnEnter.setEnabled(false);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.v("synLogIn", "doInBackground start");
			try {
				CookieManager cookieManager = new CookieManager();
				CookieHandler.setDefault(cookieManager);
				String data = ((new HttpClient()).getPOSTAJAX(
						tele2_url_auth,
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
			tvWait.setVisibility(TextView.GONE);
			btnEnter.setEnabled(true);
			Log.v("synLogIn", "onPostExecute start " + result);
			if (result != null && result.length() > 0) {
				tvErrorLogIn.setVisibility(TextView.GONE);
				String res = json_par.test_a(result);
				Log.v("synLogIn", "onPostExecute res= " + res);
				if (res.equals("true")) {
					Editor ed = sp.edit();
					ed.putString("lastNumb", edNumb.getText().toString());
					ed.putString("LastPass", edPass.getText().toString());
					String Dtime = new SimpleDateFormat("MM.dd HH:mm")
							.format(new Date());
					ed.putString("tvLastLogIn", Dtime);
					ed.commit();

					onResume();
					startActivity(new Intent(getBaseContext(),
							SendSmsActivity.class));
				} else
					tvErrorLogIn.setVisibility(TextView.VISIBLE);
			} else {
				tvErrorLogIn.setVisibility(TextView.VISIBLE);
			}
		}
	}
}
