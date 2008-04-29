/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.identitymaps;

import java.util.*;

/**
 * Used to allow iterating over a maps cache keys.
 */
public class IdentityMapKeyEnumeration implements Enumeration {
    protected FullIdentityMap map;
    protected Iterator cacheKeysIterator;
    protected CacheKey nextKey;

    public IdentityMapKeyEnumeration(FullIdentityMap map) {
        this.map = map;
        this.cacheKeysIterator = map.getCacheKeys().values().iterator();
    }

    public boolean hasMoreElements() {
        this.nextKey = getNextCacheKey();
        return this.nextKey != null;
    }

    public Object nextElement() {
        if (this.nextKey == null) {
            throw new NoSuchElementException("IdentityMapKeyEnumeration nextElement");
        }

        // CR#... Must check the read lock to avoid
        // returning half built objects.
        this.nextKey.checkReadLock();
        return this.nextKey;
    }

    protected CacheKey getNextCacheKey() {
        CacheKey key = null;
        while (this.cacheKeysIterator.hasNext() && (key == null)) {
            key = (CacheKey)this.cacheKeysIterator.next();
        }
        return key;
    }
}