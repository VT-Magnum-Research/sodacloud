package org.magnum.soda.example.maint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.ctx.ImageContainer;
import org.magnum.soda.ctx.SodaQR;
import org.magnum.soda.ctx.SodaQR_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateReportActivity extends Activity implements AndroidSodaListener {
	
	//host
	private String mHost;//="172.31.55.100";
	// UI references.
	private ImageView attachedPhotoView;
	private ImageView QRView;
	private Bitmap mAttachedPhoto;
	private EditText reportContent;
	private Button attachPhotoButton;
	private Button saveButton;
	private Button bindLocationButton;
	private Button bindQRButton;

	private MaintenanceReports reports_;
	Context ctx_ = this;
	LocationManager locationManager = null;
	private double mPosLat,mPosLng;
	
	private static final int SELECT_IMAGE = 100;
	private static final int CAPTURE_IMAGE = 200;
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	

	private AndroidSodaListener asl_=null;
	private AndroidSoda as=null;	
	private String mContent=null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		asl_=this;
		
		setContentView(R.layout.activity_createreport);
		Properties prop = new Properties();
		 
    	try {
    		 InputStream rawResource = getResources().openRawResource(R.raw.connection);
    		 prop.load(rawResource);
    		  System.out.println("The properties are now loaded");
    		  System.out.println("properties: " + prop);
    		
    		mHost=prop.getProperty("host");
    	}
    	catch(IOException e)
    	{
    		Log.e("Property File not found",e.getLocalizedMessage());
    	}



		attachedPhotoView = (ImageView) findViewById(R.id.attachedPhotoView);
		QRView = (ImageView) findViewById(R.id.textQRimage);
		reportContent = (EditText) findViewById(R.id.reportContentText);
		attachPhotoButton = (Button) findViewById(R.id.attachphotoButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		bindLocationButton = (Button) findViewById(R.id.currentLocation);
		bindQRButton = (Button) findViewById(R.id.generateQRbutton);
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new MyLocationListener();  
		   locationManager.requestLocationUpdates(  
		    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
		   
		attachPhotoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				captureImageIntent(ctx_);
			}
		});
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 mContent = reportContent.getText().toString();
				if(mContent!=null)
				{

				AndroidSoda.init(ctx_, mHost, 8081, asl_);
							}}
		});

		bindQRButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
				
				 mContent = reportContent.getText().toString();
				 if(mContent!=null)
				 {
					 
					 SodaQR qr_=SodaQR.create(mContent);
					 ImageContainer bitmap=qr_.getImg_();
					QRView.setImageBitmap(bitmap.getQrBitCodeImage_());
					 QRView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					 QRView.setAdjustViewBounds(true);

				 }
				Log.d("SODA", "QR");
			}
		});
		
		bindLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
				Log.d("SODA", "Location: lat:" + mPosLat + "lng:" + mPosLng);
			}
		});

		
	}
	
	private void addReport()
	{
		List<Future> list = new ArrayList<Future>();

		Future<?> Result =AndroidSoda.async(new Runnable() {
			
			@Override
			public void run() {
				
			
				MaintenanceReports reportHandle=as.get(MaintenanceReports.class, MaintenanceReports.SVC_NAME);
				
				reportHandle.addListener(new MaintenanceListener() {

					@SodaInvokeInUi
					public void reportAdded(final MaintenanceReport r) {
						Log.d("SODA", "Maintenance report uploaded: " + r.getContents());
						Toast.makeText(CreateReportActivity.this, "New report:"+r.getContents(), Toast.LENGTH_SHORT).show();
					}
				});
				
		
				MaintenanceReport r = new MaintenanceReport();
				r.setContents(mContent);
				r.setId(2);
				r.setCreatorId("aks1");

				

				System.out.println("========Input Content======== :"+mContent);
				reportHandle.addReport(r);
				Log.d("SODA", "report content:" + r.getContents());
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

	@Override
	public void connected(final AndroidSoda s) {
		
		this.as=s;
		addReport();
		/*reports_ = s.get(MaintenanceReports.class, MaintenanceReports.SVC_NAME);
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
		});*/

	}
	
    public void updateReports(List<MaintenanceReport> reports){
		
	}
    
    private void captureImageIntent(Context c) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile("upload");
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

				mAttachedPhoto = scaleBitmap("/sdcard/IMG_upload.jpg");

				if (mAttachedPhoto != null) {
					attachedPhotoView.setImageBitmap(mAttachedPhoto);
					attachedPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					attachedPhotoView.setAdjustViewBounds(true);

				} else
					Log.i("SODA", "Failure of capturing image.");
			}

		}
			break;
		}

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
	
	// Some lifecycle callbacks so that the image can survive orientation change
		@Override
		protected void onSaveInstanceState(Bundle outState) {
			outState.putParcelable(BITMAP_STORAGE_KEY, mAttachedPhoto);
			outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mAttachedPhoto != null) );

			super.onSaveInstanceState(outState);
		}

		@Override
		protected void onRestoreInstanceState(Bundle savedInstanceState) {
			super.onRestoreInstanceState(savedInstanceState);
			mAttachedPhoto = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
			attachedPhotoView.setImageBitmap(mAttachedPhoto);
			attachedPhotoView.setVisibility(
					savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
							ImageView.VISIBLE : ImageView.INVISIBLE
					);		
			
		}
		
		public class MyLocationListener implements LocationListener {

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if (location != null) {
					mPosLat = location.getLatitude();
					mPosLng = location.getLongitude();    
				}
			}

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				Toast.makeText( getApplicationContext(),"Gps Disabled",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				Toast.makeText( getApplicationContext(),"Gps Enabled",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
			}
		} 

}
