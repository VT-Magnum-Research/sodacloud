package org.magnum.soda.example.maint;
import org.magnum.soda.proxy.SodaByValue;

@SodaByValue
public class MonitorZone {

	private double latitude_;
	private double longitude_;
	private int radius_;
	private String ownerName_;
	public MonitorZone(){
		
	}
	public MonitorZone(double latitude, double longitude, int radius, String ownerName){
		this.latitude_ = latitude;
		this.longitude_ = longitude;
		this.radius_ = radius;
		this.ownerName_ = ownerName;
	}
	public double getLatitude_() {
		return latitude_;
	}
	public void setLatitude_(double latitude_) {
		this.latitude_ = latitude_;
	}
	public double getLongitude_() {
		return longitude_;
	}
	public void setLongitude_(double longitude_) {
		this.longitude_ = longitude_;
	}
	public double getRadius_() {
		return radius_;
	}
	public void setRadius_(int radius_) {
		this.radius_ = radius_;
	}
	public String getOwnerName_() {
		return ownerName_;
	}
	public void setOwnerName_(String ownerName_) {
		this.ownerName_ = ownerName_;
	}
	
	




}