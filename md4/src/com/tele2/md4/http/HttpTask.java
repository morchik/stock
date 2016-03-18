package com.tele2.md4.http;

import android.os.AsyncTask;
import android.util.Log;

public class HttpTask extends AsyncTask<String, Integer, String> {
	public String[] in_params; 
	
	@Override
	protected String doInBackground(String... params) {
		in_params = params;
		String data;
		if (params.length == 2)
			data = ((new HttpJsonClient()).getPOSTAJAX(params[0], params[1]));
		else
			data = ((new HttpClient()).getData(params[0]));			
		Log.v("HttpTask", params[0] + " result " + data);
		return data;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}
}
