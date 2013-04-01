package org.magnum.soda.example.maint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.Callback;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.svc.PingSvc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SearchByQRActivity extends Activity implements AndroidSodaListener {
	// host
	private String mHost;
	// UI references.
	private Button getQRImage;
	private Button getReport;
	private ImageView qrImage;
	private Bitmap qrBitmap;
	private ListView searchResultList;
	private static SimpleAdapter mAdapter;
	private List<MaintenanceReport> mReportList = new ArrayList<MaintenanceReport>();
	private List<HashMap<String, String>> mDisplayList = new ArrayList<HashMap<String, String>>();

	Context ctx_ = this;
	static boolean imageLoaded = false;
	static Boolean mDataExecuted = false;
	private byte dataBytes[];
	private static final int CAPTURE_IMAGE = 200;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AndroidSodaListener asl_ = null;

	private AndroidSoda as = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		asl_ = this;

		setContentView(R.layout.activity_searchbyqr);
		Properties prop = new Properties();

		try {
			InputStream rawResource = getResources().openRawResource(
					R.raw.connection);
			prop.load(rawResource);
			System.out.println("The properties are now loaded");
			System.out.println("properties: " + prop);

			mHost = prop.getProperty("host");
		} catch (IOException e) {
			Log.e("Property File not found", e.getLocalizedMessage());
		}

		getQRImage = (Button) findViewById(R.id.getQRCode);
		getReport = (Button) findViewById(R.id.searchQRReports);
		qrImage = (ImageView) findViewById(R.id.QRImage);
		searchResultList = (ListView) findViewById(R.id.QR_ListView);

		mAdapter = new SimpleAdapter(
				this,
				mDisplayList,// data source
				R.layout.listview_item_nocheckbox,
				new String[] { "itemDescription" },
				new int[] { R.id.item_description });
		mAdapter.notifyDataSetChanged();
		searchResultList.setAdapter(mAdapter);

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

						if (qrBitmap != null) {
							ByteArrayOutputStream blob = new ByteArrayOutputStream();
							qrBitmap.compress(CompressFormat.PNG, 0 /*
																	 * ignored
																	 * for PNG
																	 */, blob);
							dataBytes = blob.toByteArray();
						}
					}
				});
			}
		});

		getReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (imageLoaded && dataBytes != null) {
					AndroidSoda.init(ctx_, mHost, 8081, asl_);

					Log.d("onClick:", "before async connected");

				}
			}
		});

		searchResultList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) searchResultList
						.getItemAtPosition(position);
				String des = map.get("itemDescription");
				ReportDetailIntent(des);
			}
		});

	}

	private void getReports() {
		List<Future> list = new ArrayList<Future>();

		Future<?> Result = AndroidSoda.async(new Runnable() {
			@Override
			@SodaInvokeInUi
			public void run() {
				if (dataBytes != null && as != null) {

					Log.e("conected", "------------------------------------");
					
					MaintenanceReports reportHandle = as.get(
							MaintenanceReports.class,
							MaintenanceReports.SVC_NAME);
					reportHandle
							.getReports(new Callback<List<MaintenanceReport>>() {
								//@SodaInvokeInUi
								public void handle(List<MaintenanceReport> arg0) {
									mReportList = arg0;
									populateList();
								}
							});
					Log.e("obtained", "------------------------------------");


				}
			}
		});

		list.add(Result);
		for (Future f : list) {
			try {
				while (!f.isDone()) {

				}
				f.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void populateList() {
		Iterator<MaintenanceReport> itr = mReportList.iterator();

		while (itr.hasNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			MaintenanceReport temp = ((MaintenanceReport) itr.next());
			Log.e("-- items--", temp.getCreatorId());
			map.put("itemDescription", temp.getContents());
			mDisplayList.add(map);

		}
		Log.e("size", ":" + mDisplayList.size());
		
		runOnUiThread(new Runnable()
		{

			@Override
			public void run() {
				mAdapter.notifyDataSetInvalidated();//
				mAdapter.notifyDataSetChanged();
			}
			
		});
		
	

	}

	private void ReportDetailIntent(String descript) {
		Intent i = new Intent(this, ReportEditorActivity.class);
		i.putExtra("description", descript);
		startActivity(i);
	}

	public static final int MEDIA_TYPE_IMAGE = 1;

	Uri fileUri = null;
	static File img = null;

	private void captureQRImageIntent(Context c) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile("target");
			f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
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
					imageLoaded = true;
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
		f.getAbsolutePath();

		return f;
	}

	@Override
	public void connected(AndroidSoda s) {

		this.as = s;
		getReports();
		/*
		 * MaintenanceReports reports = s.get(MaintenanceReports.class,
		 * MaintenanceReports.SVC_NAME);
		 * 
		 * reports.addListener(new MaintenanceListener() {
		 * 
		 * @SodaInvokeInUi public void reportAdded(MaintenanceReport r) {
		 * Log.d("SODA", "Maintenance report added: " + r.getContents());
		 * 
		 * } });
		 */
	}
}
