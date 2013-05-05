package edu.berkeley.cs160.gsale;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoPreviewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_preview);
		Bundle extras = this.getIntent().getExtras();
		Bitmap preview = (Bitmap) extras.getParcelable("image");
		ImageView iv = (ImageView) findViewById(R.id.PhotoPreview);
		iv.setImageBitmap(preview);
		TextView tv = (TextView) findViewById(R.id.PhotoPreviewDescription);
		tv.setText(extras.getString("description"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_preview, menu);
		return true;
	}

}
