/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpa.rs.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.persistence.jpa.rs.util.JPARSLogger;

@Provider
public class JPARSExceptionMapper extends AbstractExceptionMapper implements ExceptionMapper<JPARSException> {
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
