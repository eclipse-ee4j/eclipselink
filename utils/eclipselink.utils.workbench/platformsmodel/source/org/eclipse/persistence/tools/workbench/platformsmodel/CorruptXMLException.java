/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
