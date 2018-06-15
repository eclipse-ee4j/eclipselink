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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Thrown when there are problems loading an ExternalClass.
 */
public class ExternalClassNotFoundException extends Exception {

    /**
     * Constructs an ExternalClassNotFoundException with no detail message.
     */
    public ExternalClassNotFoundException() {
        super();
    }

    /**
     * Constructs an ExternalClassNotFoundException with the
     * specified detail message.
     *
     * @param message
     */
    public ExternalClassNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs an ExternalClassNotFoundException with the
     * specified detail message and optional exception that was raised
     * while loading the external class.
     *
     * @param message
     * @param cause
     */
    public ExternalClassNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an ExternalClassNotFoundException with the
     * optional exception that was raised while loading the external class.
     *
     * @param cause
     */
    public ExternalClassNotFoundException(Throwable cause) {
        super(cause);
    }

}
