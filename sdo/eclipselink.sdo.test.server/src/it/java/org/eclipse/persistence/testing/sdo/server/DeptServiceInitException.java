/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Iaroslav Savytskyi - August 13/2014 - 2.6.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.server;

/**
 * Used to indicate service fail during initialisation
 */
public class DeptServiceInitException extends RuntimeException {

    private static final long serialVersionUID = 2104702765526786415L;

    public DeptServiceInitException(Throwable cause) {
        super(cause);
    }
}
