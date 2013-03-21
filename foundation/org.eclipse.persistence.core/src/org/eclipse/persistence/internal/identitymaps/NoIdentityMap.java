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
        return new Vector(0).elements();
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
    public Enumeration keys() {
        return new Vector(0).elements();
    }

    /**
     * Return an empty enumerator.
     */
    public Enumeration keys(boolean checkReadLocks) {
        return keys();
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
