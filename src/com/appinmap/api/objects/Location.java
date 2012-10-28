package com.appinmap.api.objects;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.json.JSONException;
import org.json.JSONObject;

@PersistenceCapable
@EmbeddedOnly
public class Location {
	
	@Persistent
	private double latitude;

	@Persistent
	private double longitude;
	
	@Persistent
	private double accuracy;
	
	@Persistent
	private double altitude;
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	public static Location GetLocation(JSONObject object) throws JSONException {
		if(object != null) {
			Location location = new Location();
			
			location.setLatitude(object.getDouble("latitude"));
			location.setLongitude(object.getDouble("longitude"));
			location.setAccuracy(object.getDouble("accuracy"));
			//We are ignoring altitude until there is a need for it
			
			return location;
		}
		
		return null;
	}
}
