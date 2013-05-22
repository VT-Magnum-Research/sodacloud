package org.magnum.soda.example.maint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.Callback;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MonitorZoneActivity extends Activity implements OnMapClickListener, OnMarkerClickListener, AndroidSodaListener{
	private String mHost;
	private GoogleMap mMap;
	private EditText radiusText;
	private Button createZoneButton;
	private double selectedLatitude;
	private double selectedLongitude;
	static final LatLng Blacksburg = new LatLng(37.225134, -80.425425);
	private List<MaintenanceReport> mReportList = new ArrayList<MaintenanceReport>();
	private AndroidSodaListener asl_ = null;
	private AndroidSoda as = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitorzone);
		radiusText = (EditText) findViewById(R.id.monitorzone_radius_edittext);
		createZoneButton = (Button) findViewById(R.id.CreateMonitorZone_Button);
		
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
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Blacksburg, 15));
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		int radius = Integer.valueOf(radiusText.getText().toString());
		
		CircleOptions circleOptions = new CircleOptions()
	    .center(new LatLng(37.222134, -80.425425))
	    .radius(100)
	    .fillColor(0x99000000)
	    .strokeWidth(5); // In meters
	    Circle circle = mMap.addCircle(circleOptions);
	
	    CircleOptions circleOptions2 = new CircleOptions()
	    .center(new LatLng(37.225134, -80.425425))
	    .radius(150)
	    .fillColor(0x99000000)
	    .strokeWidth(5); // In meters
	    Circle circle2 = mMap.addCircle(circleOptions2);


        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        
        AndroidSoda.init(this, mHost, 8081, asl_);
        List<LatLng> l= new ArrayList<LatLng>();
        l.add(new LatLng(37.223134, -80.425425));
        l.add(new LatLng(37.223134, -80.425625));
        l.add(new LatLng(37.224134, -80.425525));
	    for(int i = 0; i<mReportList.size(); i++){
	    	mMap.addMarker(new MarkerOptions()
	        .position(l.get(i))
	        .title(mReportList.get(i).getContents()));
	    }
        createZoneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
			}
			
        });

	}
	 @Override
	 public void onMapClick(LatLng point) {
		 Log.i("Soda", "LatLon"+point);
		 selectedLatitude = point.latitude;
		 selectedLongitude = point.longitude;
		 int r = Integer.valueOf(radiusText.getText().toString());
			CircleOptions circleOptions = new CircleOptions()
		    .center(new LatLng(selectedLatitude, selectedLongitude))
		    .radius(r)			// In meters
		    .fillColor(0x99000000)
		    .strokeWidth(5); 
		  Circle circle = mMap.addCircle(circleOptions);
	  }
	 @Override
	 public boolean onMarkerClick(Marker marker) {
		 return true;
	  }
	 
	 private void getReports() {
			List<Future> list = new ArrayList<Future>();

			Future<?> Result = AndroidSoda.async(new Runnable() {
				@Override
				@SodaInvokeInUi
				public void run() {
					if (as != null) {

						Log.e("conected", "------------------------------------");
						
						MaintenanceReports reportHandle = as.get(
								MaintenanceReports.class,
								MaintenanceReports.SVC_NAME);
						reportHandle
								.getReports(new Callback<List<MaintenanceReport>>() {
									//@SodaInvokeInUi
									public void handle(List<MaintenanceReport> arg0) {
										mReportList = arg0;
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
	 @Override
		public void connected(AndroidSoda s) {

			this.as = s;
			getReports();
			
		}
	
}
