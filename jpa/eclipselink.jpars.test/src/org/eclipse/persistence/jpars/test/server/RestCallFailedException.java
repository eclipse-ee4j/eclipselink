/*******************************************************************************
 * Copyright (c) 2011, 2015 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Dmitry Kornilov - Upgrade to Jersey 2.x
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.server;

import org.eclipse.persistence.jpa.rs.exceptions.ErrorResponse;

import javax.ws.rs.core.Response;

public class RestCallFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private int httpStatus;
    private ErrorResponse errorDetails;

    public RestCallFailedException(int httpStatus, ErrorResponse errorDetails) {
        super();
        this.httpStatus = httpStatus;
        this.errorDetails = errorDetails;
    }

    public RestCallFailedException(int httpStatus) {
        super();
        this.httpStatus = httpStatus;
    }

    public RestCallFailedException(Response.Status status) {
        super();
        this.httpStatus = status.getStatusCode();
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public ErrorResponse getErrorDetails() {
        return errorDetails;
    }
}
