package com.appinmpa.api.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.appinmap.api.objects.Application;
import com.appinmap.api.objects.Platform;
import com.google.appengine.labs.repackaged.org.json.JSONArray;

public class SessionHandlerTest {

	static Application testApplication = new Application();
	static ApplicationHandlerTest applicationHandlerClient = new ApplicationHandlerTest();
	static String applicationId = null;
	static JSONObject application = null;
	String sessionId = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testApplication.setBundleId("com.appinmap.api.unitests");
		testApplication.setName("API UnitTests");
		testApplication.setPlatform(Platform.iOS);
		
		//Create an application
		applicationId = applicationHandlerClient.testApplicationCreate(testApplication);
		application = applicationHandlerClient.testApplicationGet(applicationId);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		applicationHandlerClient.testApplicationDelete(applicationId);
	}
	
	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {	

	}
	
	void testCreateSession() throws KeyManagementException, NoSuchAlgorithmException, JSONException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient(Utilities.getInsecureClientConnectionManager());
		HttpPost post = new HttpPost(Utilities.baseURL + "/session/start");
		post.setHeader("Content-Type", "application/json");
		post.setHeader("charset","UTF-8");
		
		/*
		 * '{"sessionId":"9f87d9dc-e91a-48a6-9a4c-0e65ac0ba2cc","applicationId":"098c27f2-383b-4cad-909b-e63f28387a44",
		 * "bundleId":"com.dinakaran.mobile.iphone","deviceId":"2216f4f9-60da-4478-9fb6-9c4ca778c57a",
		 * "deviceIdType":1,"time":1351380896,"appVersion":"1.02.1.02","sdkVersion":"0.1","location":
		 * {"latitude":37.391644,"longitude":-122.044174,"accuracy":50},"tags":[]}'
		 */
		
		sessionId = UUID.randomUUID().toString();
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sessionId", sessionId);
		jsonObject.put("applicationId", application.get("applicationId"));
		jsonObject.put("bundleId", application.get("bundleId"));
	
		JSONArray deviceIds = new JSONArray();
		deviceIds.put("ODIN1="+UUID.randomUUID().toString());
		deviceIds.put("UDID="+UUID.randomUUID().toString());
		deviceIds.put("IDV="+UUID.randomUUID().toString());
		deviceIds.put("IDAd="+UUID.randomUUID().toString());
		
		jsonObject.put("deviceIds",deviceIds);
		jsonObject.put("time", System.currentTimeMillis());
		jsonObject.put("appVersion", "1.02.02");
		jsonObject.put("sdkVersion", "0.1");
		
		JSONObject locationObject = new JSONObject();
		locationObject.put("latitude", 37.391644);
		locationObject.put("longitude", -122.044174);
		locationObject.put("accuracy", 50);
		
		jsonObject.put("location", locationObject);
		jsonObject.put("tags", new JSONArray());
		jsonObject.put("platform", application.getInt("platform"));
		post.setEntity(new StringEntity(jsonObject.toString()));
		
		String authentication = Utilities.superUser+":"+Utilities.superUserSecret;
        byte[] encoded = Base64.encodeBase64(authentication.getBytes());     	
        post.setHeader("Authorization", "Basic " + new String(encoded));
		
		HttpResponse response = client.execute(post);
		assertEquals(response.getStatusLine().getStatusCode(),201);	
	}
	
	void testSendBeacon() throws KeyManagementException, NoSuchAlgorithmException, JSONException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient(Utilities.getInsecureClientConnectionManager());
		HttpPost post = new HttpPost(Utilities.baseURL + "/session/beacon");
		post.setHeader("Content-Type", "application/json");
		post.setHeader("charset","UTF-8");
		
		/*
		 * '{"sessionId":"9f87d9dc-e91a-48a6-9a4c-0e65ac0ba2cc","time":1352380996,
		 * "location":{"latitude":37.391644,"longitude":-122.044174,"accuracy":50}}' 
		 */
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sessionId", sessionId);
		jsonObject.put("time", System.currentTimeMillis());
		
		JSONObject locationObject = new JSONObject();
		locationObject.put("latitude", 37.391644);
		locationObject.put("longitude", -122.044174);
		locationObject.put("accuracy", 50);
		
		jsonObject.put("location", locationObject);
		post.setEntity(new StringEntity(jsonObject.toString()));
		
		String authentication = Utilities.superUser+":"+Utilities.superUserSecret;
        byte[] encoded = Base64.encodeBase64(authentication.getBytes());     	
        post.setHeader("Authorization", "Basic " + new String(encoded));
		
		HttpResponse response = client.execute(post);
		assertEquals(response.getStatusLine().getStatusCode(),201);	
	}
	
	void testEndSession() throws KeyManagementException, NoSuchAlgorithmException, JSONException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient(Utilities.getInsecureClientConnectionManager());
		HttpPost post = new HttpPost(Utilities.baseURL + "/session/end");
		post.setHeader("Content-Type", "application/json");
		post.setHeader("charset","UTF-8");
		
		/*
		 * '{"sessionId":"9f87d9dc-e91a-48a6-9a4c-0e65ac0ba2cc","time":1352380996,
		 * "location":{"latitude":37.391644,"longitude":-122.044174,"accuracy":50}}' 
		 */
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sessionId", sessionId);
		jsonObject.put("time", System.currentTimeMillis());
		
		JSONObject locationObject = new JSONObject();
		locationObject.put("latitude", 37.391644);
		locationObject.put("longitude", -122.044174);
		locationObject.put("accuracy", 50);
		
		jsonObject.put("location", locationObject);
		post.setEntity(new StringEntity(jsonObject.toString()));
		
		String authentication = Utilities.superUser+":"+Utilities.superUserSecret;
        byte[] encoded = Base64.encodeBase64(authentication.getBytes());     	
        post.setHeader("Authorization", "Basic " + new String(encoded));
		
		HttpResponse response = client.execute(post);
		assertEquals(response.getStatusLine().getStatusCode(),201);	
	}
	
	@Test
	public void testSessions() throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, JSONException, IOException {
		testCreateSession();
		
		testSendBeacon();
		
		testEndSession();
	}
}
