package com.demo.androidtask.imagedownload;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Window;

import com.demo.androidtask.R;
import com.demo.androidtask.utils.FileUtils;

public class ImageLoadAsynkTask extends AsyncTask<Void, Void, ArrayList<String>> {

	private static final String TAG = "ImageLoadAsynkTask";

	private ArrayList<ImageDownloadModel> urlList;

	private ImageLoadAsyncTaskListDelegate delegate;

	private boolean mExternalStorageAvailable;

	private boolean mExternalStorageWriteable;

	private Activity activity;

	private String directory;

	private boolean isToPublishProgress = true;
	
	private boolean showProgress = false;

	private String localPath;

	private ProgressDialog progressDialog;

	public ImageLoadAsynkTask(ImageLoadAsyncTaskListDelegate delegate, ArrayList<ImageDownloadModel> urlList, String dirName, Activity activity) {

		this.delegate = delegate;

		this.activity = activity;

		this.urlList = urlList;

		this.directory = dirName;

		mExternalStorageAvailable = false;

		mExternalStorageWriteable = false;

	}

	public ImageLoadAsynkTask(ImageLoadAsyncTaskListDelegate delegate, ImageDownloadModel url, String dirName, Activity activity) {

		this.delegate = delegate;

		this.activity = activity;

		this.urlList = new ArrayList<ImageDownloadModel>();

		this.urlList.add(url);

		this.directory = dirName;

		mExternalStorageAvailable = false;

		mExternalStorageWriteable = false;

	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();

		if (activity != null && !activity.isFinishing() && showProgress) {
			progressDialog = new ProgressDialog(activity);

			progressDialog.setMessage(activity.getString(R.string.txt_loading));

			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

			progressDialog.setCancelable(true);

			progressDialog.setCanceledOnTouchOutside(false);

			if (!activity.isFinishing()) {

				try {
					progressDialog.show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {

			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;

		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

			// We can only read the media
			mExternalStorageAvailable = true;

			mExternalStorageWriteable = false;

		} else {

			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;

		}

	}

	@Override
	protected ArrayList<String> doInBackground(Void... params) {

		Log.d(TAG, "Async doInBackground");

		FileUtils.getInstance().createDirIfNotExists(directory);

		ArrayList<String> localPathList = new ArrayList<String>();

		for (int i = 0; i < urlList.size(); i++) {

			String imageUrl = urlList.get(i).getImageUrl();

			if (imageUrl == null) {

				continue;

			} else {

				Log.d(TAG, "image url in image task-->" + imageUrl);

				localPath = urlList.get(i).getImageLocalPath();

				if (new File(localPath).exists()) {

					if (isToPublishProgress) {

						publishProgress();

					}

					continue;

				} else {

					try {

						if (mExternalStorageAvailable && mExternalStorageWriteable) {

							FileUtils.getInstance().copyFileFromURL(imageUrl, localPath);

						}

					} catch (Exception e) {

						Log.d(TAG, "Exception image loading asynctask");

						e.printStackTrace();

					} finally {

						publishProgress();

						if (isCancelled())
							break;

					}

				}

			}

		}

		return localPathList;

	}

	@Override
	protected void onPostExecute(ArrayList<String> localPathList) {

		super.onPostExecute(localPathList);

		if (progressDialog != null)
			progressDialog.dismiss();

		delegate.imageLoadAsyncTaskListOnComplete();

	}

	@Override
	protected void onProgressUpdate(Void... values) {

		Log.d(TAG, "Publish progress");

		if (delegate != null)
			delegate.imageLoadAsyncTaskListUpdates(localPath);

		super.onProgressUpdate(values);

	}

}
