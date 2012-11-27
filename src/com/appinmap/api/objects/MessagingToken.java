package com.appinmap.api.objects;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;

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
	private List<String> deviceIds;

	@Persistent
	private String appVersion;
	
	@Persistent
	private String sdkVersion;
	
	@Persistent
	private Platform platform;
	
	@Persistent
	private boolean TestMode;
	
	@Persistent
	private long last_registration_time;
	
	@Persistent
	private List<String> tags;
	
	@Persistent
	private List<String> deviceSpecs;

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

	public List<String> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<String> deviceId) {
		this.deviceIds = deviceId;
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
	
	public String getTagsAsString() {
	    StringBuilder sb = new StringBuilder();
	    String loopDelim = "";
	    for(String s : getTags()) {
	        sb.append(loopDelim);
	        sb.append(s);            
	        loopDelim = ",";
	    }

	    return sb.toString();
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public List<String> getDeviceSpecs() {
		return deviceSpecs;
	}

	public void setDeviceSpecs(List<String> deviceSpecs) {
		this.deviceSpecs = deviceSpecs;
	}
	
	public boolean isTestMode() {
		return TestMode;
	}

	public void setTestMode(boolean testMode) {
		TestMode = testMode;
	}
	
	public static MessagingToken CreateMessagingToken(JSONObject object,PersistenceManager pm) throws JSONException {
		if(object != null) {
			MessagingToken token = new MessagingToken();
			
			Key key = KeyFactory.createKey(MessagingToken.class.getSimpleName(),object.getString("device_token"));
			
			MessagingToken messagingToken = null;
			try {
				messagingToken = pm.getObjectById(MessagingToken.class, key);
			} catch(JDOObjectNotFoundException e) {
				messagingToken = null;
			}
			
			if(messagingToken != null) {
				messagingToken.update(object);
				return messagingToken;
			} else {
				token.setKey(key);

				token.setApplicationId(object.getString("applicationId"));
				token.setBundleId(object.getString("bundleId"));
				
				JSONArray deviceIdArray = null;
				if(object.getString("deviceIds").getClass().equals(JSONArray.class))
					deviceIdArray = object.getJSONArray("deviceIds");
				else 
					deviceIdArray = new JSONArray(object.getString("deviceIds"));
				
				if(deviceIdArray != null) {
					for (int i = 0; i < deviceIdArray.length(); i++) {
						if(token.getDeviceIds() == null)
							token.setDeviceIds(new ArrayList<String>());
						token.getDeviceIds().add(deviceIdArray.getString(i));
					}
				}
				token.setAppVersion(object.getString("appVersion"));
				token.setSdkVersion(object.getString("sdkVersion"));
				
				if(object.has("testMode"))
					token.setTestMode(object.getBoolean("testMode"));
				else
					token.setTestMode(false);
				
				JSONArray deviceSpecsArray = null;
				if(object.getString("deviceSpecs").getClass().equals(JSONArray.class))
					deviceSpecsArray = object.getJSONArray("deviceSpecs");
				else 
					deviceSpecsArray = new JSONArray(object.getString("deviceSpecs"));

				if(deviceSpecsArray != null) {
					for (int i = 0; i < deviceSpecsArray.length(); i++) {
						if(token.getDeviceSpecs() == null)
							token.setDeviceSpecs(new ArrayList<String>());
						token.getDeviceSpecs().add(deviceSpecsArray.getString(i));
					}
				}
				
				token.setPlatform(Platform.GetPlatform(object.getInt("platform")));
				token.setLast_registration_time(object.getLong("time"));
				token.setActive(true);
			
				JSONArray tagArray = null;
				if(object.getString("tags").getClass().equals(JSONArray.class))
					tagArray = object.getJSONArray("tags");
				else 
					tagArray = new JSONArray(object.getString("tags"));
				if(tagArray != null) {
					for (int i = 0; i < tagArray.length(); i++) {
						if(token.getTags() == null)
							token.setTags(new ArrayList<String>());
					    String tag = tagArray.getString(i);
					    token.getTags().add(tag);
					}
				}
				
				// Push this to tokens task queue so that it can be processed by external systems like BrightPush
				Gson gson = new Gson();
				Queue q = QueueFactory.getQueue("tokens-create");
				q.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload(gson.toJson(token).toString()));
				
				return token;
			}
		}
		
		return null;	
	}
	
	public void update(JSONObject object) throws JSONException {
		if(object != null) {
			JSONArray deviceIdArray = null;
			if(object.getString("deviceIds").getClass().equals(JSONArray.class))
				deviceIdArray = object.getJSONArray("deviceIds");
			else 
				deviceIdArray = new JSONArray(object.getString("deviceIds"));
			
			if(deviceIdArray != null) {
				for (int i = 0; i < deviceIdArray.length(); i++) {
					String deviceId = deviceIdArray.getString(i);
					if(this.getDeviceIds() == null)
						this.setDeviceIds(new ArrayList<String>());
					if(!this.getDeviceIds().contains(deviceId))
						this.getDeviceIds().add(deviceId);
				}
			}
			this.setAppVersion(object.getString("appVersion"));
			this.setSdkVersion(object.getString("sdkVersion"));
			
			
			JSONArray deviceSpecsArray = null;
			if(object.getString("deviceSpecs").getClass().equals(JSONArray.class))
				deviceSpecsArray = object.getJSONArray("deviceSpecs");
			else 
				deviceSpecsArray = new JSONArray(object.getString("deviceSpecs"));

			if(deviceSpecsArray != null) {
				for (int i = 0; i < deviceSpecsArray.length(); i++) {
					if(this.getDeviceSpecs() == null)
						this.setDeviceSpecs(new ArrayList<String>());
					this.getDeviceSpecs().add(deviceSpecsArray.getString(i));
				}
			}
			
			if(object.has("active")) {
				this.setActive(object.getBoolean("active"));	
			}
			
			if(object.has("testMode"))
				this.setTestMode(object.getBoolean("testMode"));
			
			this.setLast_registration_time(object.getLong("time"));
			this.setPlatform(Platform.GetPlatform(object.getInt("platform")));
			
			JSONArray tagArray = null;
			if(object.getString("tags").getClass().equals(JSONArray.class))
				tagArray = object.getJSONArray("tags");
			else 
				tagArray = new JSONArray(object.getString("tags"));
			if(tagArray != null) {
				for (int i = 0; i < tagArray.length(); i++) {
					if(this.getTags() == null)
						this.setTags(new ArrayList<String>());
				    String tag = tagArray.getString(i);
				    this.getTags().add(tag);
				}
			}
			
			// Push this to tokens task queue so that it can be processed by external systems like BrightPush
			Gson gson = new Gson();
			Queue q = QueueFactory.getQueue("tokens-update");
			q.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload(gson.toJson(this).toString()));
		}	
	}
}
