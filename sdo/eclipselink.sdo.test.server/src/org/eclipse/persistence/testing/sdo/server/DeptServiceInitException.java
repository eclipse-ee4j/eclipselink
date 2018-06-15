/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Iaroslav Savytskyi - August 13/2014 - 2.6.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.server;

/**
 * Used to indicate service fail during initialisation
 */
public class DeptServiceInitException extends RuntimeException {

    public DeptServiceInitException(Throwable cause) {
        super(cause);
    }
}
