/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
    @Override
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
    @Override
    public void initializeIdentityMaps() {
        for (Iterator sessionEnum = ((SessionBroker)session).getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.getIdentityMapAccessorInstance().initializeIdentityMaps();
        }
        super.initializeIdentityMaps();
    }
}
