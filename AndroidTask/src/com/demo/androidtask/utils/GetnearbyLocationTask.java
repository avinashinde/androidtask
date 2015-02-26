package com.demo.androidtask.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class GetnearbyLocationTask extends AsyncTask<String, Void, String> {

	private Activity activity;
	private ProgressDialog progressDialog;
	private NearByLocation nearByLocation;
	String error = "No places Found NearyBy";

	public GetnearbyLocationTask(Activity activity, NearByLocation nearByLocation) {
		this.activity = activity;
		this.nearByLocation = nearByLocation;
		progressDialog = new ProgressDialog(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog.setMessage("Fetching result, please wait..");
		progressDialog.setCancelable(false);
		progressDialog.show();

	}

	@SuppressWarnings("finally")
	@Override
	protected String doInBackground(String... params) {
		String str = "";

		try {
			HttpClient mHttpClient = MySSLSocketFactory.wrapClient(new DefaultHttpClient());
			HttpGet get = new HttpGet(params[0]);
			HttpResponse response = mHttpClient.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				return str = error;
			}

			str = getStringFromResponse(response);
			JSONObject jsonObject = new JSONObject(str);

			if (!jsonObject.getString("status").equals("OK"))
				return str = error;

			return str;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return str;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		nearByLocation.processResult(result);
	}

	private String getStringFromInputStream(InputStream is)
			throws IOException {
		byte[] bytes = new byte[1000];

		StringBuilder sb = new StringBuilder();

		int numRead = 0;
		while ((numRead = is.read(bytes)) >= 0) {
			sb.append(new String(bytes, 0, numRead));
		}
		return sb.toString();
	}

	public String getStringFromResponse(HttpResponse response)
			throws IOException {

		HttpEntity entity = response.getEntity();

		Header contentEncoding = response.getFirstHeader("Content-Encoding");
		if (contentEncoding != null
				&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {

			InputStream content = entity.getContent();
			try {
				InputStream inStream = new GZIPInputStream(content);
				try {
					return getStringFromInputStream(inStream);
				} finally {
					inStream.close();
				}
			} finally {
				content.close();
			}
		} else {
			return EntityUtils.toString(entity);
		}
	}

	public interface NearByLocation {
		public void processResult(String result);
	}

}
