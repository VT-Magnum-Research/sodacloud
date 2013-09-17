package org.magnum.soda.example.controllers.searchqrfragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import leadtools.demos.BitmapUtils;
import leadtools.demos.BitmapUtils.GetBitmapListener;

import org.magnum.soda.Callback;
import org.magnum.soda.ThirdPartyIntent.IntentIntegrator;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.android.ctx.SodaQR;
import org.magnum.soda.ctx.ImageContainer;
import org.magnum.soda.example.maint.LoginActivity;
import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.MaintenanceReports;
import org.magnum.soda.example.maint.R;
import org.magnum.soda.example.maint.ReportEditorActivity;
import org.magnum.soda.example.maint.ReportParcelable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockFragment;

public class SearchQrFragment extends SherlockFragment implements
		AndroidSodaListener {
	private static final String TAG = SearchQrFragment.class.getName();

	// UI references.
	private Button getQRImage;
	private Button getReport;
	private Button refresh;
	private Button mLoadDummyReportsButton;
	private ImageView qrImage;
	private Bitmap qrBitmap;
	private static SimpleAdapter mAdapter;
	private List<MaintenanceReport> mReportList = new ArrayList<MaintenanceReport>();
	private List<HashMap<String, String>> mDisplayList = new ArrayList<HashMap<String, String>>();

	private List<HashMap<String, MaintenanceReport>> mMapList = new ArrayList<HashMap<String, MaintenanceReport>>();

	Context ctx_;
	static boolean imageLoaded = false;
	static Boolean mDataExecuted = false;
	private byte dataBytes[];
	private static final int CAPTURE_IMAGE = 200;
	private static final String BITMAP_STORAGE_KEY = "qrbitmap";
	private static final String LIST_STORAGE_KEY = "list";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AndroidSodaListener asl_ = null;

	private AndroidSoda as = null;

	private View mRootView;
	private ListView mReportsListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		asl_ = this;
		ctx_ = this.getActivity();
		Log.i(TAG, getActivity().toString());

		mRootView = inflater.inflate(R.layout.fragment_search_qr, container,
				false);

		setupActionBar();

		getQRImage = (Button) mRootView.findViewById(R.id.getQRCode);
		getReport = (Button) mRootView.findViewById(R.id.searchQRReports);
		refresh = (Button) mRootView.findViewById(R.id.Refresh);
		
		mReportsListView = (ListView) mRootView.findViewById(R.id.listViewListReports);
		mReportsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "onItemClicked: " + position);
				if(mReportList == null) {
					Log.d(TAG, "mReports was null");
					return;
				}
				HashMap<String, String> map = (HashMap<String, String>) mReportsListView
						.getItemAtPosition(position);
				launchReportEditor(mReportList.get(position));

			}
		});
		
		mAdapter = new SimpleAdapter(
				this.getActivity(),
				mDisplayList,// data source
				R.layout.listview_item_nocheckbox,
				new String[] { "itemDescription" },
				new int[] { R.id.item_description });
		mAdapter.notifyDataSetChanged();
		mReportsListView.setAdapter(mAdapter);
		
		mLoadDummyReportsButton = (Button) mRootView
				.findViewById(R.id.buttonLoadDummyReports);
		mLoadDummyReportsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displayDummyReports();

			}
		});
		qrImage = (ImageView) mRootView.findViewById(R.id.QRImage);

		getQRImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				captureQRImageIntent(ctx_);
				AndroidSoda.async(new Runnable() {
					@Override
					public void run() {
						while (!imageLoaded) {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						if (qrBitmap != null) {
							dataBytes = getBytes(qrBitmap);
						}
					}
				});
			}
		});

		getReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AndroidSoda.init(ctx_, LoginActivity.mHost, 8081, asl_);
			}
		});
		 
	      
		refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		return mRootView;

	}
	private void populateList() {

		mDisplayList.clear();
		mMapList.clear();
		Iterator<MaintenanceReport> itr = mReportList.iterator();

		while (itr.hasNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			HashMap<String,MaintenanceReport> sm=new HashMap<String,MaintenanceReport>();
			MaintenanceReport temp = ((MaintenanceReport) itr.next());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
			String item = "Title:"+temp.getTitle()+"\n content:"+temp.getContents()+"\n"+temp.getCreatorId()+"   "+ sdf.format(temp.getCreateTime_());
			map.put("itemDescription", item);

			sm.put(item,temp);
			mMapList.add(sm);
			mDisplayList.add(map);

		}
		Log.e("size", ":" + mDisplayList.size());

		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run() {
				mAdapter.notifyDataSetInvalidated();
				mAdapter.notifyDataSetChanged();
				Log.i(TAG,"after notifyDataSetChanged()");
			}
		});

	}

	private void launchReportEditor(MaintenanceReport report) {
		Log.d(TAG,"launcing report editor");
		Intent i = new Intent(this.getActivity(), ReportEditorActivity.class);
		i.putExtra("mReport", new ReportParcelable(report));
		startActivity(i);
	}
	
	private byte[] getBytes(Bitmap bmp) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 70 /* ignored for PNG */, bos);
		byte[] bitmapdata = bos.toByteArray();
		return bitmapdata;
	}

	public void getReports() {
		List<Future> list = new ArrayList<Future>();

		Future<?> Result = AndroidSoda.async(new Runnable() {
			@Override
			@SodaInvokeInUi
			public void run() {
				if (as != null) {

					Log.i(TAG, "conected");

					MaintenanceReports reportHandle = as.get(
							MaintenanceReports.class,
							MaintenanceReports.SVC_NAME);
					reportHandle
							.getReports(new Callback<List<MaintenanceReport>>() {
								// @SodaInvokeInUi
								public void handle(List<MaintenanceReport> arg0) {
									mReportList = arg0;
									Log.i(TAG, "size of mReportList: "
											+ mReportList.size());
									populateList();
									
								}
							});
					Log.i(TAG, "obtained");

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
	
	private void displayDummyReports() {
		List<MaintenanceReport> dummyReports = new ArrayList<MaintenanceReport>();
		final MaintenanceReport reportWithImage = new MaintenanceReport();
		reportWithImage.setContents("This report has an image!");
		reportWithImage.setTitle("Report with an image");
		String url = "https://si0.twimg.com/profile_images/2725938749/60d4af1fa99056b83e9ccc746a81c88b.png";
		BitmapUtils.getBitmapByteArrayFromUrlAsync(url,
				new GetBitmapListener<byte[]>() {

					@Override
					public void onResponse(byte[] bitmaps) {
						reportWithImage.setImageData(bitmaps);

					}
				});
		dummyReports.add(reportWithImage);
		for (int i = 0; i < 10; ++i) {
			MaintenanceReport report = new MaintenanceReport();
			report.setTitle("Report Title");
			report.setCreateTime_(new Date());
			report.setContents("These are the contents of this report.");
			if (i == 4) {
				report.setContents("The contents of this report are very very very very very long: Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
			}
			dummyReports.add(report);
		}
		
		mReportList = dummyReports;
		populateList();

	}


	public static final int MEDIA_TYPE_IMAGE = 1;

	Uri fileUri = null;
	static File img = null;

	private void captureQRImageIntent(Context c) {
		IntentIntegrator integrator = new IntentIntegrator(this,
				this.getActivity());
		integrator.initiateScan();
		/*
		 * Intent takePictureIntent = new
		 * Intent(MediaStore.ACTION_IMAGE_CAPTURE); File f = null; try { f =
		 * setUpPhotoFile("target"); f.getAbsolutePath(); takePictureIntent
		 * .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f)); } catch
		 * (IOException e) { e.printStackTrace(); f = null; }
		 * 
		 * startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
		 */
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		org.magnum.soda.ThirdPartyIntent.IntentResult scanResult = IntentIntegrator
				.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
			SodaQR qr = SodaQR.create(scanResult.getContents());
			ImageContainer img = qr.getImg_();
			if (img.getQrCodeImage_() instanceof Bitmap)
				qrBitmap = (Bitmap) img.getQrCodeImage_();

			// qrBitmap = scaleBitmap("/sdcard/IMG_target.jpg");

			if (qrBitmap != null) {
				Log.i("SearchQrFragment", "qrBitmap not null.");
				qrImage.setImageBitmap(qrBitmap);
				qrImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				qrImage.setAdjustViewBounds(true);
				imageLoaded = true;
			} else
				Log.e("SearchQrFragment", "failure of loading image.");
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

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, qrBitmap);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			qrBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
			qrImage.setImageBitmap(qrBitmap);

		}

	}

	@Override
	public void connected(AndroidSoda s) {

		this.as = s;
		getReports();

	}

	private void setupActionBar() {
		final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity()
				.getSupportActionBar();
		bar.hide();
	}

}
