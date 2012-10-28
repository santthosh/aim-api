package com.appinmap.api.objects;

import java.util.UUID;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class Application {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String name;
	
	@Persistent
	private String bundleId;
	
	@Persistent
	private Platform platform;
	
	@Persistent
	private String secret;
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public JSONObject toJSONObject() throws JSONException {
		JSONObject object = new JSONObject();
		
		object.put("applicationId", this.key.getName());
		object.put("bundleId", this.getBundleId());
		object.put("name", this.getName());
		object.put("platform", Platform.GetIntValue(this.getPlatform()));
		object.put("secret", this.getSecret());
		
		return object;
	}
	
	public static Application CreateApplication(JSONObject object) throws JSONException {
		if(object != null) {
			Application application = new Application();
			
			Key key = KeyFactory.createKey(Application.class.getSimpleName(),UUID.randomUUID().toString());
			application.setKey(key);
			
			application.setBundleId(object.getString("bundleId"));
			application.setName(object.getString("name"));
			application.setPlatform(Platform.GetPlatform(object.getInt("platform")));
			application.setSecret(UUID.randomUUID().toString());
			return application;
		}
		
		return null;	
	}
	
	public void update(JSONObject object) throws JSONException {
		if(object != null) {
			this.setBundleId(object.getString("bundleId"));
			this.setName(object.getString("name"));
			this.setPlatform(Platform.GetPlatform(object.getInt("platform")));
		}	
	}
}
