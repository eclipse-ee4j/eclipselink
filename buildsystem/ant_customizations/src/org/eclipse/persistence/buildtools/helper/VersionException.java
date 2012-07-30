/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.persistence.buildtools.helper;

public class VersionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Throwable cause;

    public VersionException() {
        super();
    }

    public VersionException(String message) {
        super(message);
    }

    public VersionException(Throwable cause) {
        super(cause.toString());
        this.cause = cause;
    }

    public VersionException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }
    
    public Throwable getException() {
        return cause;
    }

    public Throwable getCause() {
        return getException();
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }
}
