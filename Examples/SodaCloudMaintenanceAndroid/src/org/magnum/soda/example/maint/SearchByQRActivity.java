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

public class SearchByQRActivity extends Activity implements AndroidSodaListener{
	// UI references.
			private Button getQRImage;
			private Button getReport;
			private ImageView qrImage;
			Context ctx_=this;
			static boolean imageLoaded=false;
			private byte dataBytes[];
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
						Log.d("Soda","after capture");
						AndroidSoda.async(new Runnable() {
							@Override
							public void run() {
								while(!imageLoaded)
								{
								try {
									Thread.sleep(4000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	
								}
								
								if(img!=null)
								{
									Bitmap bm = BitmapFactory.decodeFile(img.getAbsolutePath());
									ByteArrayOutputStream blob = new ByteArrayOutputStream();
									bm.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, blob);
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
								if(dataBytes!=null)
								{
									
								}
							}
						});
					}
				});
				
				AndroidSoda.init(this, "10.0.1.8", 8081, this);

			}
			public static final int MEDIA_TYPE_IMAGE = 1;

			private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
			Uri fileUri=null;
			static File img=null;
			
			private void captureQRImageIntent(Context c)
			{
				 // create Intent to take a picture and return control to the calling application
			    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


				File mediaFile=new File("Test.jpg");
				
			   	FileOutputStream fos;
				try {
					fos = openFileOutput("Test.jpg", Context.MODE_PRIVATE);

					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fileUri= Uri.fromFile(mediaFile);

			    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
			    Log.d("Soda","flag");
			    // start the image capture Intent
			    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
			
			private static Uri getOutputMediaFileUri(int type,Context ct){

				File mediaFile=new File("Test.jpg");
				
								
				FileOutputStream fos;
				try {
					fos = ct.openFileOutput("/"+mediaFile.getName(), Context.MODE_WORLD_WRITEABLE);

					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				img=mediaFile;
			      return Uri.fromFile(mediaFile);
			}
			@Override
			protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			        if (resultCode == RESULT_OK) {			        	
			        	Bitmap bm = null;
			            try {
			                bm = Media.getBitmap(this.getContentResolver(), fileUri);
			                FileOutputStream out = new FileOutputStream(img);
			                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			                bm.recycle();
			                imageLoaded=true;
			            } catch (FileNotFoundException e) {
			                e.printStackTrace();
			            } catch (IOException e) {
			                e.printStackTrace();
			            } catch (Exception e) {
			                e.printStackTrace();
			            }
			        } else if (resultCode == RESULT_CANCELED) {
			            // User cancelled the image capture
			        } else {
			            // Image capture failed, advise user
			        }
			    }

			}

			/** Create a File for saving an image or video */
			private static File getOutputMediaFile(int type,Context ct){
			    // To be safe, you should check that the SDCard is mounted
			    // using Environment.getExternalStorageState() before doing this.

			   // File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
			     //         Environment.DIRECTORY_PICTURES), "MyCameraApp");
			    // This location works best if you want the created images to be shared
			    // between applications and persist after your app has been uninstalled.

				File mediaFile=new File("Test.jpg");
				
			    // Create the storage directory if it does not exist
			   /* if (! mediaStorageDir.exists()){
			        if (! mediaStorageDir.mkdirs()){
			            Log.d("MyCameraApp", "failed to create directory");
			            return null;
			        }
			    }*/
								
				FileOutputStream fos;
				try {
					fos = ct.openFileOutput("/"+mediaFile.getName(), Context.MODE_WORLD_WRITEABLE);

					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
/*
			    // Create a media file name
			    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			    File mediaFile;
			    if (type == MEDIA_TYPE_IMAGE){
			        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
			        "IMG_"+ timeStamp + ".jpg");
			    } else {
			        return null;
			    }
*/
				if(mediaFile!=null)
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
