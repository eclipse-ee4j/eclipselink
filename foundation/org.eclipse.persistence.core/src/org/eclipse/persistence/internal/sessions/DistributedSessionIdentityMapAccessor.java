/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.remote.*;

/**
 * INTERNAL:
 * Subclass of IdentityMapAccessor for distributed sessions
 * Overrides some IdentityMapInitialization code
 */
public class DistributedSessionIdentityMapAccessor extends IdentityMapAccessor {
    public DistributedSessionIdentityMapAccessor(AbstractSession session, IdentityMapManager identityMapManager) {
        super(session, identityMapManager);
    }

    /**
     * Reset the entire object cache.
     * This method blows away both this session's and its parents caches, including the server cache or any other cache.
     * This throws away any objects that have been read in.
     * Extream caution should be used before doing this because object identity will no longer
     * be maintained for any objects currently read in.  This should only be called
     * if the application knows that it no longer has references to object held in the cache.
     */
    public void initializeAllIdentityMaps() {
        super.initializeAllIdentityMaps();
        initializeIdentityMapsOnServerSession();
    }

    /**
     * INTERNAL:
     * Flushes the server side cache, because the server cache may be shared, caution should be used in calling this.
     */
    public void initializeIdentityMapsOnServerSession() {
        ((DistributedSession)session).getRemoteConnection().initializeIdentityMapsOnServerSession();
    }
}
