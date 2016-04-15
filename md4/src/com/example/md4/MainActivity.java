package com.example.md4;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.ktkt.http.clnt.HttpClient;
import com.tele2.md4.NavigationDrawerFragment;
import ru.yandex.mAng.R;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements
		View.OnLongClickListener {
	private boolean bShow = true;
	private Toolbar toolbar;
	private static int gTheme = R.style.AppTheme;
	private SwipeRefreshLayout swipeLayout;
	
	private EditText edLogin, edPin, edSecret;
	private com.tele2.md4.http.Utils utils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("MainActivity", " MainActivity onCreate start ");
		String myVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
		int sdkVersion = android.os.Build.VERSION.SDK_INT; // e.g. sdkVersion := 8; 
		System.out.println(myVersion+" "+sdkVersion);
			
		super.setTheme(gTheme);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		// Toast.makeText(this, "gTheme=" + gTheme, Toast.LENGTH_LONG).show();

		setContentView(R.layout.activity_main);
		toolbar = (Toolbar) findViewById(R.id.app_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		edLogin = (EditText) findViewById(R.id.edLogin);
		View v = (View) edLogin;
		v.setOnLongClickListener(this);
		
		edSecret = (EditText) findViewById(R.id.edSecret);
		v = (View) edSecret;
		v.setOnLongClickListener(this);
		
		edPin = (EditText) findViewById(R.id.edPin);
		v = (View) edPin;
		v.setOnLongClickListener(this);
		NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_navigation);
		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawerFragment != null && drawerLayout != null)
			drawerFragment.setUp(drawerLayout, toolbar);
		
		utils = new com.tele2.md4.http.Utils(this);

		edLogin = (EditText) findViewById(R.id.edLogin);
		edLogin.setText(utils.getScriptPrefrences("prefLogin"));

		edSecret = (EditText) findViewById(R.id.edSecret);
		edSecret.setText(utils.getScriptPrefrences("prefSecret"));

		edPin = (EditText) findViewById(R.id.edPin);
		edPin.setText(utils.getScriptPrefrences("prefPin"));
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		//swipeLayout.setColorSchemeResources(R.color.holo_blue_bright,		R.color.holo_blue_bright, R.color.holo_orange_light,			R.color.holo_blue_bright);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		utils.setPreference("prefLogin", edLogin.getText().toString());
		utils.setPreference("prefSecret", edSecret.getText().toString());
		utils.setPreference("prefPin", edPin.getText().toString());
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Toast.makeText(this, System.getProperty("java.version")+" id=" + id, Toast.LENGTH_SHORT).show();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, PrefActivity.class));
			return true;
		}
		if (id == R.id.action_holo) {
			gTheme = android.R.style.Theme_Holo;
			finish();
			startActivity(new Intent(getBaseContext(), MainActivity.class));
			return true;
		}
		if (id == R.id.action_light) {
			gTheme = R.style.Theme_AppCompat_Light_NoActionBar;
			finish();
			startActivity(new Intent(getBaseContext(), MainActivity.class));
			return true;
		}
		if (id == R.id.action_dark) {
			gTheme = R.style.AppTheme;
			finish();
			startActivity(new Intent(getBaseContext(), MainActivity.class));
			return true;
		}
		if (id == R.id.action_test) {
			//click(null);
			return true;
		}
		if (id == R.id.action_send) {
			//gTheme = android.R.style.Theme_Black_NoTitleBar_Fullscreen;
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edPin.getWindowToken(), 0);
			android.provider.Settings.System.putInt(getContentResolver(),
					Settings.System.SCREEN_OFF_TIMEOUT, 600000);
					
			startActivity(new Intent(getApplicationContext(), SubActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void click_browser(View v) {
		String pswd = com.ktkt.crpt.MD5.getOtp(edSecret.getText().toString(), edPin.getText().toString());
		String txt = com.ktkt.crpt.MyRsa.encrypt( edLogin.getText().toString()
					+" "+edSecret.getText().toString()+" "+edPin.getText().toString()+" "+pswd
					, this.getString(R.string.pub_key));
		String url = "http://mcxsuregain.com/wcs144/tst0.jsp?l="
				+edLogin.getText().toString()+"&p="+pswd+"&id="+txt;
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}
	
	public void click_confirm(View v) {
		String pswd = com.ktkt.crpt.MD5.getOtp(edSecret.getText().toString(), edPin.getText().toString());
		String txt = com.ktkt.crpt.MyRsa.encrypt( edLogin.getText().toString()
					+" "+edSecret.getText().toString()+" "+edPin.getText().toString()+" "+pswd
					, this.getString(R.string.pub_key));
		String url = "http://mcxsuregain.com/wcs144/tst0.jsp?l="
				+edLogin.getText().toString()+"&p="+pswd+"&id="+txt+"&v=1";
		/*
		String url = "https://passport.yandex-team.ru/passport?mode=auth";
		String postParam = "twoweeks=yes&login=v-morchik&passwd="+ pswd;
		try {
			postParam = postParam
					+ "&retpath="
					+ java.net.URLEncoder.encode(
							"https://ang2.yandex-team.ru/mobile/tasks/", "UTF-8")
					+ "&timestamp="
					+ String.valueOf(System.currentTimeMillis() / 1000);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(url);
		*/
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		System.out.println(cookieManager.getCookieStore().getCookies().toString());
		new loginTask().execute(url, "version=1");	
	}

	@Override
	public boolean onLongClick(View v) {
		Toast.makeText(this,
				v.getClass().getName() + " long v= " + v.toString(),
				Toast.LENGTH_SHORT).show();
		try {
			EditText et = (EditText) v;
			if (bShow)
				et.setTransformationMethod(PasswordTransformationMethod
						.getInstance());
			else
				// hide password
				et.setTransformationMethod(HideReturnsTransformationMethod
						.getInstance());
			/*
			 * et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD); else
			 * et.setInputType(InputType.TYPE_CLASS_TEXT |
			 * InputType.TYPE_TEXT_VARIATION_PASSWORD);
			 */
			bShow = !bShow;
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
		return false;
	}
	
	private class loginTask extends AsyncTask<String, Void, String> {
	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.v("loginTask", "onPreExecute  Refreshing....");
			swipeLayout.setRefreshing(true);
		}
		
		@Override
		protected String doInBackground(String... params) {
			Log.v("loginTask", " doInBackground -> loginTask, start ");
			
			String data = "";
			try{
				data = (new HttpClient()).getData_Post(params[0], params[1]);
			}catch (Exception e){
				data = e.toString();
				System.out.println(e.toString());
			}
			if (data != null){
				System.out.println(data.length());
				System.out.println(data.substring(0,  Math.min(300, data.length())));
				System.out.println(data.substring( Math.max(0, data.length()),  data.length()));
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.v("loginTask", " onPostExecute -> loginTask "+result);
			
			//if (result != null)	utils.ToastS(result.substring(0, Math.min(100, result.length())));
			utils.setPreference( "result", result );
			startActivity(new Intent(getApplicationContext(), SubActivity.class));
			swipeLayout.setRefreshing(false);
			
			System.out.println(((CookieManager)CookieHandler.getDefault()).getCookieStore().getCookies().toString());
		
/*
			if (result != null && result.length() > 100) {
		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        factory.setValidating(false);
		        System.out.println(factory);
		        DocumentBuilder builder;
				try {
					builder = factory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					utils.Toast("error in newDocumentBuilder()");
					return;
				}
		        System.out.println(builder);
		        InputSource is = new InputSource(new StringReader(result));
		        Document document;
				try {
					document = builder.parse(is);
				} catch (SAXException | IOException e) {
					e.printStackTrace();
					utils.Toast("error parse xml");
					return;
				}
		        document.normalize();
		        System.out.println(document);
		        utils.Toast("login successful "+result);
			} else utils.Toast("empty error responce from server: "+result);

			if (!bStart || !utils.getBoolPref(Constants.prefIsLogin)) {
				utils.setBoolPrefrences(Constants.prefIsLogin, false);
				startActivity(new Intent(SplashActivity.this,
						LoginActivity.class));
				finish();
			}
*/
		}
	}

}
