package com.example.test.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import com.example.test.HttpClient;

import dalvik.system.DexClassLoader;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ModuleEngine {

	public static void load_all(String modulePath, Context cntxt) {
		/**
		 * Создаем загрузчик модулей.
		 */
		ModuleLoader loader = new ModuleLoader(modulePath,
				cntxt.getClassLoader(), cntxt);

		/**
		 * Получаем список доступных модулей.
		 */
		/*
		File dir = cntxt.getDir(modulePath, 0);
		System.out.println("dir=" + dir.toString());
		String[] modules = dir.list();
		*/
		String[] modules = cntxt.fileList (); // files folder
		/**
		 * Загружаем и исполняем каждый модуль.
		 */
		for (String module : modules) {
			if (module.indexOf(".class") > 1)
				try {
					String moduleName = module.split(".class")[0];
					System.out.println(moduleName);
					Class clazz = loader.loadClass(moduleName);
					Module execute = (Module) clazz.newInstance();

					execute.load();
					execute.run();
					execute.unload();

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoClassDefFoundError e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

	}

	public static void writeFile(Context cntxt, String data) {
		try {
			// отрываем поток для записи
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					cntxt.openFileOutput("ModulePrinter.class", 0)));
			// пишем данные
			bw.write(data);
			// закрываем поток
			bw.close();
			Log.d("writeFile", "Файл записан");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(Context cntxt, byte[] data, String file_name) {
		try {
			// отрываем поток для записи
			FileOutputStream os = cntxt.openFileOutput(file_name, 0);
			// пишем данные
			os.write(data);
			// закрываем поток
			os.close();
			Log.d("writeFile", "Файл записан");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void save_url(String url, Context cntxt) {
		//String data = (new HttpClient()).getData(url);
		//Log.v("synLogIn", "result " + data);
		(new getFileUrl(cntxt, url)).execute();
		
		// cntxt.openFileOutput("ModulePrinter.class");
	}

	private class getFileUrl extends AsyncTask<Void, Void, Void> {

		private String file_url ;
		private Context cntxt ;

		public getFileUrl(Context cntxt, String url){
			this.file_url = url;
			this.cntxt = cntxt;
		}
		
		@Override
		protected void onPreExecute() {
			Log.v("getFileUrl", "onPreExecute start ");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.v("getFileUrl", "doInBackground start");
			try{
				//File jarFile = new File("/data/JarProjectWithDex.jar");
				//DexClassLoader loader = new DexClassLoader(jarFile.getPath(), getFilesDir().getPath(), null, this.getClassLoader());
				//Class<?> classToLoad = Class.forName("zordon.MegaZords", true, loader);
				/* do not work on android
				URL classUrl;
		    	classUrl = new URL("http://192.168.8.228/tst/class/");
		    	URL[] classUrls = { classUrl };
		    	DexClassLoader ucl = new DexClassLoader(classUrls);
				Class c = ucl.loadClass("ModulePrinter"); // LINE 14
		    	for(Field f: c.getDeclaredFields()) {
		    		System.out.println("Field name" + f.getName());
		    	}
		    	*/
			} catch (Exception e){
				e.printStackTrace();
				System.out.println(" Exception for "+e.toString());
			}

			try {
				byte[] data = ((new HttpClient()).getFileData(file_url));
				Log.v("synLogIn", "result " + data);
				writeFile(cntxt, data, "ModulePrinter.class");
			} catch (Exception e) {
				e.printStackTrace();
				Log.v("getFileUrl", "doInBackground error  " + e.toString());
			} finally {
				Log.v("getFileUrl", "doInBackground finally  ");
			}
			return null;
		}

	}
}