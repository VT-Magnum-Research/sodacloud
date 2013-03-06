package org.magnum.soda.example.maint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.example.maint.SearchByLocationActivity.GetReportListTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class SearchByQRActivity extends Activity implements AndroidSodaListener {
	// UI references.
	private Button getQRImage;
	private Button getReport;
	private ImageView qrImage;
	private Bitmap qrBitmap;
	Context ctx_ = this;
	static boolean imageLoaded = false;
	private byte dataBytes[];
	private String mCurrentPhotoPath;
	private static final int CAPTURE_IMAGE = 200;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_searchbyqr);

		getQRImage = (Button) findViewById(R.id.getQRCode);
		getReport = (Button) findViewById(R.id.searchQRReports);
		qrImage = (ImageView) findViewById(R.id.QRImage);

		getQRImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				captureQRImageIntent(ctx_);
				Log.d("Soda", "after capture");
				AndroidSoda.async(new Runnable() {
					@Override
					public void run() {
						while (!imageLoaded) {
							try {
								Thread.sleep(4000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						if (img != null) {
							Bitmap bm = BitmapFactory.decodeFile(img
									.getAbsolutePath());
							ByteArrayOutputStream blob = new ByteArrayOutputStream();
							bm.compress(CompressFormat.PNG,
									0 /* ignored for PNG */, blob);
							dataBytes = blob.toByteArray();
						}
					}
				});
			}
		});

		getReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				AndroidSoda.async(new Runnable() {
					@Override
					public void run() {
						if (dataBytes != null) {

						}
					}
				});
			}
		});

		AndroidSoda.init(this, "10.0.1.8", 8081, this);

	}

	public static final int MEDIA_TYPE_IMAGE = 1;

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	Uri fileUri = null;
	static File img = null;

	private void captureQRImageIntent(Context c) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile("target");
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}

		startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case CAPTURE_IMAGE: {
			if (resultCode == RESULT_OK) {

				qrBitmap = scaleBitmap("/sdcard/IMG_target.jpg");

				if (qrBitmap != null) {
					qrImage.setImageBitmap(qrBitmap);
					qrImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					qrImage.setAdjustViewBounds(true);
				} else
					Log.i("getup", "failure of loading image.");
			}

		}
			break;
		}

	}

	private Bitmap scaleBitmap(String filepath) {
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = qrImage.getWidth();
		int targetH = qrImage.getHeight();

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

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			storageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"BattleOfGetUp");

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	private File createImageFile(String name) throws IOException {
		String imageFileName = JPEG_FILE_PREFIX + name;
		File imageF = new File("/sdcard/" + imageFileName + JPEG_FILE_SUFFIX);
		if (!imageF.exists()) {
			imageF.createNewFile();
		}
		return imageF;
	}

	private File setUpPhotoFile(String name) throws IOException {

		File f = createImageFile(name);
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	private static Uri getOutputMediaFileUri(int type, Context ct) {

		File mediaFile = new File("Test.jpg");

		FileOutputStream fos;
		try {
			fos = ct.openFileOutput("/" + mediaFile.getName(),
					Context.MODE_WORLD_WRITEABLE);

			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		img = mediaFile;
		return Uri.fromFile(mediaFile);
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type, Context ct) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		// File mediaStorageDir = new
		// File(Environment.getExternalStoragePublicDirectory(
		// Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		File mediaFile = new File("Test.jpg");

		// Create the storage directory if it does not exist
		/*
		 * if (! mediaStorageDir.exists()){ if (! mediaStorageDir.mkdirs()){
		 * Log.d("MyCameraApp", "failed to create directory"); return null; } }
		 */

		FileOutputStream fos;
		try {
			fos = ct.openFileOutput("/" + mediaFile.getName(),
					Context.MODE_WORLD_WRITEABLE);

			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * // Create a media file name String timeStamp = new
		 * SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); File
		 * mediaFile; if (type == MEDIA_TYPE_IMAGE){ mediaFile = new
		 * File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp +
		 * ".jpg"); } else { return null; }
		 */
		if (mediaFile != null)
			return mediaFile;

		return null;
	}

	@Override
	public void connected(AndroidSoda s) {
		// TODO Auto-generated method stub
		MaintenanceReports reports = s.get(MaintenanceReports.class,
				MaintenanceReports.SVC_NAME);

		reports.addListener(new MaintenanceListener() {

			@SodaInvokeInUi
			public void reportAdded(MaintenanceReport r) {
				Log.d("SODA", "Maintenance report added: " + r.getContents());

			}
		});
	}
}
