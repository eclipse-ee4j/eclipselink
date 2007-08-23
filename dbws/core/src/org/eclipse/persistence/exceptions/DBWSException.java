/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.exceptions;

// Javase imports

// Java extension imports

// TopLink imports

/**
 * <p><b>PUBLIC</b>: runtime exception for TopLink DBWS Service
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
@SuppressWarnings("serial")
public class DBWSException extends RuntimeException {

    public DBWSException() {
        super();
    }

    public DBWSException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBWSException(String message) {
        super(message);
    }

    public DBWSException(Throwable cause) {
        super(cause);
    }
}
