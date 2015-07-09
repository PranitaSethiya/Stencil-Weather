package com.saphion.stencilweather.climacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.ImageView;

import com.saphion.stencilweather.R;

public class SunImageView extends ImageView {

	int height, width;
	Bitmap sun;
	Paint mPaint;
	int angle = 0;

	public SunImageView(Context context) {
		super(context);
	}

	public SunImageView(Context context, int height, int width) {
		super(context);
		this.height = height;
		this.width = width;
		sun = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		sun = Bitmap.createScaledBitmap(sun, (int) (width), (int) (height),
				true);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();
		canvas.rotate(angle, width / 2, height / 2);
		canvas.drawBitmap(sun, width / 2 - sun.getWidth() / 2,
				height / 2 - sun.getHeight() / 2, mPaint);
		canvas.restore();

		if (angle++ > 360)
			angle = 0;

		mHandler.postDelayed(runnable, 20);

	}

	Handler mHandler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			invalidate();
		}
	};

}
