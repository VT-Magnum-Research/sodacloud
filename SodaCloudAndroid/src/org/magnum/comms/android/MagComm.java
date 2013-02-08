package org.magnum.comms.android;

import java.util.UUID;

import org.magnum.soda.Soda;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.svc.PingSvc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MagComm extends Activity {

	private static final String TAG = MagComm.class.getName();

	private String me_ = UUID.randomUUID().toString();

	private Soda soda_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mag_comm);

		AndroidSoda.init(this, "10.0.1.8", 8081, new AndroidSodaListener() {
			
			@Override
			public void connected(AndroidSoda s) {
				soda_ = s;
				init();
			}
		});
	}

	public void init() {
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				PingSvc p = soda_.get(PingSvc.class, PingSvc.SVC_NAME);
				while(true){
					p.ping();
					try{
						Thread.sleep(5000);
					}catch(Exception e){break;}
				}
			}
		});
		t.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_mag_comm, menu);
		return true;
	}

}
