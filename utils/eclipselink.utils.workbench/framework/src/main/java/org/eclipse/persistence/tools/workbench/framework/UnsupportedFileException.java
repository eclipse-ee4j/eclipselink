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
package org.eclipse.persistence.tools.workbench.framework;

/**
 * This exception is thrown when a {@link Plugin}
 * is asked to open a file and the plug-in does provide support
 * for the file (either its extension is invalid or its contents
 * are unrecognized).
 */
public class UnsupportedFileException
    extends Exception
{

    /**
     * Construct an exception with no cause or message.
     */
    public UnsupportedFileException() {
        super();
    }

    /**
     * Construct an exception with the specified root cause.
     */
    public UnsupportedFileException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct an exception with the specified message.
     */
    public UnsupportedFileException(String message) {
        super(message);
    }

    /**
     * Construct an exception with the specified root cause and message.
     */
    public UnsupportedFileException(String message, Throwable cause) {
        super(message, cause);
    }

}
