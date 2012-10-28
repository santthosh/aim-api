package com.appinmap.api;

import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import org.restlet.security.SecretVerifier;
import org.restlet.security.Verifier;

import com.appinmap.api.objects.Application;
import com.appinmap.api.objects.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class RequestVerifier extends SecretVerifier implements Verifier {

	private static char[] globalSecret = "6fdabe6a-8b6e-4ba2-8404-87d9c1d4a728".toCharArray();
	
	private static MemcacheService syncCache = null;
	
	private MemcacheService getMemcache() {
		if(syncCache == null) {
			syncCache = MemcacheServiceFactory.getMemcacheService();
		    syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		}
		
		return syncCache;
	}
	
	@Override
	public boolean verify(String arg0, char[] arg1) throws IllegalArgumentException {
		if(arg0.equalsIgnoreCase("santthosh@appinmap.com") && SecretVerifier.compare(arg1, globalSecret))
			return true;
			   
	    byte[] value;
	    String key = "app-"+arg0;

	    value = (byte[]) getMemcache().get(key); // read from cache
	    if (value == null) {
	      // try getting things from persistent store, if exists
	      try {
	    	  PersistenceManager pm = PMF.get().getPersistenceManager();
	    	  Key persistenceKey = KeyFactory.createKey(Application.class.getSimpleName(), arg0);
	    	  Application app = pm.getObjectById(Application.class, persistenceKey);  
	    	  value = app.getSecret().getBytes();
		      syncCache.put(key, value); // populate cache
	      } catch(Exception e) {
			  return false;
	      } 
	    }
	    
	    String secret = new String(value);
	    if(SecretVerifier.compare(arg1, secret.toCharArray()))
	    	return true;
	    
		return false;
	}
}
