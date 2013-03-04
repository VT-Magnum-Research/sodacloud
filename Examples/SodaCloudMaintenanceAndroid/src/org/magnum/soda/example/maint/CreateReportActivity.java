package org.magnum.soda.example.maint;

import java.util.List;

import org.magnum.soda.Callback;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateReportActivity extends Activity implements AndroidSodaListener {
	// UI references.
	private ImageView attachedPhotoView;
	private Bitmap mAttachedPhoto;
	private EditText reportContent;
	private Button attachPhotoButton;
	private Button saveButton;
	private Button deleteButton;

	private MaintenanceReports reports_;
	
	private static final int SELECT_IMAGE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_createreport);

		attachedPhotoView = (ImageView) findViewById(R.id.attachedPhotoView);
		reportContent = (EditText) findViewById(R.id.reportContentText);
		attachPhotoButton = (Button) findViewById(R.id.attachphotoButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		deleteButton = (Button) findViewById(R.id.deleteReportButton);

		attachPhotoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, SELECT_IMAGE);
			}
		});
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			final String content = reportContent.getText().toString();
			
			@Override
			public void onClick(View v) {
				AndroidSoda.async(new Runnable() {
					
					@Override
					public void run() {
						MaintenanceReport r = new MaintenanceReport();
						r.setContents(content);
						reports_.addReport(r);
						Log.d("SODA", "report content:" + r.getContents());
					}
				});
			}
		});
		
		AndroidSoda.init(this, "10.0.1.8", 8081, this);

	}

	@Override
	public void connected(final AndroidSoda s) {
		reports_ = s.get(MaintenanceReports.class, MaintenanceReports.SVC_NAME);
		reports_.getReports(new Callback<List<MaintenanceReport>>() {
			@SodaInvokeInUi
			public void handle(List<MaintenanceReport> arg0) {
				updateReports(arg0);
			}
		});

		reports_.addListener(new MaintenanceListener() {

			@SodaInvokeInUi
			public void reportAdded(final MaintenanceReport r) {
				Log.d("SODA", "Maintenance report uploaded: " + r.getContents());
				Toast.makeText(CreateReportActivity.this, "New report:"+r.getContents(), Toast.LENGTH_SHORT).show();
			}
		});

	}
	
    public void updateReports(List<MaintenanceReport> reports){
		
	}
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SELECT_IMAGE: {
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				mAttachedPhoto = scaleBitmap(filePath);
				attachedPhotoView.setImageBitmap(mAttachedPhoto);
				attachedPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				attachedPhotoView.setAdjustViewBounds(true);
			}
			break;
		}

		}

	}

	private Bitmap scaleBitmap(String filepath) {
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = attachedPhotoView.getWidth();
		int targetH = attachedPhotoView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(filepath, bmOptions);
		return bitmap;
	}
}
