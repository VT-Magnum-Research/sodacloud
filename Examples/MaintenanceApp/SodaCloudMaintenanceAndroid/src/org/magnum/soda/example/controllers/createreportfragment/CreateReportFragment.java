package org.magnum.soda.example.controllers.createreportfragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.android.ctx.SodaQR;
import org.magnum.soda.ctx.ImageContainer;
import org.magnum.soda.example.maint.LoginActivity;
import org.magnum.soda.example.maint.MaintenanceListener;
import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.MaintenanceReports;
import org.magnum.soda.example.maint.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class CreateReportFragment extends SherlockFragment implements
		AndroidSodaListener {

	// host
	// private String mHost;
	// UI references.
	private ImageButton attachedPhotoView;
	private ImageView QRView;
	private Bitmap mAttachedPhoto;
	private EditText reportTitle;
	private EditText reportContent;
	private Button attachPhotoButton; 
	private Button saveButton;
	private Button bindLocationButton;
	private Button bindQRButton;
    
	private MaintenanceReports reports_;
	Context ctx_;
	LocationManager locationManager = null;
	private double mPosLat, mPosLng;

	private static final int SELECT_IMAGE = 100;
	private static final int CAPTURE_IMAGE = 200;
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final int STATIC_INTEGER_VALUE = 90;

	private AndroidSodaListener asl_ = null;
	private AndroidSoda as = null;
	private String mTitle = null;
	private String mContent = null;
	private byte[] mImageData = null;
    private String creator = null;
	private View mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx_ = this.getActivity();

		asl_ = this;
		
		mRootView = inflater.inflate(R.layout.fragment_create_report, container,
				false);
		
		setupActionBar();

		attachedPhotoView = (ImageButton) mRootView
				.findViewById(R.id.attachedPhotoView);
		QRView = (ImageView) mRootView.findViewById(R.id.QRimage_createreport);
		reportTitle = (EditText) mRootView
				.findViewById(R.id.title_text);
		reportContent = (EditText) mRootView
				.findViewById(R.id.reportContentText);
		attachPhotoButton = (Button) mRootView
				.findViewById(R.id.attachphotoButton);
		saveButton = (Button) mRootView.findViewById(R.id.saveButton);
		bindLocationButton = (Button) mRootView
				.findViewById(R.id.currentLocation);
		bindQRButton = (Button) mRootView.findViewById(R.id.generateQRbutton);

		locationManager = (LocationManager) this.getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new MyLocationListener(
				this.getActivity());
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 10, locationListener);

		attachPhotoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				captureImageIntent(ctx_);
			}
		});

		attachedPhotoView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				callIntent(getBmp(attachedPhotoView.getDrawable()));

			}

		});
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mTitle = reportTitle.getText().toString();
				mContent = reportContent.getText().toString();
				if(attachedPhotoView.getDrawable() != null)
					mImageData = getBytes(getBmp(attachedPhotoView.getDrawable()));
				SharedPreferences sharedPref = ctx_.getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);
				creator = sharedPref.getString("username", "no");

				if (mContent != null) {

					AndroidSoda.init(ctx_, LoginActivity.mHost, 8081, asl_);
				}
			}
		});

		bindQRButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mContent = reportContent.getText().toString();
				if (mContent != null) {

					SodaQR qr_ = SodaQR.create(mContent);
					ImageContainer bitmap = qr_.getImg_();
					if (bitmap.getQrCodeImage_() instanceof Bitmap) {
						QRView.setImageBitmap((Bitmap) bitmap.getQrCodeImage_());
						QRView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
						QRView.setAdjustViewBounds(true);
					}

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
		return mRootView;

	}

	private Bitmap getBmp(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			return bitmap;
		}
		return null;
	}

	private void callIntent(Bitmap img) {
		Intent i = new Intent(this.getActivity(),
				leadtools.annotationsdemo.AnnotationsDemoActivity.class);
		i.putExtra("byteArray", getBytes(img));
		startActivityForResult(i, STATIC_INTEGER_VALUE);
	}

	private byte[] getBytes(Bitmap bmp) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 10 /* ignored for PNG */, bos);
		byte[] bitmapdata = bos.toByteArray();
		return bitmapdata;
	}

	private void addReport() {
		List<Future> list = new ArrayList<Future>();

		Future<?> Result = AndroidSoda.async(new Runnable() {

			@Override
			public void run() {

				MaintenanceReports reportHandle = as.get(
						MaintenanceReports.class, MaintenanceReports.SVC_NAME);

				reportHandle.addListener(new MaintenanceListener() {

					@SodaInvokeInUi
					public void reportAdded(final MaintenanceReport r) {
						Log.d("SODA",
								"Maintenance report uploaded: "
										+ r.getContents());
						Toast.makeText(CreateReportFragment.this.getActivity(),
								"New report:" + r.getContents(),
								Toast.LENGTH_SHORT).show(); 
					}

					@Override
					public void reportchanged(final MaintenanceReport r) {

					}
				});

				MaintenanceReport r = new MaintenanceReport();
				r.setContents(mContent);
				r.setTitle(mTitle);
				
				if(mImageData!=null){
					r.setImageData(mImageData);
				    Log.e("CreateReportActivity", "length " + mImageData.length);
				}
				UUID id = UUID.randomUUID();
				r.setId(id);
				r.setCreatorId(creator);
				Calendar cal = Calendar.getInstance();
		    	Date createTime = cal.getTime();
		    	r.setCreateTime_(createTime);

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

		this.as = s;
		addReport();

	}

	public void updateReports(List<MaintenanceReport> reports) {

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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case STATIC_INTEGER_VALUE: {
			if (resultCode == Activity.RESULT_OK) {
				byte[] b = data.getByteArrayExtra("result");
				Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
				attachedPhotoView.setImageBitmap(bitmap);
				attachedPhotoView
						.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				attachedPhotoView.setAdjustViewBounds(true);

			}
			break;
		}

		case CAPTURE_IMAGE: {
			if (resultCode == Activity.RESULT_OK) {

				mAttachedPhoto = scaleBitmap("/sdcard/IMG_upload.jpg");

				if (mAttachedPhoto != null) {
					attachedPhotoView.setImageBitmap(mAttachedPhoto);
					attachedPhotoView
							.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mAttachedPhoto);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
				(mAttachedPhoto != null));

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			mAttachedPhoto = savedInstanceState
					.getParcelable(BITMAP_STORAGE_KEY);
			attachedPhotoView.setImageBitmap(mAttachedPhoto);
			attachedPhotoView
					.setVisibility(savedInstanceState
							.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE
							: ImageView.INVISIBLE);
		}
	}
	private void setupActionBar() {
		final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
		bar.hide();
	}

	public class MyLocationListener implements LocationListener {
		private Context mContext;

		public MyLocationListener(Context context) {
			mContext = context;
		}

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
			Toast.makeText(mContext, "Gps Disabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(mContext, "Gps Enabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
		}
	}
}