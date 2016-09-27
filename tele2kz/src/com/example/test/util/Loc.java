package com.example.test.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Loc implements LocationListener {
	public String provider;
	protected static Loc loc = null;
	protected static long counter = 0;
	
	public Loc(String pr){
		provider = pr;
	}
	
	public static String DateTimetoStr(long dt){ // for google track
		return (String) android.text.format.DateFormat.format("yyyy-MM-ddThh:mm:ss.001Z", new java.util.Date(dt));
	}
	
	public static String toCSV(Location l){ // for google track
		counter = counter + 1;
		String result = "\"1\",\""+counter+"\","
			+"\""+l.getLatitude()+"\","
			+"\""+l.getLongitude()+"\","
			+"\""+l.getAltitude()+"\","
			+"\""+l.getBearing()+"\","
			+"\""+Math.round(l.getAccuracy())+"\","
			+"\""+l.getSpeed()+"\","
			+"\""+DateTimetoStr(l.getTime())+"\"" // 2013-09-04T06:01:47.718Z
			+"\"\",\"\",\"\"";
		return result;
	}

	public static String toKML(Location l){ // for google track
		String result = // <when>2014-03-06T11:10:03.421Z</when> <gx:coord>98.394276 7.873981 -16.700000762939453</gx:coord>
			 "<when>"+DateTimetoStr(l.getTime())+"</when> "
			+"<gx:coord>"+l.getLatitude()+" "+l.getLongitude()+" "+l.getAltitude()+"</gx:coord><gx:coord> "
			;
		return result;
	}
		
	public static void start(Context mContext) {
		if (loc == null)
			loc = new Loc(LocationManager.GPS_PROVIDER);

		LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 1, loc);
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		if (loc != null){
			Log.wrLogFile("gps", arg0.toString());
			// ������ � ��� ���� �� �����
			Log.wrLogFile(arg0.toString()+"\n"
				, Log.getDir()+"/"+Log.getDateDir()+"loc_"+provider+"_"+Log.getDay() + ".txt"); // ������ � ����
			// ���� ������� csv ��� ����-�������
			Log.wrLogFile(toCSV(arg0)+"\n"
				, Log.getDir()+"/"+Log.getDateDir()+"loc_"+provider+"_"+Log.getDay() + ".csv"); // ������ � ����
			// ���� ������� KML ��� google earth
			Log.wrLogFile(toKML(arg0)+"\n"
				, Log.getDir()+"/"+Log.getDateDir()+"loc_"+provider+"_"+Log.getDay() + ".kml"); // ������ � ����
		}
	}


    @Override
    public void onProviderDisabled(String provider) {
		Log.d("LOC", "onProviderDisabled "+provider); 
    }

    @Override
    public void onProviderEnabled(String provider) {
		Log.d("LOC", "onProviderEnabled "+provider); 
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("LOC", "onStatusChanged  "+provider+" status "+status); 
    }
}
