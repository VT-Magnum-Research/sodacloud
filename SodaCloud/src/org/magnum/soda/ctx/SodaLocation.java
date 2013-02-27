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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.magnum.soda.SodaContext;

public class SodaLocation implements SodaContext {

	private double latitude_;
	private double longitude_;
	private String geohash_;
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

	public static SodaLocation at(double lat, double lon) {
		SodaLocation sodaLocation = new SodaLocation();

		try {
			sodaLocation.latitude_ = lat;
			sodaLocation.longitude_ = lon;
			sodaLocation.proximity_ = Proximity.ZERO;
            sodaLocation.geohash_ = encode(lat,lon);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return sodaLocation;
	}
	
	public static SodaLocation at(String geohash) {
		SodaLocation sodaLocation = new SodaLocation();

		try {
			sodaLocation.geohash_ = geohash;
			sodaLocation.proximity_ = Proximity.ZERO;
			double[] latlon = decode(geohash);
			sodaLocation.latitude_ = latlon[0];
			sodaLocation.longitude_ = latlon[1];
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return sodaLocation;
	}

	public static SodaLocation within(Proximity range) {
		SodaLocation sodaLocation = new SodaLocation();

		try {
			sodaLocation.proximity_ = range;

		} catch (Exception e) {
			throw new RuntimeException();
		}
		return sodaLocation;
	}

	public SodaLocation of(double lat, double lon) {

		try {
			latitude_ = lat;
			longitude_ = lon;
			geohash_ = encode(lat,lon);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return this;
	}
	
	public SodaLocation of(String geohash) {

		try {
			geohash_ = geohash;
			double[] latlon = decode(geohash);
			latitude_ = latlon[0];
			longitude_ = latlon[1];
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return this;
	}

	public SodaLocation atAccuracy(Accuracy accuracy) {
		try {
			this.accuracy_ = accuracy;

		} catch (Exception e) {
			throw new RuntimeException();
		}
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		SodaLocation l = (SodaLocation) obj;

		double range = proximityToNum(l.proximity_)
				+ proximityToNum(this.proximity_);
		double dist = distFrom(l.latitude_, l.longitude_, this.latitude_,
				this.longitude_);
		if (dist <= range)
			return true;
		else
			return false;

	}
	
	public double proximityToNum(Proximity proximity) {
		if (proximity == Proximity.TWENTY_METERS)
			return 20;
		else if (proximity == Proximity.ZERO)
			return 0;
		else
			return -1;
	}

	public double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 63710100; // in meters
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist;
	}

	private static int numbits = 6 * 5;
	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	final static HashMap<Character, Integer> lookup = new HashMap<Character, Integer>();
	static {
		int i = 0;
		for (char c : digits)
			lookup.put(c, i++);
	}
    // convert from geohash string to lat,lon double
	public static double[] decode(String geohash) {
		StringBuilder buffer = new StringBuilder();
		for (char c : geohash.toCharArray()) {

			int i = lookup.get(c) + 32;
			buffer.append(Integer.toString(i, 2).substring(1));
		}

		BitSet lonset = new BitSet();
		BitSet latset = new BitSet();

		// even bits
		int j = 0;
		for (int i = 0; i < numbits * 2; i += 2) {
			boolean isSet = false;
			if (i < buffer.length())
				isSet = buffer.charAt(i) == '1';
			lonset.set(j++, isSet);
		}

		// odd bits
		j = 0;
		for (int i = 1; i < numbits * 2; i += 2) {
			boolean isSet = false;
			if (i < buffer.length())
				isSet = buffer.charAt(i) == '1';
			latset.set(j++, isSet);
		}

		double lon = decode(lonset, -180, 180);
		double lat = decode(latset, -90, 90);

		return new double[] { lat, lon };
	}

	private static double decode(BitSet bs, double floor, double ceiling) {
		double mid = 0;
		for (int i = 0; i < bs.length(); i++) {
			mid = (floor + ceiling) / 2;
			if (bs.get(i))
				floor = mid;
			else
				ceiling = mid;
		}
		return mid;
	}

	public static String encode(double lat, double lon) {
		BitSet latbits = getBits(lat, -90, 90);
		BitSet lonbits = getBits(lon, -180, 180);
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < numbits; i++) {
			buffer.append((lonbits.get(i)) ? '1' : '0');
			buffer.append((latbits.get(i)) ? '1' : '0');
		}
		return base32(Long.parseLong(buffer.toString(), 2));
	}

	private static BitSet getBits(double lat, double floor, double ceiling) {
		BitSet buffer = new BitSet(numbits);
		for (int i = 0; i < numbits; i++) {
			double mid = (floor + ceiling) / 2;
			if (lat >= mid) {
				buffer.set(i);
				floor = mid;
			} else {
				ceiling = mid;
			}
		}
		return buffer;
	}

	public static String base32(long i) {
		char[] buf = new char[65];
		int charPos = 64;
		boolean negative = (i < 0);
		if (!negative)
			i = -i;
		while (i <= -32) {
			buf[charPos--] = digits[(int) (-(i % 32))];
			i /= 32;
		}
		buf[charPos] = digits[(int) (-i)];

		if (negative)
			buf[--charPos] = '-';
		return new String(buf, charPos, (65 - charPos));
	}

}
