/*******************************************************************************
 * Copyright (c) 2011, 2018 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial
 ******************************************************************************/
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
