package com.example.md4;

import ru.yandex.mAng.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


@SuppressWarnings("deprecation")
public class SubActivity extends ActionBarActivity {
	private Toolbar toolbar;
	private WebView webView;
	private com.tele2.md4.http.Utils utils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		utils = new com.tele2.md4.http.Utils(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub);
		toolbar = (Toolbar) findViewById(R.id.app_bar);
		setSupportActionBar(toolbar);
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(true);
		/*
		final Activity activity = this;
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setProgress(progress * 1000);
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(activity, "O! " + description,
						Toast.LENGTH_SHORT).show();
			}
		});
	*/
		webView.loadData(utils.getScriptPrefrences("result"),
				"text/html; charset=UTF-8", null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sub_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	int id = item.getItemId();
		if (id == R.id.action_back) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
