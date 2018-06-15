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
package org.eclipse.persistence.internal.sessions;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.Session;

/**
 * This is a finalizer for a session.
 * This was added to avoid always having a finalizer on client session for GC performance.
 * @author James Sutherland
 * @since TopLink 11
 */
public class SessionFinalizer {
    protected Session session;

    public SessionFinalizer(Session session) {
        this.session = session;
    }

    /**
     * Release the session.
     */
    @Override
    protected void finalize() throws DatabaseException {
        this.session.release();
    }
}
