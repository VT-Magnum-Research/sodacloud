package org.magnum.soda.example;

import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.example.sodaclient.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AndroidSodaListener{
	
    private TextView status;
    private Button addReportButton_;
    private AndroidSoda as_;
    private AndroidSodaListener asl_; 
    private Reports reports;
    private Report r;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		status = (TextView) findViewById(R.id.textView_status);
		addReportButton_ = (Button) findViewById(R.id.button_add);
		addReportButton_.setOnClickListener(new View.OnClickListener() {	
			@Override 
			public void onClick(View v) {
				addReport();
			}
		});
		 
		 AndroidSoda.init(this, "192.168.173.1", 8081, this);
		// AndroidSoda.init(this, "10.0.2.2", 8081, this); 

	}

	@Override
	public void connected(AndroidSoda soda) {
		Log.d("SODA","connected ");
		
		this.as_ = soda;
		getReport();
	}
	
   public void getReport(){
	   AndroidSoda.async(new Runnable() {
				@Override
				public void run() {
					reports = as_.get(Reports.class,Reports.SVC_NAME);
					r = reports.getReports().get(0);
					
					as_.inUi(new Runnable() {
						@Override
						public void run() {
							updateStatus(r.getContent());
						}
					});

					reports.addListener(new ReportsListener() {
						@Override
						@SodaInvokeInUi
						public void reportAdded(Report r){
							Log.d("SODA","reportAdded:#4 " + System.currentTimeMillis());
							Log.d("SODA","reportAdded: "+ r.getContent());
							status.setText(r.getContent());
							Toast.makeText(MainActivity.this, "Add new report:" + r.getContent(),
							 Toast.LENGTH_SHORT).show();
						}
						@Override
						@SodaInvokeInUi
						public void reportchanged(Report r) {
							Log.d("SODA","report modified: "+ r.getContent());
							status.setText(r.getContent());
							Toast.makeText(MainActivity.this, "Modified report:"+r.getContent(),
							 Toast.LENGTH_SHORT).show();
						}
					});

				}
			});

    }
   
   
   public void addReport(){
	   Log.d("SODA","addReport#1: " + System.currentTimeMillis());
	   AndroidSoda.async(new Runnable() {
				@Override
				public void run() {
					reports = as_.get(Reports.class,Reports.SVC_NAME);
					Log.d("SODA","listeners size: "+ reports.getListeners().size());
					Report r = new Report("First report.");
					reports.addReport(r);
					
				}
			});

    }
   
   
   @SodaInvokeInUi
   public void updateStatus(String content){
	   Log.d("SODA","In updateStatus: "+ r.getContent());
	   status.setText(content);
   }


}