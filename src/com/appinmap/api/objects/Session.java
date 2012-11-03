package com.appinmap.api.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Column;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class Session {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
    private String applicationId;

	@Persistent
    private Set<Beacon> beacons;

	private String bundleId;
	
	@Persistent
	private List<String> deviceIds;

	@Persistent
	private String appVersion;
	
	@Persistent
	private String sdkVersion;
	
	@Persistent
	private long startTime;
	
	@Persistent
	private long endTime;
	
	@Persistent
	private List<String> tags;

	@Persistent
	@Embedded(members = {
        @Persistent(name="latitude", columns=@Column(name="beginLatitude")),
        @Persistent(name="longitude", columns=@Column(name="beginLongitude")),
        @Persistent(name="accuracy", columns=@Column(name="beginAccuracy")),
        @Persistent(name="altitude", columns=@Column(name="beginAltitude")),
	})
	private Location startLocation;

	@Persistent
	@Embedded(members = {
        @Persistent(name="latitude", columns=@Column(name="endLatitude")),
        @Persistent(name="longitude", columns=@Column(name="endLongitude")),
        @Persistent(name="accuracy", columns=@Column(name="endAccuracy")),
        @Persistent(name="altitude", columns=@Column(name="endAltitude")),
    })
	private Location endLocation;
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public List<String> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<String> deviceIds) {
		this.deviceIds = deviceIds;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public Location getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

	public Location getEndLocation() {
		return endLocation;
	}

	public void setEndLocation(Location endLocation) {
		this.endLocation = endLocation;
	}
	
	public Set<Beacon> getBeacons() {
		return beacons;
	}

	public void setBeacons(Set<Beacon> beacons) {
		this.beacons = beacons;
	}
	
	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	public static Session CreateSession(JSONObject object) throws JSONException {
		if(object != null) {
			Session session = new Session();
			
			Key key = KeyFactory.createKey(Session.class.getSimpleName(),object.getString("sessionId"));
			session.setKey(key);

			session.setApplicationId(object.getString("applicationId"));
			session.setBundleId(object.getString("bundleId"));
			
			JSONArray deviceIdArray = null;
			if(object.getString("deviceIds").getClass().equals(JSONArray.class))
				deviceIdArray = object.getJSONArray("deviceIds");
			else 
				deviceIdArray = new JSONArray(object.getString("deviceIds"));

			if(deviceIdArray != null) {
				for (int i = 0; i < deviceIdArray.length(); i++) {
					if(session.getDeviceIds() == null)
						session.setDeviceIds(new ArrayList<String>());
				    session.getDeviceIds().add(deviceIdArray.getString(i));
				}
			}
			session.setAppVersion(object.getString("appVersion"));
			session.setSdkVersion(object.getString("sdkVersion"));
			session.setStartTime(object.getLong("time"));
			session.setStartLocation(Location.GetLocation(object.getJSONObject("location")));

			JSONArray tagArray = null;
			if(object.getString("tags").getClass().equals(JSONArray.class))
				tagArray = object.getJSONArray("tags");
			else 
				tagArray = new JSONArray(object.getString("tags"));
			
			if(tagArray != null) {
				for (int i = 0; i < tagArray.length(); i++) {
				    String tag = tagArray.getString(i);
					if(session.getTags() == null)
						session.setTags(new ArrayList<String>());
				    session.getTags().add(tag);
				}
			}
			
			return session;
		}
		
		return null;	
	}
}
