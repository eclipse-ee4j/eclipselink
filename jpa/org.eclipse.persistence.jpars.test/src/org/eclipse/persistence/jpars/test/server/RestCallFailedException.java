package org.eclipse.persistence.jpars.test.server;

import com.sun.jersey.api.client.ClientResponse.Status;

public class RestCallFailedException extends RuntimeException {

    private Status responseStatus = null;
    
    public RestCallFailedException(Status responseStatus){
        this.responseStatus = responseStatus;
    }

    public Status getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Status responseStatus) {
        this.responseStatus = responseStatus;
    }
}
