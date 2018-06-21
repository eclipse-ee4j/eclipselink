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
package org.eclipse.persistence.internal.sessions.remote;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: This Interfaces provides the reference part of an remote command framework
 * Whereby, TopLink can issue commands to distributed servers.  This framework reduces
 * the size of the ever growing RemoteController and RemoteConnection framework
 * <p>
 * <b>Description</b>: Used as the Interface for making calls to the object
 * <p>
 */
public interface RemoteCommand extends java.io.Serializable {

    /**
    * INTERNAL:
    * This method is used bye the remote Session to execute the command
    */
    public void execute(AbstractSession session, RemoteSessionController remoteSessionController);
}
