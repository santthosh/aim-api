package com.appinmap.api.handlers;

import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.appinmap.api.WebSwitch;
import com.appinmap.api.objects.Application;
import com.appinmap.api.objects.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ApplicationHandler extends ServerResource {
	private static final Logger log = Logger.getLogger(ApplicationHandler.class.getName());

	public ApplicationHandler()
    {
    }
	
	@Get
	public JsonRepresentation get() 
	{
		WebSwitch application = (WebSwitch) getApplication();
        if (!application.authenticate(getRequest(), getResponse())) {
        	setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        	return null;
        }

        try
        {
            String applicationId = (String)getRequest().getAttributes().get("applicationId");
            
            if(applicationId == null || applicationId.isEmpty()) {
            	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            	return null;
            }
            
            PersistenceManager pm = PMF.get().getPersistenceManager();
	        Key key = KeyFactory.createKey(Application.class.getSimpleName(), applicationId);
	        Application app = pm.getObjectById(Application.class, key);
            setStatus(Status.SUCCESS_OK);
            
            JSONObject jsonApp = app.toJSONObject();
            
            return new JsonRepresentation(jsonApp);
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
        
        return null;
	}
	
	@Delete
	//TODO: Someday we have to offload this to some other backend service where 
	//cascade deletes have to be handled gracefully
	public JsonRepresentation delete() 
	{
		WebSwitch application = (WebSwitch) getApplication();
        if (!application.authenticate(getRequest(), getResponse())) {
        	setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        	return null;
        }

        try
        {
            String applicationId = (String)getRequest().getAttributes().get("applicationId");
            
            if(applicationId == null || applicationId.isEmpty()) {
            	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            	return null;
            }
            
            PersistenceManager pm = PMF.get().getPersistenceManager();
	        Key key = KeyFactory.createKey(Application.class.getSimpleName(), applicationId);
	        Application app = pm.getObjectById(Application.class, key);
            setStatus(Status.SUCCESS_ACCEPTED);
            
            JSONObject jsonApp = app.toJSONObject();

            try {
            	pm.deletePersistent(app);
            } finally {
            	pm.close();
            }
            
            return new JsonRepresentation(jsonApp);
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
        
        return null;
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
            String applicationId = (String)getRequest().getAttributes().get("applicationId");
            
            if(applicationId == null || applicationId.isEmpty()) {
            	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            	return;
            }
            
            PersistenceManager pm = PMF.get().getPersistenceManager();
	        Key key = KeyFactory.createKey(Application.class.getSimpleName(), applicationId);
	        Application app = pm.getObjectById(Application.class, key);
            setStatus(Status.SUCCESS_OK);
            
            app.update(json);
            try {
            	pm.makePersistent(app);
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
    public JsonRepresentation create(JsonRepresentation request)
    {	
		WebSwitch application = (WebSwitch) getApplication();
        if (!application.authenticate(getRequest(), getResponse())) {
        	setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        	return null;
        }
        
		JSONObject jsonObject = new JSONObject();

        try
        {
        	JSONObject json = ((JsonRepresentation) request).getJsonObject();
            Application app = Application.CreateApplication(json);	
            jsonObject.put("applicationId", app.getKey().getName());
            
            PersistenceManager pm = PMF.get().getPersistenceManager();
            try {
            	pm.makePersistent(app);
            } finally {
            	pm.close();
            }

            setStatus(Status.SUCCESS_CREATED);
        } catch(JSONException e) {
			log.severe(e.getLocalizedMessage());
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
        catch(Exception e) {
            log.severe(e.getLocalizedMessage());
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        
        return new JsonRepresentation(jsonObject.toString());
    }
}
