package com.demo.androidtask.imagedownload;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.demo.androidtask.R;
import com.demo.androidtask.utils.FileUtils;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	private final ImageView imageView;

	public ImageDownloaderTask(ImageView imageView) {
		this.imageView = imageView;
	}

	@Override
	// Actual download method, run in the task thread
	protected Bitmap doInBackground(String... params) {
		// params comes from the execute() call: params[0] is the url.
		return FileUtils.downloadBitmap(params[0]);
	}

	@Override
	// Once the image is downloaded, associates it to the imageView
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageView != null) {

			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else {
				imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.icon_default));
			}
		}

	}

}