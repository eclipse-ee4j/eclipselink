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
package org.eclipse.persistence.tools.workbench.framework;

/**
 * This exception is thrown when a {@link Plugin}
 * has problems opening a file.
 */
public class OpenException
    extends Exception
{

    /**
     * Construct an exception with the specified root cause.
     */
    public OpenException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct an exception with the specified root cause and message.
     */
    public OpenException(String message, Throwable cause) {
        super(message, cause);
    }

}
