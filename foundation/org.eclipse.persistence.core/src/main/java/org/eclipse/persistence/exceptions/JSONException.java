/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * <b>Purpose:</b>
 * <ul><li>This class provides an implementation of EclipseLinkException specific to the EclipseLink JSON handling (marshall, unmarshall, Jersey provider)</li>
 * </ul>
 * @since Oracle EclipseLink 2.7.5
 */
public class JSONException extends EclipseLinkException {
    // Error code range for this exception is 60001 - 61000.
    public static final int ERROR_INVALID_DOCUMENT = 60001;

    public JSONException(String theMessage) {
        super(theMessage);
    }

    protected JSONException(String message, Exception internalException) {
        super(message, internalException);
    }

    public static JSONException errorInvalidDocument(Exception internalEx) {
        Object[] args = {  };
        JSONException ex = new JSONException(ExceptionMessageGenerator.buildMessage(JSONException.class, ERROR_INVALID_DOCUMENT, args));
        ex.setErrorCode(ERROR_INVALID_DOCUMENT);
        ex.setInternalException(internalEx);
        return ex;
    }
}
