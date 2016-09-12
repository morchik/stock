package com.example.test;

import com.example.test.util.HttpClnt;

import android.os.AsyncTask;
import android.util.Log;

class HttpTask extends AsyncTask<String, Integer, String> {
	public String[] in_params; 
	
	@Override
	protected String doInBackground(String... params) {
		in_params = params;
		String data;
		if (params.length == 2)
			data = ((new HttpClient()).getPOSTAJAX(params[0], params[1]));
		else
			data = ((new HttpClnt()).getData(params[0]));			
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
		FullscreenActivity.finish_all_tasks(in_params);
	}
}
