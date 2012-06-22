/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.eis.cobol;


/**
* <b>Purpose</b>: This class creates all exceptions for the copy book parser
*/
public class CopyBookParseException extends org.eclipse.persistence.exceptions.EclipseLinkException {
    public static final int INVALID_COPYBOOK = 14000;
    public static final int IO_EXCEPTION = 14001;

    public CopyBookParseException() {
        super("");
    }

    public CopyBookParseException(String message) {
        super(message);
    }

    public CopyBookParseException(String message, Exception exception) {
        super(message, exception);
    }

    /**
    * this exception is thrown when a problem is encountered parsing a copybook
    */
    public static CopyBookParseException invalidCopyBookException(String message) {
        CopyBookParseException exception = new CopyBookParseException(message);
        exception.setErrorCode(INVALID_COPYBOOK);
        return exception;
    }

    /**
    * this exception is thrown when an io exception occurs during copybook parsing
    */
    public static CopyBookParseException ioException(java.io.IOException internalException) {
        CopyBookParseException exception = new CopyBookParseException("An IOException occurred.");
        exception.setErrorCode(IO_EXCEPTION);
        exception.setInternalException(internalException);
        return exception;
    }
}
