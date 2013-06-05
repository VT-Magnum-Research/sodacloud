package org.magnum.soda.example.controllers.searchqrfragment;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.Callback;
import org.magnum.soda.ThirdPartyIntent.IntentIntegrator;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.android.ctx.SodaQR;
import org.magnum.soda.ctx.ImageContainer;
import org.magnum.soda.example.controllers.searchlocationfragment.SearchLocationFragment;
import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.MaintenanceReports;
import org.magnum.soda.example.maint.R;
import org.magnum.soda.example.maint.ReportEditorActivity;
import org.magnum.soda.example.maint.ReportParcelable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;

	
	public class SearchQrFragment extends SherlockFragment implements AndroidSodaListener {
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

		private List<HashMap<String, MaintenanceReport>> mMapList = new ArrayList<HashMap<String, MaintenanceReport>>();
		
		Context ctx_ ;
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
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			asl_ = this;
			ctx_ = this.getActivity();

			mRootView = inflater.inflate(R.layout.activity_searchbyqr, container,false);
//			setContentView(R.layout.activity_searchbyqr);
			setupActionBar();
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

			getQRImage = (Button) mRootView.findViewById(R.id.getQRCode);
			getReport = (Button) mRootView.findViewById(R.id.searchQRReports);
			qrImage = (ImageView) mRootView.findViewById(R.id.QRImage);
			searchResultList = (ListView) mRootView.findViewById(R.id.QR_ListView);

			mAdapter = new SimpleAdapter(
					this.getActivity(),
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
			
			return mRootView;

		}
		
		private byte[] getBytes(Bitmap bmp)
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	        bmp.compress(CompressFormat.JPEG, 70 /*ignored for PNG*/, bos); 
	        byte[] bitmapdata = bos.toByteArray();
	        return bitmapdata;
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
			
			mDisplayList.clear();
			mMapList.clear();
			Iterator<MaintenanceReport> itr = mReportList.iterator();

			while (itr.hasNext()) {
				HashMap<String, String> map = new HashMap<String, String>();
				HashMap<String,MaintenanceReport> sm=new HashMap<String,MaintenanceReport>();
				MaintenanceReport temp = ((MaintenanceReport) itr.next());
				Log.e("-- items--", temp.getCreatorId());
				map.put("itemDescription", temp.getContents());
				sm.put(temp.getContents(),temp);
				mMapList.add(sm);
				mDisplayList.add(map);

			}
			Log.e("size", ":" + mDisplayList.size());
			
			getActivity().runOnUiThread(new Runnable()
			{

				@Override
				public void run() {
					mAdapter.notifyDataSetInvalidated();//
					mAdapter.notifyDataSetChanged();
				}
				
			});
			
		

		}

		private void ReportDetailIntent(String descript) {
			Intent i = new Intent(this.getActivity(), ReportEditorActivity.class);
			i.putExtra("description", descript);
			Iterator<HashMap<String, MaintenanceReport>> itr=mMapList.iterator();
			while(itr.hasNext())
			{
				HashMap<String,MaintenanceReport> m=itr.next();
				if(m.containsKey(descript))
				{
					i.putExtra("mReport", new ReportParcelable(m.get(descript)));
					break;
				}
			}
			startActivity(i);
		}

		public static final int MEDIA_TYPE_IMAGE = 1;

		Uri fileUri = null;
		static File img = null;

		private void captureQRImageIntent(Context c) {
			IntentIntegrator integrator = new IntentIntegrator(this.getActivity());
			integrator.initiateScan();
			/*
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

			startActivityForResult(takePictureIntent, CAPTURE_IMAGE);*/
		}

		public void onActivityResult(int requestCode, int resultCode, Intent data) {


				org.magnum.soda.ThirdPartyIntent.IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
					  if (scanResult != null) {
					   SodaQR qr= SodaQR.create(scanResult.getContents());
					   ImageContainer img= qr.getImg_();
					   if(img.getQrCodeImage_() instanceof Bitmap)
						   qrBitmap= (Bitmap)img.getQrCodeImage_();
					 
				//	qrBitmap = scaleBitmap("/sdcard/IMG_target.jpg");

					if (qrBitmap != null) {
						qrImage.setImageBitmap(qrBitmap);
						qrImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
						qrImage.setAdjustViewBounds(true);
						imageLoaded = true;
					} else
						Log.i("getup", "failure of loading image.");
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
				if(savedInstanceState != null) {
					qrBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
					qrImage.setImageBitmap(qrBitmap);
					
					mAdapter.notifyDataSetChanged();
				}

			}
			
		@Override
		public void connected(AndroidSoda s) {

			this.as = s;
			getReports();
			
		}
	private void setupActionBar() {
		final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
		bar.hide();
	}


}
