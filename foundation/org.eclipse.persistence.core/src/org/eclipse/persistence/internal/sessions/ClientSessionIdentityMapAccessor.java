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
import org.eclipse.persistence.sessions.server.*;

/**
 * INTERNAL:
 * IdentityMapAccessor subclass for client sessions.
 * Note: A client session will always use it's parent session's IdentityMapManager
 */
public class ClientSessionIdentityMapAccessor extends IdentityMapAccessor {

    /**
     * INTERNAL:
     * Create a ClientSessionIdentityMapAccessor
     * Since the parent session's identity map manager is used, an IdentityMapManager
     * does not need to be supplied to the constructor
     */
    public ClientSessionIdentityMapAccessor(ClientSession session) {
        super(session, null);
    }

    /**
     * INTERNAL:
     * Was PUBLIC: customer will be redirected to {@link org.eclipse.persistence.sessions.Session}.
     * Reset the entire object cache.
     * This method blows away both this session's and its parents caches, including the server cache or any other cache.
     * This throws away any objects that have been read in.
     * Extreme caution should be used before doing this because object identity will no longer
     * be maintained for any objects currently read in.  This should only be called
     * if the application knows that it no longer has references to object held in the cache.
     */
    public void initializeAllIdentityMaps() {
        ((ClientSession)session).getParent().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * INTERNAL:
     * Return the IdentityMapManager for the client session.
     * This overrides the IdentityMapAccessor version of getIdentityMapManager to
     * return the parent session's IdentityMapManager
     */
    public IdentityMapManager getIdentityMapManager() {
        return ((ClientSession)session).getParent().getIdentityMapAccessorInstance().getIdentityMapManager();
    }

    /**
     * INTERNAL:
     * The client session does not have a local identity map, so this has no effect and should not be used.
     */
    public void initializeIdentityMap(Class theClass) {
        // Do nothing	
    }

    /**
     * INTERNAL:
     * The client session does not have a local identity map, so this has no effect and should not be used.
     */
    public void initializeIdentityMaps() {
        // Do nothing	
    }

    /**
     * INTERNAL:
     * The identity map manager cannot be set on a client session since it
     * looks at it's parent session's identity map manager.
     */
    public void setIdentityMapManager(IdentityMapManager identityMapManager) {
    }
}
