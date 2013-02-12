package com.appinmap.api.handlers;

import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.appinmap.api.WebSwitch;
import com.appinmap.api.objects.Application;
import com.appinmap.api.objects.Beacon;
import com.appinmap.api.objects.PMF;
import com.appinmap.api.objects.Session;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class SessionHandler extends ServerResource {
	private static final Logger log = Logger.getLogger(SessionHandler.class.getName());
	
	public SessionHandler()
    {
    }
	
	private Status startSession(JsonRepresentation request) {
		return Status.SUCCESS_CREATED;
		// NOTE: Disabled
		/*try
        {
        	JSONObject json = ((JsonRepresentation) request).getJsonObject();
            PersistenceManager pm = PMF.get().getPersistenceManager();
            
			Key applicationKey = KeyFactory.createKey(Application.class.getSimpleName(), json.getString("applicationId"));
	        Application app = pm.getObjectById(Application.class, applicationKey);
	        if(!app.getBundleId().equalsIgnoreCase(json.getString("bundleId")))
	        	return Status.CLIENT_ERROR_EXPECTATION_FAILED;
            
	        String ipAddress = this.getClientInfo().getAddress();
            Session session = Session.Create(json,ipAddress);	
            
            try {
            	pm.makePersistent(session);
            } finally {
            	pm.close();
            }

            return Status.SUCCESS_CREATED;
        } catch(JSONException e) {
        	e.printStackTrace();
			log.severe("JSONException : " + e.getMessage());
			return Status.CLIENT_ERROR_BAD_REQUEST;
		} catch(JDOObjectNotFoundException e) {
			return Status.CLIENT_ERROR_NOT_FOUND;
		}
        catch(Exception e) {
        	e.printStackTrace();
			log.severe("Exception : " + e.getMessage());
            return Status.SERVER_ERROR_INTERNAL;
        }*/
	}
	
	private Status addBeacon(JsonRepresentation request) {
		return Status.SUCCESS_CREATED;
		// Disabled
		/*try
        {
        	JSONObject json = ((JsonRepresentation) request).getJsonObject();
        	PersistenceManager pm = PMF.get().getPersistenceManager();
        	
            try {
    	        String ipAddress = this.getClientInfo().getAddress();
                Beacon beacon = Beacon.CreateBeacon(json,ipAddress, pm);
            	pm.makePersistent(beacon);
            } finally {
            	pm.close();
            }

            return Status.SUCCESS_CREATED;
        } catch(JSONException e) {
			log.severe(e.getLocalizedMessage());
			return Status.CLIENT_ERROR_BAD_REQUEST;
		} catch(JDOObjectNotFoundException e) {
			return Status.CLIENT_ERROR_NOT_FOUND;
		}
        catch(Exception e) {
        	e.printStackTrace();
            log.severe(e.getLocalizedMessage());
            return Status.SERVER_ERROR_INTERNAL;
        }*/
	}
	
	private Status endSession(JsonRepresentation request) {
		return Status.SUCCESS_CREATED;
		// Disabled
		/* try
        {
        	JSONObject json = ((JsonRepresentation) request).getJsonObject();
        	PersistenceManager pm = PMF.get().getPersistenceManager();
        	
            try {
    	        String ipAddress = this.getClientInfo().getAddress();
                Beacon beacon = Beacon.EndSession(json,ipAddress, pm);
            	pm.makePersistent(beacon);
            } finally {
            	pm.close();
            }

            return Status.SUCCESS_CREATED;
        } catch(JSONException e) {
			log.severe(e.getLocalizedMessage());
			return Status.CLIENT_ERROR_BAD_REQUEST;
		} catch(JDOObjectNotFoundException e) {
			return Status.CLIENT_ERROR_NOT_FOUND;
		}
        catch(Exception e) {
            log.severe(e.getLocalizedMessage());
            return Status.SERVER_ERROR_INTERNAL;
        }*/
	}

	@Post("json")
    public void Handler(JsonRepresentation request)
    {
		WebSwitch application = (WebSwitch) getApplication();
        if (!application.authenticate(getRequest(), getResponse())) {
        	setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        	return;
        }

        String method = (String)getRequest().getAttributes().get("method");
        
        if(method == null || method.isEmpty())
        	method = "start";
        
        if(method.equalsIgnoreCase("start")) {
        	Status status = startSession(request);
        	setStatus(status);
        } else if(method.equalsIgnoreCase("beacon")) {
        	Status status = addBeacon(request);
        	setStatus(status);
        } else if(method.equalsIgnoreCase("end")) {
        	Status status = endSession(request);
        	setStatus(status);
        }
    }
}
