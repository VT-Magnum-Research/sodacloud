package org.magnum.soda.example.maint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;


public class WatchZoneActivity extends MapActivity{
	private MapView mapView;
	private MapController mMapController; 
	private double mPosLat = 37.225134;
	private double mPosLong = -80.425425;
	private int selectedLatitude;
	private int selectedLongitude;
	
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
	
	public class MarkerOverlay extends Overlay {

	    Geocoder geoCoder = null;

	    public MarkerOverlay() {
	        super();
	    }


	    @Override
	    public boolean onTap(GeoPoint geoPoint, MapView mapView){
	        selectedLatitude = geoPoint.getLatitudeE6(); 
	        selectedLongitude = geoPoint.getLongitudeE6();
	        return super.onTap(geoPoint,mapView);
	    }

	    @Override
	    public void draw(Canvas canvas, MapView mapV, boolean shadow){

	        if(shadow){
	            Projection projection = mapV.getProjection();
	            Point pt = new Point();
	        //    projection.toPixels(globalGeoPoint,pt);

	            GeoPoint newGeos = new GeoPoint(selectedLatitude+(100),selectedLongitude); // adjust your radius accordingly
	            Point pt2 = new Point();
	            projection.toPixels(newGeos,pt2);
	            float circleRadius = Math.abs(pt2.y-pt.y);

	            Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	            circlePaint.setColor(0x30000000);
	            circlePaint.setStyle(Style.FILL_AND_STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, circlePaint);

	            circlePaint.setColor(0x99000000);
	            circlePaint.setStyle(Style.STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, circlePaint);

	            Bitmap markerBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_launcher);
	            canvas.drawBitmap(markerBitmap,pt.x,pt.y-markerBitmap.getHeight(),null);

	            super.draw(canvas,mapV,shadow);
	        }
	    }
	}
}
