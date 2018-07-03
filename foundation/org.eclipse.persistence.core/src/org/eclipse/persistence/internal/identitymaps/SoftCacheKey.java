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
package org.eclipse.persistence.internal.identitymaps;

import java.lang.ref.*;

/**
 * <p><b>Purpose</b>: Container class for storing objects in an IdentityMap.
 * The soft cache key uses a soft reference to allow garbage collection of its object.
 * The cache key itself however will remain and thus should cleaned up every now and then.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Hold key and object.
 * <li> Maintain and update the current writeLockValue.
 * </ul>
 * @author James Sutherland
 * @since TopLink 11g
 */
public class SoftCacheKey extends WeakCacheKey {
    /**
     * Initialize the newly allocated instance of this class.
     * @param primaryKey contains values extracted from the object
     * @param writeLockValue is the write lock value, null if optimistic locking not being used for this object.
     * @param readTime the time EclipseLInk read the cache key
     */
    public SoftCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime, boolean isIsolated) {
        super(primaryKey, object, writeLockValue, readTime, isIsolated);
    }

    public void setObject(Object object) {
        this.reference = new SoftReference(object);
    }
}
