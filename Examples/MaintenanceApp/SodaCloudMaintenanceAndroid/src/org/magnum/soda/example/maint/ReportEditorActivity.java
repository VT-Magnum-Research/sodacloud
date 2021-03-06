/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.example.maint;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.example.controllers.createreportfragment.CreateReportFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReportEditorActivity extends Activity implements
		AndroidSodaListener {
	private EditText reportTitle_;
	private EditText reportContent_;
	private TextView creatorText_;
	private TextView createtimeText_;
	private Button saveButton_;
	private Button deleteButton_;
	private Button bindLocationButton_;
	private Button bindQRButton_;
	private Button followButton_;
	private ImageButton photoView_;

	private String username;
	private MaintenanceReports reports_;
	private MaintenanceReport currReport_;
	private ReportParcelable current;
	private static final int STATIC_INTEGER_VALUE = 10;

	private String mContent = null;
	private byte[] mImageData = null;
	private String creator = null;

	private AndroidSoda as = null;
	private AndroidSodaListener asl_ = null;
	Context ctx_ = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		asl_ = this;
		setContentView(R.layout.report_form);
		try {
			reportTitle_ = (EditText) findViewById(R.id.title_text_editreport);
			reportContent_ = (EditText) findViewById(R.id.reportEditText_editreport);
			saveButton_ = (Button) findViewById(R.id.saveButton_reportedit);
			deleteButton_ = (Button) findViewById(R.id.DeleteButton);
			bindLocationButton_ = (Button) findViewById(R.id.bindLocationButton);
			bindQRButton_ = (Button) findViewById(R.id.BindQR);
			followButton_ = (Button) findViewById(R.id.Button_follow);
			creatorText_ = (TextView) findViewById(R.id.textView_creatorID);
			createtimeText_ = (TextView) findViewById(R.id.textView_createTime);
			photoView_ = (ImageButton) findViewById(R.id.image_editreport);

			SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);
			username = sharedPref.getString("username", "no");
			
			Intent callingintent = getIntent();

			if (getIntent().hasExtra("mReport")) {
				current = getIntent().getExtras().getParcelable("mReport");
				currReport_ = current.getReport();
				reportTitle_.setText(currReport_.getTitle());
				reportContent_.setText(currReport_.getContents());
				creator = currReport_.getCreatorId();
				creatorText_.setText(creator);

				Date createtime = currReport_.getCreateTime_();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy.MM.dd  HH:mm:ss");
				createtimeText_.setText(sdf.format(createtime));

				byte[] b = current.getReport().getImageData();
				if (b != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0,
							b.length);
					photoView_.setImageBitmap(bitmap);
					photoView_.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					photoView_.setAdjustViewBounds(true);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		AndroidSoda.init(ctx_, LoginActivity.mHost, 8081, asl_);
		
		photoView_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				callIntent(getBmp(photoView_.getDrawable()));

			}

		});

		saveButton_.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mContent = reportContent_.getText().toString();
				if (mContent != null) {
					currReport_.setContents(mContent);
					
				if(photoView_.getDrawable() != null){
					mImageData = getBytes(getBmp(photoView_.getDrawable()));				
					currReport_.setImageData(mImageData);
				}
				updateReport();
					
				}
			}
		});

		followButton_.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Future<?> Result = AndroidSoda.async(new Runnable() {

					@Override
					public void run() {
						reports_ = as.get(MaintenanceReports.class,
								MaintenanceReports.SVC_NAME);
						reports_.addFollowerListener(currReport_.getId(),new UserListener() {

							@Override
							public void userAdded(User u) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void notifyFollowers(MaintenanceReport u) {
								// TODO Auto-generated method stub
								Log.d("SODA",
										"-------notify followers---: " + u.getContents());
								
							}

						
							});
						
					reports_.addFollower(currReport_,username);
					Toast.makeText(ctx_,
							username + "is now following this report",
							Toast.LENGTH_SHORT).show();
					}
					
					});

			}
		});
		
		deleteButton_.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteReport();
			}
		});

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
		Intent i = new Intent(this,
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case STATIC_INTEGER_VALUE: {
			if (resultCode == Activity.RESULT_OK) {
				byte[] b = data.getByteArrayExtra("result");
				Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
				photoView_.setImageBitmap(bitmap);
				photoView_.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				photoView_.setAdjustViewBounds(true);

			}
			break;
		}
		}
	}

	private void updateReport() {
		List<Future> list = new ArrayList<Future>();

		Future<?> Result = AndroidSoda.async(new Runnable() {

			@Override
			public void run() {

				reports_ = as.get(MaintenanceReports.class,
						MaintenanceReports.SVC_NAME);

				reports_.addListener(new MaintenanceListener() {

					@SodaInvokeInUi
					public void reportAdded(final MaintenanceReport r) {
					}

					@Override
					public void reportchanged(MaintenanceReport r) {
						// TODO Auto-generated method stub
						Log.d("SODA",
								"Maintenance report modified: "
										+ r.getContents());
						 Toast.makeText(ReportEditorActivity.this,
						 "Modified report:"+r.getContents(),
						 Toast.LENGTH_SHORT).show();
						if (mContent.equals(r.getContents())) {

							Log.d("SODA",
									"-------modify---: " + r.getContents());
							currReport_.setImageData(r.getImageData());
							currReport_.setContents(mContent);
							setImage();
						}

					}
				});

				reports_.modifyReport(currReport_);
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

	private void deleteReport() {
		List<Future> list = new ArrayList<Future>();
		Future<?> Result = AndroidSoda.async(new Runnable() {
			
			@Override
			public void run() {
				reports_ = as.get(MaintenanceReports.class,
						MaintenanceReports.SVC_NAME);
				reports_.deleteReport(currReport_.getId());
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
	
	public void setImage() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] b = currReport_.getImageData();
				if (b != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0,
							b.length);
					photoView_.setImageBitmap(bitmap);
					photoView_.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					photoView_.setAdjustViewBounds(true);

				}
			}
		});
	}

	@Override
	public void connected(final AndroidSoda s) {

		this.as = s;
		

	}

	public void updateReports(List<MaintenanceReport> reports) {

	}

}
