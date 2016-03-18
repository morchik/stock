package com.example.md4;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.tele2.md4.NavigationDrawerFragment;
import com.tele2.md4.R;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements
		View.OnLongClickListener {
	private boolean bShow = true;
	private Toolbar toolbar;
	private static int gTheme = R.style.AppTheme;

	private EditText edNumber, edMessage;
	private TextView tvDebug;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("MainActivity", " MainActivity onCreate 111 ");
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
		edNumber = (EditText) findViewById(R.id.edNumber);
		View v = (View) edNumber;
		v.setOnLongClickListener(this);
		edMessage = (EditText) findViewById(R.id.edMessage);
		v = (View) edMessage;
		v.setOnLongClickListener(this);
		NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_navigation);
		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawerFragment != null && drawerLayout != null)
			drawerFragment.setUp(drawerLayout, toolbar);
		
		com.tele2.md4.http.Utils utils = new com.tele2.md4.http.Utils(this);

		edNumber = (EditText) findViewById(R.id.edNumber);
		edNumber.setText(utils.getScriptPrefrences("prefnumToSend"));

		edMessage = (EditText) findViewById(R.id.edMessage);
		edMessage.setText(utils.getScriptPrefrences("prefSmsText"));

		tvDebug = (TextView) findViewById(R.id.tvDebug);
		tvDebug.setMovementMethod(new ScrollingMovementMethod());
		tvDebug.setText(utils.getPreference("log_debug"));

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
		Toast.makeText(this, "id=" + id, Toast.LENGTH_SHORT).show();
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
		if (id == R.id.action_send) {
			gTheme = android.R.style.Theme_Black_NoTitleBar_Fullscreen;
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edMessage.getWindowToken(), 0);
			android.provider.Settings.System.putInt(getContentResolver(),
					Settings.System.SCREEN_OFF_TIMEOUT, 600000);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void click(View v) {
		Toast.makeText(this, com.tst.tst.str()+v.getClass().getName() + " v= " + v.toString(),
				Toast.LENGTH_SHORT).show();
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
}
