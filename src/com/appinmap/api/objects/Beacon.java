package com.appinmap.api.objects;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class Beacon {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private long time;
	
	@Persistent
	@Embedded
	private Location location;

	public Key getKey() {
		return key;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public static Beacon CreateBeacon(JSONObject object,PersistenceManager pm) throws JSONException {
		if(object != null) {
			Beacon beacon = new Beacon();
			
	        beacon.setTime(object.getLong("time"));
	        beacon.setLocation(Location.GetLocation(object.getJSONObject("location")));
	        
			String sessionId = object.getString("sessionId");
	        Key key = KeyFactory.createKey(Session.class.getSimpleName(), sessionId);
	        Session session = pm.getObjectById(Session.class, key);
	        session.getBeacons().add(beacon);
	        
			return beacon;
		}
		
		return null;	
	}
	
	public static Beacon EndSession(JSONObject object,PersistenceManager pm) throws JSONException {
		if(object != null) {
			Beacon beacon = new Beacon();
			
	        beacon.setTime(object.getLong("time"));
	        beacon.setLocation(Location.GetLocation(object.getJSONObject("location")));
	        
			String sessionId = object.getString("sessionId");
	        Key key = KeyFactory.createKey(Session.class.getSimpleName(), sessionId);
	        Session session = pm.getObjectById(Session.class, key);
	        session.setEndTime(beacon.getTime());
	        session.setEndLocation(beacon.getLocation());
	        session.getBeacons().add(beacon);
	        
			return beacon;
		}
		
		return null;	
	}
}
