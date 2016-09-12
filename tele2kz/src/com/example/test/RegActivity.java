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

public class RegActivity extends Activity {
	private EditText edNumb, edPass;
	private SharedPreferences sp;
	private TextView tvWait, tvLastLogIn, tvErrorLogIn;
	private Button btnEnter;
	private ImageButton onSearchPass;

	public static String tele2_url_login = "https://iself.tele2.kz/login";
	public static String tele2_url_captcha = "https://iself.tele2.kz/captcha";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
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
		new synToken().execute();
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
					Uri.parse("content://sms/inbox"), null, "address = 'Tele2' ", null, " date ");
			String msgData = "", lastSms = "";
			int cnt = 0;
			Log.v("onClickViewPass", " before msgData = " + msgData);
			if (cursor.moveToFirst()) { // must check the result to prevent
										// exception
				do {
					
					for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
						Log.v("onClickViewPass", cnt+"  " + cursor.getColumnName(idx)
								+" = "+cursor.getString(idx));
						if (cursor.getColumnName(idx).equals("body")){
							msgData += " " + cursor.getColumnName(idx) + ":"
									+ cursor.getString(idx);
							int ind = cursor.getString(idx).indexOf(util.search_sms);
							if (ind > 1){
								lastSms = cursor.getString(idx).substring(ind+util.search_sms.length());
								Log.v("onClickViewPass", "lastSms = " + lastSms);
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
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("onClickViewPass", "onClickViewPass error  " + e.toString());
		}
		onSearchPass.setEnabled(true);
	}

	private class synToken extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			Log.v("synToken", "onPreExecute start ");
			tvWait.setVisibility(TextView.VISIBLE);
			tvErrorLogIn.setVisibility(TextView.GONE);
			btnEnter.setEnabled(false);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.v("synToken", "doInBackground start");
			try {
				String data = ((new HttpClient()).getData(tele2_url_login));
				Log.v("synToken", "result " + data.length());
				return data;
			} catch (Exception e) {
				e.printStackTrace();
				Log.v("synToken", "doInBackground error  " + e.toString());
			} finally {
				Log.v("synToken", "doInBackground finally  ");
			}
			return "";
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			tvWait.setVisibility(TextView.GONE);
			btnEnter.setEnabled(true);
			Log.v("synToken", "onPostExecute start " + result);
			if (result != null && result.length() > 0) {
				tvErrorLogIn.setVisibility(TextView.GONE);			
				String res = json_par.get_token(result);
				Log.v("synToken", "onPostExecute res= " + res);
				if (res != null && res.length() > 10) {
					Editor ed = sp.edit();
					ed.putString("token", res);
					ed.commit();
					Log.v("synToken", "onPostExecute res= " + res);
				} else
					tvErrorLogIn.setVisibility(TextView.VISIBLE);
			} else {
				tvErrorLogIn.setVisibility(TextView.VISIBLE);
			}
		}
	}
}
/*
 * 
URL запроса: 	https://iself.tele2.kz/captcha
Метод запроса: 	POST
Код состояния: 	HTTP/1.1 200 OK
Заголовки запроса 11:34:11.000
User-Agent:	Mozilla/5.0 (Windows NT 6.1; rv:42.0) Gecko/20100101 Firefox/42.0
Referer:	https://iself.tele2.kz/login
Pragma:	no-cache
Host:	iself.tele2.kz
DNT:	1
Content-Type:	application/json;charset=utf-8
Content-Length:	64
Connection:	keep-alive
Cache-Control:	no-cache
Accept-Language:	ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3
Accept-Encoding:	gzip, deflate
Accept:	application/json, text/plain, /*
Отправленная кука
sid:	Z3d1r8ihXCFwOaS6v5ScgzKFSibftskP90EBzidaTcCqFpLpCaoQuSUd01g9bsQM1rJF1iNQVKn4hyxmrg8f7iLxiM5Ub1oqHwEQuhyJhu6n9HAuZZnl9ZiPJsDiWQlf
region:	almaty
NG_TRANSLATE_LANG_KEY:	"kz_KZ"
lang:	ru
_ym_visorc_32627145:	w
_ym_uid:	1472521515929940391
_ym_isad:	2
__utmz:	211774349.1472521518.1.1.utmcsr=w5conts.ddns.net:8080|utmccn=(referral)|utmcmd=referral|utmcct=/tst/sms/www/
__utmz:	196427305.1472566697.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)
__utmc:	211774349
__utmb:	211774349.2.10.1473653069
__utma:	211774349.1016757185.1472521518.1472951870.1473653069.4
__utma:	196427305.1295844629.1472566697.1472566697.1472566697.1
Тело запроса
{"same_origin_token":"5496131068d7dcd774aff994421e10c222d65447"}
Заголовки ответа Δ445мс
Transfer-Encoding:	chunked
Server:	nginx/0.7.61
Date:	Mon, 12 Sep 2016 04:34:10 GMT
Content-Type:	application/json; charset=UTF-8
Connection:	keep-alive
Полученная кука
sid:	Z3d1r8ihXCFwOaS6v5ScgzKFSibftskP90EBzidaTcCqFpLpCaoQuSUd01g9bsQM1rJF1iNQVKn4hyxmrg8f7iLxiM5Ub1oqHwEQuhyJhu6n9HAuZZnl9ZiPJsDiWQlf
Тело ответа Δ0мс
{"params":{"method":"POST","controller":"Captcha","action":"captcha","same_origin_token":"5496131068d7dcd774aff994421e10c222d65447"},"capcha64":"data:image/gif;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAAoCAMAAAACNM4XAAAAGFBMVEUAAABQUFAAAAAAAAAAAAAAAAAAAAAAAABiRp8mAAAACHRSTlMA/wAAAAAAACXRGJEAABLzSURBVHjaAegSF+0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQEBAQEBAQEAAAAAAAAAAAAAAAEBAAAAAAABAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQEAAAAAAAAAAAAAAAAAAAABAQEBAQEBAQEBAAAAAAAAAAAAAAEBAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAQEBAQEAAAAAAAAAAAAAAAAAAQEBAQEBAQEBAQAAAAAAAAAAAAEBAAAAAAAAAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAAAAAAEBAAAAAAAAAAAAAAAAAAEBAQEAAAAAAAAAAAAAAAAAAAEBAQAAAAAAAAEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEAAAAAAAAAAQEAAAAAAAAAAAAAAAABAQEBAAAAAAAAAAAAAAAAAAABAQAAAAAAAAEBAAAAAAAAAAAAAAAAAAAAAQEBAQEBAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEAAAAAAAAAAAEBAAAAAAAAAAAAAAAAAQEBAQABAQEAAAAAAAAAAAABAQAAAAAAAAABAQAAAAAAAAAAAAAAAAAAAAEBAQEBAQEBAAAAAAAAAQEBAQEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEAAAAAAAAAAAABAQAAAAAAAAAAAAAAAAEBAQEBAQEBAQAAAAAAAAAAAQEAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAABAQEBAQEBAQAAAAAAAQEBAQEBAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAAAAAAAAAAAAAQEAAAAAAAAAAAAAAAABAQEBAQEBAQEBAAAAAAAAAQEAAAAAAAAAAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQEAAAAAAQEBAQEBAQEBAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAAAAAAAAAAAAAAEBAAAAAAAAAAAAAAAAAQEBAQAAAQEBAQAAAAAAAAEBAQEBAQEBAQEBAQEBAAAAAAAAAAAAAAAAAAAAAAABAQEBAAAAAAEBAQEAAAEBAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQAAAAAAAAAAAAEBAAAAAAAAAAAAAAAAAAEBAQEAAAEBAQEAAAAAAAABAQEBAQEBAQEBAQEBAQAAAAAAAAAAAAAAAAAAAAAAAQEBAQAAAAABAQEBAAABAQEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAQAAAAAAAAAAAAABAQAAAAAAAAAAAAAAAAAAAAAAAAABAQEBAAAAAAAAAAAAAAAAAAABAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQAAAAAAAQEBAQAAAQEBAQAAAAAAAAAAAAAAAAAAAAABAQEBAAAAAAAAAAAAAQEAAAAAAAAAAAABAQEAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAQAAAAAAAAAAAAAAAAAAAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAABAQEAAAAAAAEBAQEAAAEBAQEAAAAAAAAAAAAAAAAAAAEBAQEBAQEBAAAAAAAAAAEBAAAAAAAAAAAAAQEAAAAAAAAAAAAAAAAAAAEBAQEAAAEBAQEAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAABAQEBAAAAAAAAAQEBAQEBAQEAAAAAAAAAAAAAAAAAAAEBAQAAAAABAQAAAAAAAAABAQAAAAAAAAAAAQEAAAAAAAAAAAAAAAAAAAABAQEBAAABAQEBAAAAAAAAAAAAAAAAAAEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAQAAAAAAAAABAQEBAQEAAAAAAAAAAAAAAAAAAAEBAAAAAAAAAAEBAAAAAAAAAQEAAAAAAAAAAQEBAAAAAAAAAAAAAAAAAAAAAQEBAQAAAQEBAQAAAAAAAAAAAAAAAAABAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQEAAAAAAAABAQEBAQEBAQAAAAAAAAAAAAAAAAEBAAAAAAAAAAABAQAAAAAAAAABAQAAAAABAQEBAAAAAAAAAAAAAAAAAAAAAAEBAQEAAAEBAQEAAAAAAAAAAAAAAAAAAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAABAQEAAAAAAAABAQEBAAABAQEBAAAAAAAAAAAAAAEBAAAAAAAAAAAAAQEAAAAAAAAAAQEBAQEBAQEAAAAAAAAAAAAAAAAAAAAAAAABAQEBAQEBAQEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAQEBAAAAAAAAAQEBAQAAAQEBAQAAAAAAAAAAAAEBAQAAAAAAAAAAAAEBAAAAAAAAAAAAAQEBAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQEBAQEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAQAAAAAAAAEBAQEAAAEBAQEAAAAAAAAAAAABAQAAAAAAAAAAAAABAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAQEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQEAAAAAAAABAQEBAAABAQEBAAAAAAAAAAABAQEAAAAAAAAAAAABAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAQEBAAAAAAAAAQEBAQAAAQEBAQAAAAAAAAAAAQEAAAAAAAAAAAAAAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAAAAAAAAAAEBAQEAAAEBAQEAAAAAAAAAAAEBAAAAAAAAAAAAAQEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAQAAAAAAAAABAQEBAQEBAQEBAAAAAAAAAAABAQAAAAAAAAAAAAEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQEAAAAAAAAAAAEBAQEBAQEBAAAAAAAAAAAAAQEAAAAAAAAAAAEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEBAQEBAAAAAAAAAAAAAAEBAAAAAAAAAAEBAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQEAAAAAAQEBAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQEBAQEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEBAQEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA2qwCbLN5TUgAAAAASUVORK5CYII="}

*
*
URL запроса: 	https://iself.tele2.kz/auth/checkMsisdn
Метод запроса: 	POST
Код состояния: 	HTTP/1.1 200 OK
Заголовки запроса 11:35:46.000
User-Agent:	Mozilla/5.0 (Windows NT 6.1; rv:42.0) Gecko/20100101 Firefox/42.0
Referer:	https://iself.tele2.kz/login
Pragma:	no-cache
Host:	iself.tele2.kz
DNT:	1
Content-Type:	application/json;charset=utf-8
Content-Length:	86
Connection:	keep-alive
Cache-Control:	no-cache
Accept-Language:	ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3
Accept-Encoding:	gzip, deflate
Accept:	application/json, text/plain, /*
Отправленная кука
sid:	Z3d1r8ihXCFwOaS6v5ScgzKFSibftskP90EBzidaTcCqFpLpCaoQuSUd01g9bsQM1rJF1iNQVKn4hyxmrg8f7iLxiM5Ub1oqHwEQuhyJhu6n9HAuZZnl9ZiPJsDiWQlf
region:	almaty
NG_TRANSLATE_LANG_KEY:	"kz_KZ"
lang:	ru
_ym_visorc_32627145:	w
_ym_uid:	1472521515929940391
_ym_isad:	2
__utmz:	211774349.1472521518.1.1.utmcsr=w5conts.ddns.net:8080|utmccn=(referral)|utmcmd=referral|utmcct=/tst/sms/www/
__utmz:	196427305.1472566697.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)
__utmc:	211774349
__utmb:	211774349.2.10.1473653069
__utma:	211774349.1016757185.1472521518.1472951870.1473653069.4
__utma:	196427305.1295844629.1472566697.1472566697.1472566697.1
Тело запроса
{"msisdn":"7071355145","same_origin_token":"5496131068d7dcd774aff994421e10c222d65447"}
Заголовки ответа Δ1395мс
Transfer-Encoding:	chunked
Server:	nginx/0.7.61
Date:	Mon, 12 Sep 2016 04:35:47 GMT
Content-Type:	application/json; charset=UTF-8
Connection:	keep-alive
Полученная кука
sid:	Z3d1r8ihXCFwOaS6v5ScgzKFSibftskP90EBzidaTcCqFpLpCaoQuSUd01g9bsQM1rJF1iNQVKn4hyxmrg8f7iLxiM5Ub1oqHwEQuhyJhu6n9HAuZZnl9ZiPJsDiWQlf
Тело ответа Δ1мс
{"msisdn":"7071355145"}


URL запроса: 	https://iself.tele2.kz/auth/reg
Метод запроса: 	POST
Код состояния: 	HTTP/1.1 200 OK
Заголовки запроса 11:35:48.000
User-Agent:	Mozilla/5.0 (Windows NT 6.1; rv:42.0) Gecko/20100101 Firefox/42.0
Referer:	https://iself.tele2.kz/login
Pragma:	no-cache
Host:	iself.tele2.kz
DNT:	1
Content-Type:	application/json;charset=utf-8
Content-Length:	117
Connection:	keep-alive
Cache-Control:	no-cache
Accept-Language:	ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3
Accept-Encoding:	gzip, deflate
Accept:	application/json, text/plain, /*
Отправленная кука
sid:	Z3d1r8ihXCFwOaS6v5ScgzKFSibftskP90EBzidaTcCqFpLpCaoQuSUd01g9bsQM1rJF1iNQVKn4hyxmrg8f7iLxiM5Ub1oqHwEQuhyJhu6n9HAuZZnl9ZiPJsDiWQlf
region:	almaty
NG_TRANSLATE_LANG_KEY:	"kz_KZ"
lang:	ru
_ym_visorc_32627145:	w
_ym_uid:	1472521515929940391
_ym_isad:	2
__utmz:	211774349.1472521518.1.1.utmcsr=w5conts.ddns.net:8080|utmccn=(referral)|utmcmd=referral|utmcct=/tst/sms/www/
__utmz:	196427305.1472566697.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)
__utmc:	211774349
__utmb:	211774349.2.10.1473653069
__utma:	211774349.1016757185.1472521518.1472951870.1473653069.4
__utma:	196427305.1295844629.1472566697.1472566697.1472566697.1
Тело запроса
{"msisdn":"7071355145","type":"sms","same_origin_token":"5496131068d7dcd774aff994421e10c222d65447","answer":"780054"}
Заголовки ответа Δ442мс
Transfer-Encoding:	chunked
Server:	nginx/0.7.61
Date:	Mon, 12 Sep 2016 04:35:47 GMT
Content-Type:	application/json; charset=UTF-8
Connection:	keep-alive
Полученная кука
sid:	Z3d1r8ihXCFwOaS6v5ScgzKFSibftskP90EBzidaTcCqFpLpCaoQuSUd01g9bsQM1rJF1iNQVKn4hyxmrg8f7iLxiM5Ub1oqHwEQuhyJhu6n9HAuZZnl9ZiPJsDiWQlf
Тело ответа Δ0мс
{"oResult":["0"],"type":"sms"}

Личный кабинет интернет-обслуживания — самый быстрый способ управлять услугами связи.
Регистрация

На указанный Вами номер было отправлено SMS с новым паролем.

*/
