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
//     12/14/2017-3.0 Tomas Kraus
//       - 522635: ConcurrentModificationException when triggering lazy load from conforming query
package org.eclipse.persistence.internal.identitymaps;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;


/**
 * <p><b>Purpose</b>: Provide the capability to not cache objects at all.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Do nothing when an object is cached.
 * </ul>
 * @since TOPLink/Java 1.0
 */
public class NoIdentityMap extends AbstractIdentityMap {

    public NoIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isolated) {
        super(size, descriptor, session, isolated);
    }

    /**
     * NoIdentityMap has no locks.
     */
    @Override
    public void collectLocks(HashMap threadList) {
        return;
    }

    /**
     * Return an empty enumerator.
     */
    @Override
    public Enumeration elements() {
        return Collections.<Object>emptyEnumeration();
    }

    /**
     * Return null as no objects are cached.
     */
    @Override
    public Object get(Object primaryKey) {
        return null;
    }

    /**
     * Return null as no objects are cached.
     */
    @Override
    public CacheKey getCacheKey(Object searchKey, boolean forMerge) {
        return null;
    }

    /**
     * Return null as no objects are cached.
     */
    @Override
    protected CacheKey putCacheKeyIfAbsent(CacheKey cacheKey) {
        return null;
    }

    /**
     * Return 0 as no objects are cached.
     */
    @Override
    public int getSize() {
        return 0;
    }

    /**
     * Return 0 as no objects are cached.
     */
    @Override
    public int getSize(Class myClass, boolean recurse) {
        return 0;
    }

    /**
     * Return null as no objects are cached.
     */
    @Override
    public Object getWriteLockValue(Object primaryKey) {
        return null;
    }

    /**
     * Return an empty enumerator.
     */
    @Override
    public Enumeration<CacheKey> keys() {
        return Collections.<CacheKey>emptyEnumeration();
    }

    /**
     * Return an empty enumerator.
     */
    @Override
    public Enumeration<CacheKey> cloneKeys() {
        return Collections.<CacheKey>emptyEnumeration();
    }

    /**
     * Return an empty enumerator.
     */
    public Enumeration<CacheKey> keys(boolean checkReadLocks) {
        return Collections.<CacheKey>emptyEnumeration();
    }

    /**
     * Notify the cache that a lazy relationship has been triggered in the object
     * and the cache may need to be updated
     */
    public void lazyRelationshipLoaded(Object object, ValueHolderInterface valueHolder, ForeignReferenceMapping mapping){
        //NO-OP
    }

    /**
     * Do Nothing.
     */
    @Override
    public CacheKey put(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        return null;
    }

    /**
     * Do Nothing.
     * Return null, since no objects are cached.
     */
    @Override
    public Object remove(Object primaryKey, Object object) {
        return null;
    }

    /**
     * Do Nothing.
     * Return null, since no objects are cached.
     */
    @Override
    public Object remove(CacheKey searchKey) {
        return null;
    }

    /**
     * Do Nothing.
     */
    @Override
    public void setWriteLockValue(Object primaryKey, Object writeLockValue) {
        return;
    }
}
