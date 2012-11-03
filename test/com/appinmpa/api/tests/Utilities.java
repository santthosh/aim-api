package com.appinmpa.api.tests;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.BasicClientConnectionManager;

public class Utilities {
	static String baseURL = "http://localhost:8888"; 
	static String superUser = "santthosh@appinmap.com";
	static String superUserSecret = "6fdabe6a-8b6e-4ba2-8404-87d9c1d4a728";

	static ClientConnectionManager getInsecureClientConnectionManager() throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext sslContext = SSLContext.getInstance("SSL");

		// set up a TrustManager that trusts everything
		sslContext.init(null, new TrustManager[] { new X509TrustManager() {
		            public X509Certificate[] getAcceptedIssuers() {
		                    System.out.println("getAcceptedIssuers =============");
		                    return null;
		            }

		            public void checkClientTrusted(X509Certificate[] certs,
		                            String authType) {
		                    System.out.println("checkClientTrusted =============");
		            }

		            public void checkServerTrusted(X509Certificate[] certs,
		                            String authType) {
		                    System.out.println("checkServerTrusted =============");
		            }
		} }, new SecureRandom());

		SSLSocketFactory sf = new SSLSocketFactory(sslContext);
		Scheme httpScheme = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
		Scheme httpsScheme = new Scheme("https", 443, sf);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(httpScheme);
		schemeRegistry.register(httpsScheme);

		return new BasicClientConnectionManager(schemeRegistry);
	}
}
