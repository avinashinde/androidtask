package com.demo.androidtask.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Base64;
import android.util.Log;

public class FileUtils {

	private static final String TAG = "FileUtils";
	private static FileUtils instance = null;

	public static String generateFileNameFromUrl(String coverUrl) {

		String fileName = coverUrl.replace("https://", "");

		fileName = fileName.replace("http://", "");

		fileName = fileName.replaceAll("[^\\p{L}\\p{Nd}]", "_");

		StringBuilder str = new StringBuilder(fileName);

		str.setCharAt(fileName.lastIndexOf("_"), '.');

		fileName = str.toString();

		return fileName;
	}

	public static FileUtils getInstance() {
		if (instance == null) {
			instance = new FileUtils();
		}
		return instance;
	}

	public boolean createFileIfNotExists(String path) {

		File file = new File(path);

		if (!file.exists()) {
			try {
				return file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			Log.d(TAG, "File already created." + path);
			return true;

		}

	}

	public boolean createDirIfNotExists(String path) {

		File dir = new File(path);

		if (!dir.exists()) {
			try {
				return dir.mkdirs();
			} catch (Exception e) {
				return false;
			}

		} else {
			Log.d(TAG, "Dir already created." + path);
			return true;
		}

	}

	public void createNoMedia(String path) {
		File dir = new File(path);
		File nomediaFile = new File(dir, ".nomedia");
		if (!nomediaFile.exists()) {
			try {
				nomediaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void copyFileFromURL(String url, String filePath) {
		try {

			URL urll = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urll.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();

			byte data[] = new byte[1024];

			FileOutputStream out = new FileOutputStream(filePath);
			int c;
			while ((c = input.read(data)) != -1) {
				out.write(data, 0, c);

			}
			input.close();
			out.close();

		} catch (MalformedURLException urlException) {
			System.out.println("Failed to Copy File from server");

			File fileTemp = new File(filePath);
			if (fileTemp.exists()) {
				fileTemp.delete();
			}

			System.out.println("Damaged File deleted");
			urlException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			File fileTemp = new File(filePath);
			if (fileTemp.exists()) {
				fileTemp.delete();
			}

			System.out.println("Damaged File deleted");
		}
	}

	public boolean deleteDirIfExists(String path) {

		File dir = new File(path);

		if (dir.exists()) {
			return deleteDir(dir);
		} else {
			Log.d(TAG, "Dir does not exist." + path);
			return false;
		}

	}

	public boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);
		try {
			org.apache.http.HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.e(TAG, "ImageDownloader : Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			Log.e(TAG, "ImageDownloader : Error while retrieving bitmap from " + url);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	public static Bitmap decodeSampledBitmapFromPath(String localpath, int inSampleSize) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = inSampleSize;
		BitmapFactory.decodeFile(localpath, options);

		// Calculate inSampleSize
		// options.inSampleSize = calculateInSampleSize(options);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(localpath, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public byte[] getByteArrayFromFilePath(String filePath) {
		File imagefile = new File(filePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(imagefile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Bitmap bm = BitmapFactory.decodeStream(fis);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		Base64.encodeToString(b, Base64.DEFAULT);
		return b;
	}

}
