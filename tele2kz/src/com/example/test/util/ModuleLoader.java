package com.example.test.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class ModuleLoader extends ClassLoader {

	/**
	 * Путь до директории с модулями. и контекст для доступа к ним (без прав к FS)
	 */
	private String pathtobin;
	private Context cntxt;

	public ModuleLoader(String pathtobin, ClassLoader parent, Context cntxt) {
		super(parent);
		this.pathtobin = pathtobin;
		this.cntxt = cntxt;
	}

	@Override
	public Class<?> findClass(String className) throws ClassNotFoundException {
		try {
			/**
			 * Получем байт-код из файла и загружаем класс в рантайм
			 */
			System.out.println(className + ".class");
			byte b[] = fetchClassFromFS(pathtobin + className + ".class");
			System.out.println(className+" b[] len ="+b.length);
			return defineClass(className, b, 0, b.length);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.out.println(" FileNotFoundException for "+className);
			return super.findClass(className);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(" Exception for "+className);
			return super.findClass(className);
		}

	}

	/**
	 * Взято из
	 * www.java-tips.org/java-se-tips/java.io/reading-a-file-into-a-byte
	 * -array.html
	 */
	private byte[] fetchClassFromFS(String path) throws FileNotFoundException,
			IOException {
		System.out.println(path);
		InputStream is = this.cntxt.openFileInput(path); // FileInputStream(new File(path));

		// Get the size of the file
		long length = is.available();
		System.out.println("length="+length);
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file " + path);
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;

	}
}