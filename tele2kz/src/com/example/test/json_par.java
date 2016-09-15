package com.example.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class json_par {
	public static String create_sms(String msisdn, String message) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("message", message);
			obj.put("msisdn", msisdn);
			Log.v("json_par", "create_sms result " + obj.toString());
		return obj.toString();
		} catch (JSONException e) {
			android.util.Log.e("json_par", e.toString() + " message=" + message);
			e.printStackTrace();
			return "error parse "+e.toString();
		}
	}

	public static String create_token(String code) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("same_origin_token", code);
			Log.v("json_par", "create_token result " + obj.toString());
			return obj.toString();
		} catch (JSONException e) {
			android.util.Log.e("json_par", e.toString() + " code=" + code);
			e.printStackTrace();
			return "error parse "+e.toString();
		}
	}

	public static String create_auth(String number, String password) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("number", number);
			obj.put("password", password);
			Log.v("json_par", "create_auth result " + obj.toString());
		return obj.toString();
		} catch (JSONException e) {
			android.util.Log.e("json_par", e.toString() + " number=" + number
					+" "+password);
			return "error parse "+e.toString();
		}
	}
	
	// {"msisdn":"7071355145","type":"sms"
	//	,"same_origin_token":"5496131068d7dcd774aff994421e10c222d65447","answer":"780054"}
	public static String create_AuthReg(String msisdn, String token, String answer) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("answer", answer);
			obj.put("same_origin_token", token);
			obj.put("type", "sms");
			obj.put("msisdn", msisdn);
			Log.v("json_par", "create_AuthReg result " + obj.toString());
		return obj.toString();
		} catch (JSONException e) {
			android.util.Log.e("json_par", e.toString() + " number=" + msisdn
					+" answer="+answer);
			return "error parse "+e.toString();
		}
	}

	public static String test_a(String data) {
		try {
			String msg;
			Boolean suc;
			JSONObject jObj = new JSONObject(data);
			if (!jObj.has("d"))
				return "error";
			else {
				jObj = new JSONObject(jObj.getString("d"));
				if (!jObj.has("Success"))
					return "error parse json do not has Success in "+jObj.toString();
				else 
					suc = jObj.getBoolean("Success");
				if (!jObj.has("Message"))
					return "error parse json do not has Message in "+jObj.toString();
				else 
					msg = jObj.getString("Message");
				
				if (suc) {
					return "true";
				} else {
					Log.v("json_par", data);
					return msg;
				}
			}
		} catch (JSONException e) {
			android.util.Log.e("json_par", e.toString() + " " + data);
			return "error parse "+e.toString();
		} catch (Exception e) {
			android.util.Log.e("json_par error ", e.toString() + " " + data);
			return "error "+e.toString();
		}
	}

	public static String test_s(String data) {
		try {
			String err, rmsg;
			JSONObject jObj = new JSONObject(data);
			if (!jObj.has("d"))
				return "error";
			else {
				jObj = new JSONObject(jObj.getString("d"));
				if (!jObj.has("ErrorCode"))
					return "error parse json do not has ErrorCode in "+jObj.toString();
				else 
					err = jObj.getString("ErrorCode");
				if (!jObj.has("ResponseMessage"))
					return "error parse json do not has ResponseMessage in "+jObj.toString();
				else 
					rmsg = jObj.getString("ResponseMessage");
				if (!err.equalsIgnoreCase("0")) {
					return rmsg;
				} else {
					Log.v("json_par", rmsg);
					return "true";
				}
			}
		} catch (JSONException e) {
			android.util.Log.e("json_par", e.toString() + " " + data);
			return "error parse "+e.toString();
		} catch (Exception e) {
			android.util.Log.e("json_par error ", e.toString() + " " + data);
			return "error "+e.toString();
		}
	}
	
	public static String get_AmountSmsLeft(String data) {
		try {

			JSONObject jObj = new JSONObject(data);
			if (!jObj.has("d"))
				return "error no D";
			else {
				jObj = new JSONObject(jObj.getString("d"));
				String amn = jObj.getString("AmountSmsLeft");
				if (!amn.isEmpty()) {
					return amn;
				} else {
					return "error empty";
				}
			}
		} catch (JSONException e) {
			android.util.Log.e("json_par", e.toString() + " " + data);
			return "error parse "+e.toString();
		} catch (Exception e) {
			android.util.Log.e("json_par error ", e.toString() + " " + data);
			return "error "+e.toString();
		}
	}

	public static String get_captcha(String data) {
		try {
			JSONObject jObj = new JSONObject(data);
			if (!jObj.has("capcha64"))
				return "error no capcha64";
			else {
				String amn = jObj.getString("capcha64");
				if (!amn.isEmpty()) {
					return amn;
				} else {
					return "error empty";
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			android.util.Log.e("json_par", e.toString() + " " + data);
			return "error parse "+e.toString();
		} catch (Exception e) {
			e.printStackTrace();
			android.util.Log.e("json_par error ", e.toString() + " " + data);
			return "error "+e.toString();
		}
	}

	public static String get_token(String data) {
		try {
			int ind = data.indexOf("csrf_token");
			if (ind>1){
				int st = data.indexOf("'", ind);
				int en = data.indexOf("'", st+1);
				if (st>0 && en>0 && st < en)
					return data.substring(st+1, en);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("get_token error ", e.toString());		
		}
		return null;
	}

}
