/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import java.util.concurrent.ExecutorService;

import org.magnum.soda.Soda;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReportEditorActivity extends Activity implements AndroidSodaListener {

	private EditText reportContent_;
	private Button reportButton_;
	
	
	private MaintenanceReports reports_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.report_form);
		
		reportContent_ = (EditText)findViewById(R.id.reportEditText);
		reportButton_ = (Button)findViewById(R.id.addReportButton);
		
		reportButton_.setOnClickListener(new View.OnClickListener() {
			
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
		
		AndroidSoda.init(this, "10.0.1.8", 8081, this);
	}

	@Override
	public void connected(final AndroidSoda s) {
		reports_ = s.get(MaintenanceReports.class,
				MaintenanceReports.SVC_NAME);

		reports_.addListener(new MaintenanceListener() {

			@Override
			public void reportAdded(final MaintenanceReport r) {
				Log.d("SODA", "Maintenance report added: " + r.getContents());
				s.inUi(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(ReportEditorActivity.this, "New report:"+r.getContents(), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

	}


	

}
