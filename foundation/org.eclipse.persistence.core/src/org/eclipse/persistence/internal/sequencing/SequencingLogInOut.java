/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sequencing;


/**
 * <p>
 * <b>Purpose</b>: Simple interface inherited by several sequencing interfaces and classes
 * <p>
 */
interface SequencingLogInOut {

    /**
    * INTERNAL:
    * Called when the object is connected (logged in).
    */
    void onConnect();

    /**
    * INTERNAL:
    * Called when the object is disconnected (logged out).
    */
    void onDisconnect();

    /**
    * INTERNAL:
    * Indicates whether the object is connected or not.
    */
    boolean isConnected();
}
