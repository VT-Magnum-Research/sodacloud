package org.magnum.soda.example.maint;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;


public class WatchZoneActivity extends MapActivity{
	private MapView mapView;
	private EditText radiusText;
	private Button createZoneButton;
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
			radiusText = (EditText) findViewById(R.id.monitorzone_radius_edittext);
			createZoneButton = (Button) findViewById(R.id.CreateMonitorZone_Button);
			
			mapView.setBuiltInZoomControls(true);
			GeoPoint mGeoPoint = new GeoPoint((int) (mPosLat * 1000000), (int) (mPosLong * 1000000));
			mMapController = mapView.getController();
			mMapController.animateTo(mGeoPoint); 
			mMapController.setZoom(17); 

			Log.i("Soda", "Lat"+mPosLat+"Long"+mPosLong);
			int radius = Integer.valueOf(radiusText.getText().toString());
			
			MonitorZone zone1 = new MonitorZone(new GeoPoint((int) (37.225134 * 1000000), (int) (-80.425425* 1000000)),100);
			MonitorZone zone2 = new MonitorZone(new GeoPoint((int) (37.228184 * 1000000), (int) (-80.425425* 1000000)),150);
			
			MarkerOverlay mapOverlay = new MarkerOverlay(radius);
	        List<Overlay> listOfOverlays = mapView.getOverlays();
	        listOfOverlays.add(mapOverlay);        
            
	        listOfOverlays.add(zone1);
	        listOfOverlays.add(zone2);
	        createZoneButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int r = Integer.valueOf(radiusText.getText().toString());
					GeoPoint p = new GeoPoint((int) (selectedLatitude * 1000000), (int) (selectedLongitude * 1000000));
					MonitorZone zone = new MonitorZone(p,r);
				}
				
	        });
	       
		}catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	public class MarkerOverlay extends Overlay {

	    Geocoder geoCoder = null;
	    private int radius_;
	    
	    public MarkerOverlay() {
	        super();
	    }

	    public MarkerOverlay(int radius) {
	        super();
	        radius_ = radius;
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
	            GeoPoint globalGeoPoint = new GeoPoint(selectedLatitude,selectedLongitude);
	            projection.toPixels(globalGeoPoint,pt);
	            
	            double RadiusInPixel = projection.metersToEquatorPixels(radius_) * (1/Math.cos((float) Math.toRadians(selectedLatitude)));
	            
	            Log.d("SODA","selectedlat£º"+selectedLatitude+"  long:"+selectedLongitude+"   RadiusInPixel:"+RadiusInPixel);

	            Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	            circlePaint.setColor(0x30000000);
	            circlePaint.setStyle(Style.FILL_AND_STROKE);
	            circlePaint.setStrokeWidth(5);
	            canvas.drawCircle((float)pt.x, (float)pt.y, (float)RadiusInPixel, circlePaint);

	            circlePaint.setColor(0x99000000);
	            circlePaint.setStyle(Style.STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, (float)RadiusInPixel, circlePaint);

	            Bitmap markerBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_launcher);
	            canvas.drawBitmap(markerBitmap,pt.x,pt.y-markerBitmap.getHeight(),null);

	            super.draw(canvas,mapV,shadow);
	        }
	    }
	}
	
	public class MonitorZone extends Overlay {

	    Geocoder geoCoder = null;
	    private int radius_;
	    private GeoPoint center;
	    
	    public MonitorZone() {
	        super();
	    }

	    public MonitorZone(GeoPoint geoPoint, int radius) {
	        super();
	        radius_ = radius;
	        center = geoPoint;
	    }
	   
	    @Override
	    public void draw(Canvas canvas, MapView mapV, boolean shadow){

	        if(shadow){
	        	
	        	
	            Projection projection = mapV.getProjection();
	            Point pt = new Point();
	            projection.toPixels(center,pt);
	            
	            double RadiusInPixel = projection.metersToEquatorPixels(radius_) * (1/Math.cos((float) Math.toRadians(selectedLatitude)));
	            
	            Log.d("SODA","centerlat£º"+selectedLatitude+"  long:"+selectedLongitude+"   RadiusInPixel:"+RadiusInPixel);
	            

	            Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	            circlePaint.setColor(0x30000000);
	            circlePaint.setStyle(Style.FILL_AND_STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, (float)RadiusInPixel, circlePaint);

	            circlePaint.setColor(0x99000000);
	            circlePaint.setStyle(Style.STROKE);
	            canvas.drawCircle((float)pt.x, (float)pt.y, (float)RadiusInPixel, circlePaint);

	           // Bitmap markerBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_launcher);
	           // canvas.drawBitmap(markerBitmap,pt.x,pt.y-markerBitmap.getHeight(),null);

	            super.draw(canvas,mapV,shadow);
	        }
	    }
	}
}
