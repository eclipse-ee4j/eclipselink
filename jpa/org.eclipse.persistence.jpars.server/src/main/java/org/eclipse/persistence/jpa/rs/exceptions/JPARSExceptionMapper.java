/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpa.rs.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.persistence.jpa.rs.util.JPARSLogger;

@Provider
public class JPARSExceptionMapper extends AbstractExceptionMapper implements ExceptionMapper<JPARSException> {

    /**
     * Default constructor.
     */
    public JPARSExceptionMapper() {
    }

    @Override
    public Response toResponse(JPARSException exception) {
        JPARSLogger logger = exception.getSession() != null
                ? new JPARSLogger(exception.getSession().getSessionLog())
                : JPARSLogger.DEFAULT_LOGGER;
        if (exception.getCause() != null) {
            logger.exception(sessionId(exception), "jpars_caught_exception", new Object[] {}, exception.getCause());
        } else {
            logger.exception(sessionId(exception), "jpars_caught_exception", new Object[] {}, exception);
        }
        return buildResponse(exception);
    }

    private static String sessionId(JPARSException exception) {
        return exception.getSession() != null ? exception.getSession().getSessionId() : null;
    }

}
