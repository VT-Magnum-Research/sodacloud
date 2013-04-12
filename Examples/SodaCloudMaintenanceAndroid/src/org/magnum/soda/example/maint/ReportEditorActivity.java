/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.magnum.soda.Callback;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ReportEditorActivity extends Activity implements AndroidSodaListener {


	//host
	private String mHost="172.31.55.100";
	private EditText reportContent_;
	private Button saveButton_;
	private Button deleteButton_;
	private Button bindLocationButton_;
	private Button bindQRButton_;
	private ImageButton photoView_;
	
	private MaintenanceReports reports_;
	private ReportParcelable current;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
		photoView_ = (ImageButton)findViewById(R.id.editReportView);
		Log.d("SODA", "ReportEditorActivity.");
		
		Intent callingintent = getIntent();
        String content = callingintent.getStringExtra("description");
		reportContent_.setText(content);

	      if(getIntent().hasExtra("mReport"))
	      {
	    	  current= getIntent().getExtras().getParcelable("mReport");
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
		Log.d("SODA", "ReportEditorActivity.2 ");
		saveButton_.setOnClickListener(new View.OnClickListener() {
			
			final String content = reportContent_.getText().toString();
			
			@Override
			public void onClick(View v) {
				AndroidSoda.async(new Runnable() {
					
					@Override
					public void run() {
						MaintenanceReport r = new MaintenanceReport();
						r.setContents(content);
						reports_.addReport(r);
					}
				});
			}
		});
		
		AndroidSoda.init(this, mHost, 8081, this);
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
				Log.d("SODA", "Maintenance report added: " + r.getContents());
				Toast.makeText(ReportEditorActivity.this, "New report:"+r.getContents(), Toast.LENGTH_SHORT).show();
			}
		});

	}
	
	public void updateReports(List<MaintenanceReport> reports){
		
	}


	

}
