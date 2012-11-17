package com.appinmap.api.handlers;

import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.appinmap.api.WebSwitch;
import com.appinmap.api.objects.MessagingToken;
import com.appinmap.api.objects.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class MessagingTokenHandler extends ServerResource {
	
	private static final Logger log = Logger.getLogger(MessagingTokenHandler.class.getName());
	
	public MessagingTokenHandler()
    {
    }
	
	@Put("json")
	public void update(JsonRepresentation request) 
	{
		WebSwitch application = (WebSwitch) getApplication();
        if (!application.authenticate(getRequest(), getResponse())) {
        	setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        	return;
        }

        try
        {
        	JSONObject json = ((JsonRepresentation) request).getJsonObject();
            String applicationId = (String)getRequest().getAttributes().get("device_token");
            
            if(applicationId == null || applicationId.isEmpty()) {
            	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            	return;
            }
            
            PersistenceManager pm = PMF.get().getPersistenceManager();
	        Key key = KeyFactory.createKey(MessagingToken.class.getSimpleName(), applicationId);
	        MessagingToken token = pm.getObjectById(MessagingToken.class, key);
            setStatus(Status.SUCCESS_OK);
            
            token.update(json);
            try {
            	pm.makePersistent(token);
            } finally {
            	pm.close();
            }
        } catch(JSONException e) {
			log.severe(e.getLocalizedMessage());
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch(JDOObjectNotFoundException e) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
        catch(Exception e) {
            log.severe(e.getLocalizedMessage());
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }
	}

	@Post("json")
    public void create(JsonRepresentation request)
    {
		WebSwitch application = (WebSwitch) getApplication();
        if (!application.authenticate(getRequest(), getResponse())) {
        	setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        	return;
        }

        try
        {
        	JSONObject json = ((JsonRepresentation) request).getJsonObject();
        	MessagingToken token = MessagingToken.CreateMessagingToken(json);	
            
            PersistenceManager pm = PMF.get().getPersistenceManager();
            try {
            	pm.makePersistent(token);
            } finally {
            	pm.close();
            }

            setStatus(Status.SUCCESS_CREATED);
        } catch(JSONException e) {
			log.severe("JSONException : " + e.getLocalizedMessage());
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        catch(Exception e) {
            log.severe("Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }
}
