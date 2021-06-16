/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      Dmitry Kornilov - Upgrade to Jersey 2.x
package org.eclipse.persistence.jpars.test.server;

import org.eclipse.persistence.jpa.rs.exceptions.ErrorResponse;

import jakarta.ws.rs.core.Response;

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
