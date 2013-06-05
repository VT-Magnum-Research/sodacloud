/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class ReportEditorActivity extends Activity implements AndroidSodaListener {


	//host
	private String mHost;
	private EditText reportContent_;
	private TextView creatorText_;
	private TextView createtimeText_;
	private Button saveButton_;
	private Button deleteButton_;
	private Button bindLocationButton_;
	private Button bindQRButton_;
	private ImageButton photoView_;
	
	private MaintenanceReports reports_;
	private MaintenanceReport currReport_;
	private ReportParcelable current;
	private static final int STATIC_INTEGER_VALUE=10;

	private String mContent = null;
	private byte[] mImageData=null;
	

	private AndroidSoda as = null;
	private AndroidSodaListener asl_ = null;
	Context ctx_ = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		asl_ = this;

		try{
		setContentView(R.layout.report_form);
		
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
		
		reportContent_ = (EditText)findViewById(R.id.reportEditText);
		saveButton_ = (Button)findViewById(R.id.saveReportButton);
		deleteButton_ = (Button)findViewById(R.id.DeleteButton);
		bindLocationButton_ = (Button)findViewById(R.id.bindLocationButton);
		bindQRButton_ = (Button)findViewById(R.id.BindQR);
		creatorText_ = (TextView)findViewById(R.id.textView_creatorID);
		createtimeText_ = (TextView)findViewById(R.id.textView_createTime);
		photoView_ = (ImageButton)findViewById(R.id.editReportView);
		
		Intent callingintent = getIntent();
        String content = callingintent.getStringExtra("description");
		reportContent_.setText(content);

	      if(getIntent().hasExtra("mReport"))
	      {
	    	  current= getIntent().getExtras().getParcelable("mReport");
	    	  currReport_=current.getReport();
	    	  String creator = currReport_.getCreatorId();
	    	  creatorText_.setText(creator);
	    	  
	    	  Date createtime = currReport_.getCreateTime_();
	    	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.mm.dd  HH:mm:ss");
	      	  createtimeText_.setText(sdf.format(createtime));
	      	
	    	  byte[] b=current.getReport().getImageData();
	    	 if(b!=null)
	    		 {
	    		 Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
					photoView_.setImageBitmap(bitmap);
					photoView_.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					photoView_.setAdjustViewBounds(true);

	    		 }
	      }
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
			mImageData = getBytes(getBmp(photoView_.getDrawable()));
				
				if (mContent != null && mImageData !=null) {
					
					currReport_.setContents(mContent);
					currReport_.setImageData(mImageData);

					AndroidSoda.init(ctx_, mHost, 8081, asl_);
				}
			}
		});
		
	}
	
	private Bitmap getBmp(Drawable drawable)
	{
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
	
	private byte[] getBytes(Bitmap bmp)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        bmp.compress(CompressFormat.JPEG, 10 /*ignored for PNG*/, bos); 
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
				photoView_
						.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				photoView_.setAdjustViewBounds(true);

			}
			break;
		}
		}
		}
	
	private void updateReport()
	{
		List<Future> list = new ArrayList<Future>();

		Future<?> Result =AndroidSoda.async(new Runnable() {
			
			@Override
			public void run() {
				
			
				reports_ = as.get(MaintenanceReports.class, MaintenanceReports.SVC_NAME);
	

				reports_.addListener(new MaintenanceListener() {

					@SodaInvokeInUi
					public void reportAdded(final MaintenanceReport r) {
							}

					@Override
					public void reportchanged(MaintenanceReport r) {
						// TODO Auto-generated method stub
						Log.d("SODA", "Maintenance report modified: " + r.getContents());
					//	Toast.makeText(ReportEditorActivity.this, "Modified report:"+r.getContents(), Toast.LENGTH_SHORT).show();
						if(mContent.equals(r.getContents()))
						{
							
							Log.d("SODA", "-------modify---: " + r.getContents());
							currReport_.setImageData(r.getImageData());
							setImage();
						}
						
					}
				});
		
				MaintenanceReport r = new MaintenanceReport();
				r.setContents(mContent);
				Log.e("CreateRepostActibity","length "+mImageData.length);
				r.setImageData(mImageData);
				r.setId(2);
				r.setCreatorId("aks1");

				

				System.out.println("========Input Content======== :"+mContent);
				reports_.modifyReport(currReport_);
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

public void setImage()
{
	
	runOnUiThread(new Runnable()
	{
	  
		@Override
		public void run() {
			// TODO Auto-generated method stub
			byte[] b=currReport_.getImageData();
		 	 if(b!=null)
		 		 {
		 		 Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
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
		updateReport();
		
	}
	
	public void updateReports(List<MaintenanceReport> reports){
		
	}


	

}
