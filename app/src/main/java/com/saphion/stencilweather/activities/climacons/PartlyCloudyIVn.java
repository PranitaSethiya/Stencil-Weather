package com.saphion.stencilweather.activities.climacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import com.saphion.stencilweather.R;

public class PartlyCloudyIVn extends ImageView {

	int height, width;
	Bitmap cloud, sun;
	int angle = 0;
	Paint mPaint = new Paint();

	public PartlyCloudyIVn(Context context) {
		super(context);
	}

	public PartlyCloudyIVn(Context context, int height, int width) {
		super(context);
		this.height = height;
		this.width = width;
		sun = BitmapFactory.decodeResource(getResources(), R.drawable.moon);
		cloud = BitmapFactory.decodeResource(getResources(),
				R.drawable.cloud_black);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		cloud = Bitmap.createScaledBitmap(cloud, (int) (width), (int) (height),
				true);

		sun = Bitmap.createScaledBitmap(sun, (int) (width * 0.35),
				(int) (height * 0.35), true);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawBitmap(sun, width - (float) (sun.getWidth() * 1.3),
				(float) (sun.getHeight() * 0.35), mPaint);

		canvas.drawBitmap(cloud, (width - cloud.getWidth()) / 2,
				(height - cloud.getHeight()) / 2, mPaint);

	}

}
