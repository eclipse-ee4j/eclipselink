/*******************************************************************************
 * Copyright (c)  2008, Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     DaraniY  = 1.0 - Initialize contribution
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.jpa.JpaCache;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * Implements the JPA Cache interface using the EclipseLink cache API through IdentityMapAccessor.
 * @author DaraniY
 */
public class CacheImpl implements JpaCache {

    private IdentityMapAccessor accessor;
    private EntityManagerFactoryImpl emf;
    private ServerSession serversession;

    public CacheImpl(EntityManagerFactoryImpl emf, IdentityMapAccessor accessor) {
        this.accessor = accessor;
        this.emf = emf;
        this.serversession = emf.getServerSession();
    }

    /**
     * Returns true if the cache contains an Object with the id and Class type, and is valid.
     */
    public boolean contains(Class cls, Object id) {
        this.emf.verifyOpen();
        Object pk =  createPrimaryKeyFromId(cls, id);
        ClassDescriptor descriptor = this.serversession.getDescriptor(cls);
        CacheKey key = ((org.eclipse.persistence.internal.sessions.IdentityMapAccessor)this.accessor).getCacheKeyForObject(pk, cls, descriptor);

        return (key != null) && (key.getObject() != null) && (!descriptor.getCacheInvalidationPolicy().isInvalidated(key)); 
    }

    /**
     * Sets an Object with the id and Class type to be invalid in the cache.
     */
    public void evict(Class cls, Object id) {
        this.emf.verifyOpen();
        this.accessor.invalidateObject(createPrimaryKeyFromId(cls, id), cls);
    }

    /**
     * Sets all instances of the class to be invalid.
     */
    public void evict(Class cls) {
        this.emf.verifyOpen();
        this.accessor.invalidateClass(cls);
    }

    /**
     * Sets all instances in the cache to be invalid.
     */
    public void evictAll() {
        this.emf.verifyOpen();
        this.accessor.invalidateAll();
    }

    /**
     * Return the EclipseLink cache key object from the JPA Id object.
     */
    private Object createPrimaryKeyFromId(Class cls, Object id) {
        CMP3Policy policy = (CMP3Policy)this.serversession.getDescriptor(cls).getCMPPolicy();
        Object primaryKey = policy.createPrimaryKeyFromId(id, this.serversession);
        return primaryKey;
    }

    /**
     * ADVANCED:
     * Resets the entire Object cache, and the Query cache.
     * <p> NOTE: Be careful using this method. This method blows away both this session's and its parent's caches.
     * This includes the server cache or any other cache. This throws away any Objects that have been read in.
     * Extreme caution should be used before doing this because Object identity will no longer
     * be maintained for any Objects currently read in.  This should only be called
     * if the application knows that it no longer has references to Objects held in the cache.
     */
    public void clear() {
        this.emf.verifyOpen();
        this.accessor.initializeAllIdentityMaps();
    }

    /**
     * ADVANCED:
     * Resets the cache for only the instances of the given Class type.
     * For inheritance the user must make sure that they only use the root class,
     * clearing a subclass cache is not allowed (as they share their parents cache).
     * <p> NOTE: Caution must be used in doing this to ensure that the Objects within the cache
     * are not referenced from other Objects of other classes or from the application.
     */
    public void clear(Class cls) {
        this.emf.verifyOpen();
        this.accessor.initializeIdentityMap(cls);
    }

    /**
     * Clear all the query caches.
     */
    public void clearQueryCache() {
        this.emf.verifyOpen();
        this.accessor.clearQueryCache();
    }

    /**
     * Clear the named query cache associated with the query name.
     */
    public void clearQueryCache(String queryName) {
        this.emf.verifyOpen();
        this.accessor.clearQueryCache(queryName);
    }

    /**
     * Returns the remaining life of the given Object (in milliseconds).  This method is associated with use of
     * cache invalidation feature and returns the difference between the next expiry
     * time of the Object and its read time.  The method will return 0 for invalidated Objects.
     */
    public long timeToLive(Object object) {
        this.emf.verifyOpen();
        return this.accessor.getRemainingValidTime(object);
    }

    /**
     * Returns true if the Object with the same id and Class type of the
     * the given Object is valid in the cache.
     */
    public boolean isValid(Object object) {
        this.emf.verifyOpen();
        return this.accessor.isValid(object);
    }

    /**
     * Returns true if the Object with the id and Class type is valid in the cache.
     */
    public boolean isValid(Class cls, Object id) {
        this.emf.verifyOpen();
        return this.accessor.isValid(createPrimaryKeyFromId(cls, id), cls);
    }

    /**
     * Used to print all the Objects in the cache.
     * The output of this method will be logged to this persistence unit's SessionLog at SEVERE level.
     */
    public void print() {
        this.emf.verifyOpen();
        this.accessor.printIdentityMaps();
    }

    /**
     * Used to print all the Objects in the cache of the Class type.
     * The output of this method will be logged to this persistence unit's SessionLog at SEVERE level.
     */
    public void print(Class cls) {
        this.emf.verifyOpen();
        this.accessor.printIdentityMap(cls);
    }

    /**
     * Used to print all the currently locked cache keys in the cache.
     * The output of this method will be logged to this persistence unit's SessionLog at SEVERE level.
     */
    public void printLocks() {
        this.emf.verifyOpen();
        this.accessor.printIdentityMapLocks();
    }

    /**
     * This can be used to help debugging an Object identity problem.
     * An Object identity problem is when an Object in the cache references an 
     * Object that is not in the cache. This method will validate that all cached 
     * Objects are in a correct state.
     */
    public void validate() {
        this.emf.verifyOpen();
        this.accessor.validateCache();
    }

    /**
     * Returns the Object from the cache map with the id 
     * and Class type.
     */
    public Object getObject(Class cls, Object id) {
        this.emf.verifyOpen();
        return this.accessor.getFromIdentityMap(createPrimaryKeyFromId(cls, id), cls);
    }

    /**
     * ADVANCED:
     * Puts the given Object into the cache.
     * This is a very advanced method, and caution should be used in adding objects to the cache
     * as other objects may have relationships to previous object, or this object may have
     * relationships to other objects.
     */
    public Object putObject(Object object) {
        this.emf.verifyOpen();
        return this.accessor.putInIdentityMap(object);
    }

    /**
     * ADVANCED:
     * Removes the Object from the cache.
     * <p> NOTE: Caution should be used when calling to avoid violating Object identity.
     * The application should only call this if its known that no references to the Object exist.
     */
    public Object removeObject(Object object) {
        this.emf.verifyOpen();
        return this.accessor.removeFromIdentityMap(object);
    }

    /**
     * ADVANCED:
     * Removes the Object with the id and Class type from the cache.
     * <p> NOTE: Caution should be used when calling to avoid violating Object identity.
     * The application should only call this if its known that no references to the Object exist.
     */
    public Object removeObject(Class cls, Object id) {
        this.emf.verifyOpen();
        return this.accessor.removeFromIdentityMap(createPrimaryKeyFromId(cls, id), cls);
    }

    /**
     * Returns true if the cache contains an Object with the same id and Class type of the given object.
     */
    public boolean contains(Object object) {
        return contains(object.getClass(), getId(object));
    }

    /**
     * Sets the object to be invalid in the cache.
     */
    public void evict(Object object) {
        this.emf.verifyOpen();
        this.accessor.invalidateObject(object);
    }

    /**
     * Returns the object's Id.
     */
    public Object getId(Object object) {
        this.emf.verifyOpen();
        ClassDescriptor cdesc = this.serversession.getDescriptor(object.getClass());
        CMP3Policy policy = (CMP3Policy) (cdesc.getCMPPolicy());
        return policy.createPrimaryKeyInstance(object, this.serversession);
    }
}
