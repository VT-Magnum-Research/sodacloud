package org.magnum.soda.example;

import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.example.sodaclient.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AndroidSodaListener{
    private TextView status;
    private AndroidSoda as_;
    private AndroidSodaListener asl_; 
    private Reports reports;
    private Report r;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		status = (TextView) findViewById(R.id.textView_status);
		AndroidSoda.init(this, "192.168.173.1", 8081, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
   public void getReport(){
	   AndroidSoda.async(new Runnable() {
				@Override
				public void run() {
					reports = as_.get(Reports.class,Reports.SVC_NAME);
					r = reports.getReports().get(0);
					updateStatus(r.getContent());

					reports.addListener(new ReportsListener() {
						public void reportAdded(Report r){

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
   @SodaInvokeInUi
   public void updateStatus(String content){
	   Log.d("SODA","In updateStatus: "+ r.getContent());
	   status.setText(content);
   }
	@Override
	public void connected(AndroidSoda soda) {
		Log.d("SODA","connected ");
		this.as_ = soda;
		getReport();
	}


}