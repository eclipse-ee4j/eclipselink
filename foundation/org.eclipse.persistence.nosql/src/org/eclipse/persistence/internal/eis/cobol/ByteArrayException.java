/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.cobol;


/**
* This is for exceptions encountered while extracting or inserting data in byte arrays.
*/
public class ByteArrayException extends org.eclipse.persistence.exceptions.EclipseLinkException {
    public static final int UNRECOGNIZED_DATA_FORMAT = 16000;

    public ByteArrayException() {
        super("");
    }

    public ByteArrayException(String message) {
        super(message);
    }

    public ByteArrayException(String message, Exception exception) {
        super(message, exception);
    }

    /**
    * thrown when the byte converter cannot recognize the data format in the byte array
    */
    public static ByteArrayException unrecognizedDataType() {
        ByteArrayException exception = new ByteArrayException("An invalid data format has been encountered.");
        exception.setErrorCode(UNRECOGNIZED_DATA_FORMAT);
        return exception;
    }
}
