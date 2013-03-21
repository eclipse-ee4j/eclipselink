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
package org.eclipse.persistence.descriptors.invalidation;

import java.util.Random;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * PUBLIC:
 * A CacheInvalidationPolicy is used to set objects in TopLink's identity maps to be invalid
 * following given rules.  CacheInvalidationPolicy is the abstract superclass for all
 * policies used for cache invalidation.
 * By default in EclipseLink, objects do not expire in the cache.  Several different policies
 * are available to allow objects to expire.  These can be set on the ClassDescriptor.
 * @see org.eclipse.persistence.descriptors.ClassDescriptor
 * @see org.eclipse.persistence.descriptors.invalidation.NoExpiryCacheInvalidationPolicy
 * @see org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy
 * @see org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy
 */
public abstract class CacheInvalidationPolicy implements java.io.Serializable, Cloneable {
    public static final long NO_EXPIRY = -1;

    /** This will represent objects that do not expire. */
    protected boolean shouldUpdateReadTimeOnUpdate = false;

    /** Determines if expired object registered in the unit of work should be refreshed, default true. */
    protected boolean shouldRefreshInvalidObjectsOnClone = true;
    
    /** Allows the timeToLive to be randomized to avoid bottlenecks. */
    protected boolean isInvalidationRandomized = false;
    
    /** Random used for randomized invalidation. */
    protected Random random;

    /**
     * PUBLIC:
     * Allows the timeToLive to be randomized to avoid bottlenecks.
     */
    public boolean isInvalidationRandomized() {
        return isInvalidationRandomized;
    }

    /**
     * PUBLIC:
     * Allows the timeToLive to be randomized to avoid bottlenecks.
     */
    public void setIsInvalidationRandomized(boolean isInvalidationRandomized) {
        this.isInvalidationRandomized = isInvalidationRandomized;
        if (isInvalidationRandomized) {
            this.random = new Random();
        }
    }
    
    /**
     * INTERNAL:
     * Get the next time when this object will become invalid
     */
    public abstract long getExpiryTimeInMillis(CacheKey key);

    /**
     * INTERNAL:
     * Return the remaining life of this object
     */
    public long getRemainingValidTime(CacheKey key) {
        long expiryTime = getExpiryTimeInMillis(key);
        long remainingTime = expiryTime - System.currentTimeMillis();
        if (remainingTime > 0) {
            return remainingTime;
        }
        return 0;
    }

    /**
     * INTERNAL:
     * Allow initialization with the descriptor.
     */
    public void initialize(ClassDescriptor descriptor, AbstractSession session) {
        
    }

    /**
     * INTERNAL:
     * return true if this object is expire, false otherwise.
     */
    public boolean isInvalidated(CacheKey key) {
        return isInvalidated(key, System.currentTimeMillis());
    }
    
    /**
     * INTERNAL:
     * return true if this object is expire, false otherwise.
     */
    public abstract boolean isInvalidated(CacheKey key, long currentTimeMillis);

    /**
     * PUBLIC:
     * Set whether to update the stored time an object was read when an object is updated.
     * When the read time is updated, it indicates to EclipseLink that the data in the object
     * is up to date.  This means that cache invalidation checks will occur relative to the
     * new read time.
     * By default, the read time will not be updated when an object is updated.
     * Often it is possible to be confident that the object is up to date after an update
     * because otherwise the update will fail because of the locking policies in use.
     */
    public void setShouldUpdateReadTimeOnUpdate(boolean shouldUpdateReadTime) {
        shouldUpdateReadTimeOnUpdate = shouldUpdateReadTime;
    }

    /**
     * PUBLIC:
     * Return whether objects affected by this CacheInvalidationPolicy should have
     * the read time on their cache keys updated when an update occurs.
     */
    public boolean shouldUpdateReadTimeOnUpdate() {
        return shouldUpdateReadTimeOnUpdate;
    }
    
    /** 
     * PUBLIC:
     * Set if expired object registered in the unit of work should be refreshed, default is true.
     * @deprecated since EclipseLink 2.2
     * @see setShouldRefreshInvalidObjectsOnClone(boolean)
     */
    @Deprecated
    public void setShouldRefreshInvalidObjectsInUnitOfWork(boolean shouldRefreshInvalidObjectsInUnitOfWork) {
        this.shouldRefreshInvalidObjectsOnClone = shouldRefreshInvalidObjectsInUnitOfWork;
    }
    
    /** 
     * PUBLIC:
     * Set if expired object should be refreshed prior to cloning, default is true.  Applies to Protected Entities and UnitOfWork registration.
     */
    public void setShouldRefreshInvalidObjectsOnClone(boolean shouldRefreshInvalidObjectsOnClone) {
        this.shouldRefreshInvalidObjectsOnClone = shouldRefreshInvalidObjectsOnClone;
    }
    /** 
     * PUBLIC:
     * Return if expired object registered in the unit of work should be refreshed.
     * @deprecated since EclipseLink 2.2
     * @see shouldRefreshInvalidObjectsOnClone()
     */
    @Deprecated
    public boolean shouldRefreshInvalidObjectsInUnitOfWork() {
        return shouldRefreshInvalidObjectsOnClone;
    }
    /** 
     * PUBLIC:
     * Return if expired object should be refreshed prior to cloning.  Applies to Protected Entities and UnitOfWork registration.
     */ 
    
    public boolean shouldRefreshInvalidObjectsOnClone() {
        return shouldRefreshInvalidObjectsOnClone;
    }
    
    public Object clone() {
        CacheInvalidationPolicy clone = null;
        
        try {
            clone = (CacheInvalidationPolicy)super.clone();
            clone.setShouldUpdateReadTimeOnUpdate(this.shouldUpdateReadTimeOnUpdate);
            clone.setShouldRefreshInvalidObjectsOnClone(this.shouldRefreshInvalidObjectsOnClone);
            clone.setIsInvalidationRandomized(this.isInvalidationRandomized);            
        } catch (Exception exception) {
            throw new InternalError("clone failed");
        }
        
        return clone;
    }
}
