/*******************************************************************************
 * Copyright 2013 PAR Works, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package leadtools.demos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/**
 * A class for manipulating bitmaps.
 * @author Adam Hickey
 *
 */
public class BitmapUtils {

	private static final int BITMAP_SAMPLE_SIZE = 4;
	private static final String TAG = BitmapUtils.class.getName();

	/**
	 * Gets an image from a url and returns it as an inputstream.
	 * @param url The absolute url of the image
	 * @return an inputstream containing the image
	 */
	public static InputStream getImageStream(String url) {
		try {
			return getImageStream(new URL(url));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets an image from a url and returns it as an inputstream.
	 * @param url The absolute url of the image
	 * @return an inputstream containing the image
	 */
	public static InputStream getImageStream(URL stringUrl) {
		InputStream input = null;
		try {
			input = stringUrl.openConnection().getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return input;
	}
	
	public static InputStream getImageStreamFromUrl(String stringUrl) {
		URL url;
		try {
			url = new URL(stringUrl);
		} catch (MalformedURLException e1) {
			throw new RuntimeException(e1);
		}
		InputStream input = null;
		try {
			input = url.openConnection().getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return input;
	}
	
	public static void getBitmapByteArrayFromUrlAsync(final String url, final GetBitmapListener<byte[]> listener) {
		new AsyncTask<Void, Void, byte[]>() {

			@Override
			protected byte[] doInBackground(Void... arg0) {
				return getBitmapByteArrayFromUrl(url);
				
			}

			@Override
			protected void onPostExecute(byte[] result) {
				listener.onResponse(result);
			}

		}.execute();
	}
	public static byte[] getBitmapByteArrayFromUrl(String url) {
		Bitmap bmp = getBitmap(url);
		return convertBitmapToByteArray(bmp);
	}

	/**
	 * Converts an inputstream into a bitmap using the BITMAP_SAMPLE_SIZE;
	 * @param in the inputstream containing a bitmap
	 * @return the bitmap
	 */
	public static Bitmap convertBitmap(InputStream in, int sampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;
		return BitmapFactory.decodeStream(in, null, options);
	}
	
	/**
	 * Takes a url and returns the bitmap at that url
	 * @param url absolute url on the web
	 * @return the bitmap
	 */
	public static Bitmap getBitmap(String url) {
		InputStream imageStream = getImageStream(url);
		return convertBitmap(imageStream, BITMAP_SAMPLE_SIZE);
	}
	
	/**
	 * Takes a url and the sample size,
	 * 		 and returns the bitmap at that url
	 * @param url absolute url on the web
	 * @return the bitmap
	 */
	public static Bitmap getBitmap(String url, int sampleSize) {
		InputStream imageStream = getImageStream(url);
		return convertBitmap(imageStream, sampleSize);
	}		

	/**
	 * Converts a bitmap into an inputstream
	 * @param image the bitmap
	 * @return the inputstream
	 */
	public static InputStream convertBitmap(Bitmap image) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		image.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
		byte[] bitmapdata = bos.toByteArray();
		return new ByteArrayInputStream(bitmapdata);
	}

	/**
	 * Asynchronously get a bitmap from a url
	 * @param url the absolute url on the web
	 * @param listener the callback listener
	 */
	public void getBitmap(final String url, final GetBitmapListener<Bitmap> listener) {
		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... arg0) {
				InputStream imageStream = getImageStream(url);
				return convertBitmap(imageStream, BITMAP_SAMPLE_SIZE);	
				
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				listener.onResponse(result);
			}

		}.execute();

	}
	
	/**
	 * Sync download the image and save it in the local disk
	 * 
	 * @param url
	 * @param filePath
	 */
	public static File downloadBitmap(final String url, final String filePath) {
		InputStream inputStream = getImageStream(url);
		File outputFile = new File(filePath);
		try{
			FileOutputStream out = new FileOutputStream(outputFile, false);
			int read = 0;
			byte[] bytes = new byte[1024];			 
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}			 
			inputStream.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
		}				
		return outputFile;
	}
	
	/**
	 * Async download the image and save it in the local disk
	 * 
	 * @param url
	 * @param filePath
	 * @param listener
	 */
	public static void downloadBitmap(final String url, final String filePath, final GetBitmapListener<File> listener) {
		new AsyncTask<Void, Void, File>() {
			@Override
			protected File doInBackground(Void... arg0) {				
				return downloadBitmap(url, filePath);					
			}

			@Override
			protected void onPostExecute(File result) {
				listener.onResponse(result);
			}
		}.execute();
	}

	/**
	 * Enum to select the size of the image to download from the parworks api
	 * @author adam
	 *
	 */
	public enum ImageSize {
		Content, Gallery, Full

	}

	/**
	 * Listener for getBitmap
	 * @author adam
	 *
	 * @param <T>
	 */
	public interface GetBitmapListener<T> {
		public void onResponse(T bitmaps);
	}
	
	public static byte[] convertBitmapToByteArray(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}

	


}
