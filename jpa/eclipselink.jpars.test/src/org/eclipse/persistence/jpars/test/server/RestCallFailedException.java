/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.server;

import org.eclipse.persistence.jpa.rs.exceptions.ErrorResponse;

import com.sun.jersey.api.client.ClientResponse.Status;

public class RestCallFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Status httpStatus = null;
    private ErrorResponse errorDetails = null;

    public RestCallFailedException(Status httpStatus, ErrorResponse errorDetails) {
        super();
        this.httpStatus = httpStatus;
        this.errorDetails = errorDetails;
    }

    public RestCallFailedException(Status httpStatus) {
        super();
        this.httpStatus = httpStatus;
    }

    public Status getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Status httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ErrorResponse getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(ErrorResponse errorDetails) {
        this.errorDetails = errorDetails;
    }
}
