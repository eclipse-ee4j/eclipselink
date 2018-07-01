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
package org.eclipse.persistence.internal.sessions;

import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.broker.*;
import java.util.*;

/**
 * INTERNAL:
 * Subclass of IdentityMapAccessor for Session brokers
 * Overrides some identiy map initialization code
 */
public class SessionBrokerIdentityMapAccessor extends IdentityMapAccessor {
    public SessionBrokerIdentityMapAccessor(AbstractSession session, IdentityMapManager identityMapManager) {
        super(session, identityMapManager);
    }

    /**
     * INTERNAL:
     * Reset the entire object cache.
     * This method blows away both this session's and its parents caches, including the server cache or any other cache.
     * This throws away any objects that have been read in.
     * Extream caution should be used before doing this because object identity will no longer
     * be maintained for any objects currently read in.  This should only be called
     * if the application knows that it no longer has references to object held in the cache.
     */
    public void initializeAllIdentityMaps() {
        for (Iterator sessionEnum = ((SessionBroker)session).getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.getIdentityMapAccessorInstance().initializeAllIdentityMaps();
        }
        super.initializeAllIdentityMaps();
    }

    /**
     * INTERNAL:
     * Reset the entire local object caches.
     * This throws away any objects that have been read in.
     * Extream caution should be used before doing this because object identity will no longer
     * be maintained for any objects currently read in.  This should only be called
     * if the application knows that it no longer has references to object held in the cache.
     */
    public void initializeIdentityMaps() {
        for (Iterator sessionEnum = ((SessionBroker)session).getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.getIdentityMapAccessorInstance().initializeIdentityMaps();
        }
        super.initializeIdentityMaps();
    }
}
