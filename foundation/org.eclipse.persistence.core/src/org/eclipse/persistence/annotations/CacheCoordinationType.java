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
package org.eclipse.persistence.annotations;

/** 
 * An enum that is used within the Cache annotation.
 * 
 * @see org.eclipse.persistence.annotations.Cache
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
public enum CacheCoordinationType {
    /**
     * Sends a list of changed objects including data about the changes. 
     * This data is merged into the receiving cache.
     */
    SEND_OBJECT_CHANGES,

    /**
     * Sends a list of the identities of the objects that have changed. The 
     * receiving cache invalidates the objects (rather than changing any of the 
     * data)
     */
    INVALIDATE_CHANGED_OBJECTS,

    /**
     * Same as SEND_OBJECT_CHANGES except it also includes any newly created 
     * objects from the transaction.
     */
    SEND_NEW_OBJECTS_WITH_CHANGES,

    /**
     * Does no cache coordination.
     */
    NONE
}
