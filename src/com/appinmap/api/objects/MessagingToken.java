package com.appinmap.api.objects;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class MessagingToken {
	// This can be APNS device token or
	// Google's Cloud Messaging device token
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
    private String applicationId;
	
	private String bundleId;
	
	@Persistent
	private String deviceId;
	
	@Persistent
	private DeviceIdType deviceIdType;

	@Persistent
	private String appVersion;
	
	@Persistent
	private String sdkVersion;
	
	@Persistent
	private long last_registration_time;
	
	@Persistent
	private List<String> tags;
	
	@Persistent
	private boolean active;

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public DeviceIdType getDeviceIdType() {
		return deviceIdType;
	}

	public void setDeviceIdType(DeviceIdType deviceIdType) {
		this.deviceIdType = deviceIdType;
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

	public long getLast_registration_time() {
		return last_registration_time;
	}

	public void setLast_registration_time(long last_registration_time) {
		this.last_registration_time = last_registration_time;
	}
	
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public static MessagingToken CreateMessagingToken(JSONObject object) throws JSONException {
		if(object != null) {
			MessagingToken token = new MessagingToken();
			
			Key key = KeyFactory.createKey(MessagingToken.class.getSimpleName(),object.getString("device_token"));
			token.setKey(key);

			token.setApplicationId(object.getString("applicationId"));
			token.setBundleId(object.getString("bundleId"));
			token.setDeviceId(object.getString("deviceId"));
			token.setDeviceIdType(DeviceIdType.GetDeviceIdType(object.getInt("deviceIdType")));
			token.setAppVersion(object.getString("appVersion"));
			token.setSdkVersion(object.getString("sdkVersion"));
			token.setLast_registration_time(object.getLong("time"));
			token.setActive(true);
		
			org.json.JSONArray tagArray = object.getJSONArray("tags");
			if(tagArray != null) {
				for (int i = 0; i < tagArray.length(); i++) {
				    String tag = tagArray.getString(i);
				    token.getTags().add(tag);
				}
			}
			
			return token;
		}
		
		return null;	
	}
	
	public void update(JSONObject object) throws JSONException {
		if(object != null) {
			this.setDeviceId(object.getString("deviceId"));
			this.setDeviceIdType(DeviceIdType.GetDeviceIdType(object.getInt("deviceIdType")));
			this.setAppVersion(object.getString("appVersion"));
			this.setSdkVersion(object.getString("sdkVersion"));
			this.setActive(object.getBoolean("active"));
			
			org.json.JSONArray tagArray = object.getJSONArray("tags");
			if(tagArray != null) {
				for (int i = 0; i < tagArray.length(); i++) {
				    String tag = tagArray.getString(i);
				    this.getTags().add(tag);
				}
			}
		}	
	}
}
