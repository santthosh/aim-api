package com.appinmpa.api.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.appinmap.api.objects.Application;
import com.appinmap.api.objects.Platform;

public class ApplicationHandlerTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	String testApplicationCreate(Application application) throws NoSuchAlgorithmException, KeyManagementException, JSONException, AuthenticationException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient(Utilities.getInsecureClientConnectionManager());
		HttpPost post = new HttpPost(Utilities.baseURL + "/application");
		post.setHeader("Content-Type", "application/json");
		post.setHeader("charset","UTF-8");
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("bundleId", application.getBundleId());
		jsonObject.put("name", application.getName());
		jsonObject.put("platform", Platform.GetIntValue(application.getPlatform()));
		post.setEntity(new StringEntity(jsonObject.toString()));
		
		String authentication = Utilities.superUser+":"+Utilities.superUserSecret;
        byte[] encoded = Base64.encodeBase64(authentication.getBytes());     	
        post.setHeader("Authorization", "Basic " + new String(encoded));
		
		HttpResponse response = client.execute(post);
		assertEquals(response.getStatusLine().getStatusCode(),201);	
		
	    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    StringBuffer buffer = new StringBuffer();
	    String bufferLine = "";
	    while ((bufferLine = rd.readLine()) != null) {
	    	buffer.append(bufferLine);
	    }
	    
	    JSONObject responseObject = new JSONObject(buffer.toString().replaceAll("\"","\""));
		return responseObject.get("applicationId").toString();
	}
	
    JSONObject testApplicationGet(String applicationId) throws NoSuchAlgorithmException, KeyManagementException, JSONException, AuthenticationException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient(Utilities.getInsecureClientConnectionManager());
		HttpGet get = new HttpGet(Utilities.baseURL + "/application/" + applicationId);
		
		String authentication = Utilities.superUser+":"+Utilities.superUserSecret;
        byte[] encoded = Base64.encodeBase64(authentication.getBytes());     	
        get.setHeader("Authorization", "Basic " + new String(encoded));
		
		HttpResponse response = client.execute(get);
		assertEquals(response.getStatusLine().getStatusCode(),200);	
		
	    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    StringBuffer buffer = new StringBuffer();
	    String bufferLine = "";
	    while ((bufferLine = rd.readLine()) != null) {
	    	buffer.append(bufferLine);
	    }
	    
	    JSONObject responseObject = new JSONObject(buffer.toString().replaceAll("\"","\""));
		return responseObject;
	}
	
	void testApplicationUpdate(String applicationId,Application application) throws NoSuchAlgorithmException, KeyManagementException, JSONException, AuthenticationException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient(Utilities.getInsecureClientConnectionManager());
		HttpPut put = new HttpPut(Utilities.baseURL + "/application/" + applicationId);
		put.setHeader("Content-Type", "application/json");
		put.setHeader("charset","UTF-8");
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("bundleId", application.getBundleId());
		jsonObject.put("name", application.getName());
		jsonObject.put("platform", Platform.GetIntValue(application.getPlatform()));
		put.setEntity(new StringEntity(jsonObject.toString()));
		
		String authentication = Utilities.superUser+":"+Utilities.superUserSecret;
        byte[] encoded = Base64.encodeBase64(authentication.getBytes());     	
        put.setHeader("Authorization", "Basic " + new String(encoded));
		
		HttpResponse response = client.execute(put);
		assertEquals(response.getStatusLine().getStatusCode(),200);	
	}
	
	void testApplicationDelete(String applicationId) throws NoSuchAlgorithmException, KeyManagementException, JSONException, AuthenticationException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient(Utilities.getInsecureClientConnectionManager());
		HttpDelete delete = new HttpDelete(Utilities.baseURL + "/application/" + applicationId);
		
		String authentication = Utilities.superUser+":"+Utilities.superUserSecret;
        byte[] encoded = Base64.encodeBase64(authentication.getBytes());     	
        delete.setHeader("Authorization", "Basic " + new String(encoded));
		
		HttpResponse response = client.execute(delete);
		assertEquals(response.getStatusLine().getStatusCode(),202);	
	}
	
	private void testApplicationDeleteConfirmation(String applicationId) throws NoSuchAlgorithmException, KeyManagementException, JSONException, AuthenticationException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient(Utilities.getInsecureClientConnectionManager());
		HttpGet get = new HttpGet(Utilities.baseURL + "/application/" + applicationId);
		
		String authentication = Utilities.superUser+":"+Utilities.superUserSecret;
        byte[] encoded = Base64.encodeBase64(authentication.getBytes());     	
        get.setHeader("Authorization", "Basic " + new String(encoded));
		
		HttpResponse response = client.execute(get);
		assertEquals(response.getStatusLine().getStatusCode(),404);	
	}


	@Test
	public void testApplication() throws KeyManagementException, AuthenticationException, NoSuchAlgorithmException, ClientProtocolException, JSONException, IOException {
		Application testApplication = new Application();
		testApplication.setBundleId("com.appinmap.api.unitests");
		testApplication.setName("API UnitTests");
		testApplication.setPlatform(Platform.iOS);
		
		//Create an application
		String applicationId = testApplicationCreate(testApplication);
		
		//Get the application and test for consistency
		JSONObject jsonObject = testApplicationGet(applicationId);
		assertEquals(jsonObject.get("bundleId"),testApplication.getBundleId());
		assertEquals(jsonObject.get("name"),testApplication.getName());
		assertEquals(Platform.GetPlatform(jsonObject.getInt("platform")),testApplication.getPlatform());
		
		System.out.println("Create application test passed");
		System.out.println("Get application test passed");
		
		//Update the application
		testApplication.setBundleId("com.appinmoa.api.unitests.modified");
		testApplication.setName("API Modified");
		testApplication.setPlatform(Platform.Android);
		
		testApplicationUpdate(applicationId,testApplication);
		
		//Get the application again and test for consistency after update
		JSONObject updatedJsonObject = testApplicationGet(applicationId);
		assertEquals(updatedJsonObject.get("bundleId"),testApplication.getBundleId());
		assertEquals(updatedJsonObject.get("name"),testApplication.getName());
		assertEquals(Platform.GetPlatform(updatedJsonObject.getInt("platform")),testApplication.getPlatform());
		
		System.out.println("Update application test passed");
		
		//Delete the application
		testApplicationDelete(applicationId);
		
		//Get the application again and test for consistency
		testApplicationDeleteConfirmation(applicationId);
		
		System.out.println("Delete application test passed");
	}
}
