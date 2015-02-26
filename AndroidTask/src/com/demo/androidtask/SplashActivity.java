package com.demo.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

public class SplashActivity extends Activity implements AnimationListener {

	protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
	protected AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		TextView textView = (TextView) findViewById(R.id.txt_assignment);

		textView.startAnimation(fadeIn);
		textView.startAnimation(fadeOut);
		fadeIn.setDuration(1200);
		fadeIn.setFillAfter(true);
		fadeOut.setDuration(1200);
		fadeOut.setFillAfter(true);
		fadeOut.setStartOffset(4200 + fadeIn.getStartOffset());

		fadeOut.setAnimationListener(this);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		finish();

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}
}
