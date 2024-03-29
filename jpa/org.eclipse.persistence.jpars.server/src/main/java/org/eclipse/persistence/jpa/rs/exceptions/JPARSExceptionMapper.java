/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
        if (exception.getCause() != null) {
            JPARSLogger.exception("jpars_caught_exception", new Object[] {}, exception.getCause());
        } else {
            JPARSLogger.exception("jpars_caught_exception", new Object[] {}, exception);
        }
        return buildResponse(exception);
    }
}
