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
package org.eclipse.persistence.tools.workbench.platformsmodel;

/**
 * A CorruptFileException is thrown whenever a "corrupt" XML file
 * is read in.
 */
public class CorruptXMLException extends Exception {

    /**
     * Construct an exception with no message.
     */
    public CorruptXMLException() {
        super();
    }

    /**
     * Construct an exception with the specified message.
     */
    public CorruptXMLException(String message) {
        super(message);
    }

    /**
     * Construct an exception chained to the specified cause.
     */
    public CorruptXMLException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct an exception with the specified message and
     * chained to the specified cause.
     */
    public CorruptXMLException(String message, Throwable cause) {
        super(message, cause);
    }

}
