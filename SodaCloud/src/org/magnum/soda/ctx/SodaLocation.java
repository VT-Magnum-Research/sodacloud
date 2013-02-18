/*****************************************************************************
 * Copyright [2013] [Jules White]                                            *
 *                                                                           *
 *  Licensed under the Apache License, Version 2.0 (the "License");          *
 *  you may not use this file except in compliance with the License.         *
 *  You may obtain a copy of the License at                                  *
 *                                                                           *
 *      http://www.apache.org/licenses/LICENSE-2.0                           *
 *                                                                           *
 *  Unless required by applicable law or agreed to in writing, software      *
 *  distributed under the License is distributed on an "AS IS" BASIS,        *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 *  See the License for the specific language governing permissions and      *
 *  limitations under the License.                                           *
 ****************************************************************************/
package org.magnum.soda.ctx;

import org.magnum.soda.SodaContext;

public class SodaLocation implements SodaContext {
	
	private double latitude_;
	private double longitude_;
	private Proximity proximity_;
	private Accuracy accuracy_;
	private PositionUpdates positionUpdates_;
	
	public enum Proximity {
		ZERO, TWENTY_METERS
	}
	
	public enum Accuracy {
		FINE
	}
	
	public enum PositionUpdates {
		EVERY_HUNDRED_METERS
	}

	public static SodaLocation at(double lat, double lon){
		SodaLocation sodaLocation = new SodaLocation();
		
		try {	
			sodaLocation.latitude_ = lat;
			sodaLocation.longitude_ = lon;
			sodaLocation.proximity_ = Proximity.ZERO;
			
		} catch (Exception e){
			throw new RuntimeException();		
		}
		return sodaLocation;
	}
	
	public static SodaLocation within(Proximity range){
		SodaLocation sodaLocation = new SodaLocation();
		
		try {	
			sodaLocation.proximity_ = range;
			
		} catch (Exception e){
			throw new RuntimeException();		
		}
		return sodaLocation;
	}
	
	public SodaLocation of(double lat, double lon){

		try {	
			latitude_ = lat;
			longitude_ = lon;
		} catch (Exception e){
			throw new RuntimeException();		
		}
		return this;
	}
	
	public SodaLocation atAccuracy(Accuracy accuracy){
		try {	
			this.accuracy_ = accuracy;
			
		} catch (Exception e){
			throw new RuntimeException();		
		}
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		SodaLocation l = (SodaLocation)obj;
		
		double range = proximityToNum(l.proximity_) + proximityToNum(this.proximity_);
		double dist = distFrom(l.latitude_,l.longitude_,this.latitude_,this.longitude_);
		if(dist <= range)
			return true;
		else 
			return false;		
		
	}
	
	public double proximityToNum(Proximity proximity){
		if(proximity == Proximity.TWENTY_METERS)
			return 20;
		else if(proximity == Proximity.ZERO)
		    return 0;
		else
			return -1;
	}
	
	public double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 63710100;   //in meters
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return dist;
	    }
}

