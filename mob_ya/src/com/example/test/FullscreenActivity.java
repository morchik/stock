package com.example.test;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import me.noip.adimur.mob_ya.R;

import com.example.test.util.*;
import android.webkit.WebView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
@SuppressLint("ClickableViewAccessibility")
public class FullscreenActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 88000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = false;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, 1, 0, R.string.settings);
		mi.setIntent(new Intent(this, PrefActivity.class));
		return super.onCreateOptionsMenu(menu);
	}

	private String sy_login, sy_pass;
	Boolean b_debug;
	private SharedPreferences sp;
	private SystemUiHider mSystemUiHider;
	private TextView tvStatus, tvDebug;
	public WebView webView;
	
	protected void onResume() {
		Log.d("test", "start");
		Log.d("res", a.a("2248", "eb4aisoh7thoew4a").substring(0,6));
		Log.d("test", "finish");
		b_debug = sp.getBoolean("chb_debug", false);
		sy_login = sp.getString("y_login", "");
		sy_pass = sp.getString("y_pass", "");
		String s_temp = getString(R.string.text_status);
		String text = s_temp.replace("7072282999", sy_login);
		tvStatus.setText(text);
		super.onResume();
		try {
			SensorManager mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
			List<Sensor> sensors = mgr.getSensorList(Sensor.TYPE_ALL);
			for (Sensor sensor : sensors) {
				Log.v("", "" + sensor.getName());

			}
		} catch (Exception e) {
			Log.e("", e.toString());
		}
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

		setContentView(R.layout.activity_fullscreen);
		sp = PreferenceManager.getDefaultSharedPreferences(this);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		webView = (WebView) findViewById(R.id.viewRes);

		tvStatus = (TextView) findViewById(R.id.tvStatus);
		tvDebug = (TextView) findViewById(R.id.tvDebug);
		tvDebug.setMovementMethod(new ScrollingMovementMethod());
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}
/*
						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
*/
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	public void click_set(View view) {
		startActivity(new Intent(this, PrefActivity.class));
	}

	@SuppressWarnings("deprecation")
	public void click(View view) throws UnsupportedEncodingException {
		if (sy_login.equalsIgnoreCase("") || sy_pass.equalsIgnoreCase("")) {
			Toast.makeText(this, getString(R.string.text_err_sett),
					Toast.LENGTH_SHORT).show();
			tvDebug.setText(getString(R.string.text_err_sett) + "\n"
					+ tvDebug.getText().toString());
		}  else if (isOnline()) {
			// test post ya
			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);
			
			HttpTask task = new HttpTask();
			String paswd = a.a(sy_pass, "eb4aisoh7thoew4a").substring(0,6);
			task.execute(new String[] {
					"https://passport.yandex-team.ru/auth",
					"login=" + sy_login + "&passwd="
							+ paswd + "&retpath="
							+ URLEncoder.encode("https://ang2.yandex-team.ru/mobile/tasks/get", "UTF-8") } );
							//+URLEncoder.encode("https://passport.yandex-team.ru", "UTF-8") } );
			/*
			HttpTask task2 = new HttpTask();
			task2.execute(new String[] {
					"http://www.almaty.tele2.kz/WebServices/smsService.asmx/SendSms",
					"{\"msisdn\": \"" + edNumber.getEditableText().toString()
							+ "\",  \"message\": \""
							+ edMessage.getEditableText().toString() + "\"}" });
			*/
			try {
				SystemClock.sleep(1000);
				if (task.get() != null) {
					webView.loadData(task.get().toString(), "text/html; charset=UTF-8", null);
					tvDebug.setText("result:"+task.get().length() + "\n"
							+ tvDebug.getText().toString());
					Toast.makeText(this, "result:"+task.get().length(),
							Toast.LENGTH_SHORT).show();
				}
				
				/*
				if (!success.equalsIgnoreCase("true")) {
					Toast.makeText(this, getString(R.string.text_err_login),
							Toast.LENGTH_SHORT).show();
					tvDebug.setText(getString(R.string.text_err_login) + "\n"
							+ tvDebug.getText().toString());
				} else {
					tvDebug.setText(getString(R.string.text_pass_corct) + "\n"
							+ tvDebug.getText().toString());
					String send = json_par.test_s(task2.get());
					if (!send.equalsIgnoreCase("true")) {
						Toast.makeText(this, send + " " + task.get(),
								Toast.LENGTH_LONG).show();
						tvDebug.setText(send + "\n"
								+ tvDebug.getText().toString());
					} else {
						tvDebug.setText(getString(R.string.text_suc_send)
								+ "\n" + tvDebug.getText().toString());
						String amn = json_par.get_AmountSmsLeft(task2.get());
						tvDebug.setText(getString(R.string.text_sms_left) + " "
								+ amn + "\n" + tvDebug.getText().toString());
					}
				}
				
				if (b_debug) {
					tvDebug.setText(task.get() + "\n"
							+ tvDebug.getText().toString());
					
				}*/
			
			} catch (InterruptedException | ExecutionException e) {
				Log.e("click", e.toString());
				e.printStackTrace();
			}

		} else {
			Toast.makeText(this, getString(R.string.text_err_online),
					Toast.LENGTH_SHORT).show();
			tvDebug.setText(getString(R.string.text_err_online) + "\n"
					+ tvDebug.getText().toString());
		}
		tvDebug.setText((new Date()).toGMTString() + "\n"
				+ tvDebug.getText().toString());
		/*
		try{
			rsa.main();
		}catch(Exception e){
			tvDebug.setText((new Date()).toGMTString()+ "\n" 
					+ e.toString()+ "\n"
					+ tvDebug.getText().toString());
		}*/
	}
}
