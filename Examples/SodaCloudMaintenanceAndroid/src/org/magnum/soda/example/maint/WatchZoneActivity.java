package org.magnum.soda.example.maint;

import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;


public class WatchZoneActivity extends MapActivity{
	private MapView mapView;
	private MapController mMapController; 
	private double mPosLat = 37.225134;
	private double mPosLong = -80.425425;
	
	@Override
	protected boolean isRouteDisplayed() { 
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchzone);
		try{
			mapView = (MapView) findViewById(R.id.mapview);
			mapView.setBuiltInZoomControls(true);
			GeoPoint mGeoPoint = new GeoPoint((int) (mPosLat * 1000000), (int) (mPosLong * 1000000));
			mMapController = mapView.getController();
			mMapController.animateTo(mGeoPoint); 
			mMapController.setZoom(17); 

			Log.i("Soda", "Lat"+mPosLat+"Long"+mPosLong);


		}catch (Exception e) {
			e.printStackTrace();

		}
	}
}
