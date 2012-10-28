package com.appinmap.api;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Context;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

import com.appinmap.api.handlers.ApplicationHandler;
import com.appinmap.api.handlers.SessionHandler;

public class WebSwitch extends Application {

	@Override
	public Restlet createInboundRoot() {
        this.authenticatior = createAuthenticator();
        
		Router router = new Router(getContext());
		router.attach("/session/{method}",SessionHandler.class);
		router.attach("/application",ApplicationHandler.class);
		router.attach("/application/{applicationId}",ApplicationHandler.class);
		authenticatior.setNext(router);
		
		return authenticatior;
	}
	
    private ChallengeAuthenticator authenticatior;
    
    public boolean authenticate(Request request, Response response) {
        if (!request.getClientInfo().isAuthenticated()) {
            authenticatior.challenge(response, false);
            return false;
        }
        return true;
    }

    private ChallengeAuthenticator createAuthenticator() {
        Context context = getContext();
        boolean optional = true;
        ChallengeScheme challengeScheme = ChallengeScheme.HTTP_BASIC;
        String realm = "aim-api";

        ChallengeAuthenticator auth = new ChallengeAuthenticator(context, optional, challengeScheme, realm, new RequestVerifier()) {
            @Override
            protected boolean authenticate(Request request, Response response) {
            	String environment = System.getProperty("com.google.appengine.runtime.environment");
            	if (environment.equalsIgnoreCase("production")) {
            		//Do not allow appspot.com domains and don't allow unsecure api calls
                	if(request.getHostRef().getHostDomain().toLowerCase().endsWith("appspot.com") ||
                			!request.getHostRef().getScheme().equalsIgnoreCase("https")) {
                		return false;
                	}	
            	}
            	
                if (request.getChallengeResponse() == null) {
                    return false;
                } else {
                    return super.authenticate(request, response);
                }
            }
        };

        return auth;
    }
}
