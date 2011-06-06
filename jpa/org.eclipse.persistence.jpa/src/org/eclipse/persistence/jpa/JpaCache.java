/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland  = 1.0 - Initial contribution
 ******************************************************************************/
package org.eclipse.persistence.jpa;

import javax.persistence.Cache;

/**
 * Extends JPA Cache interface with additional EclipseLink API.
 * @author James Sutherland
 */
public interface JpaCache extends Cache {
    
    /**
     * ADVANCED:
     * Resets the entire Object cache, and the Query cache.
     * <p> NOTE: Be careful using this method. This method blows away both this session's and its parent's caches.
     * This includes the server cache or any other cache. This throws away any Objects that have been read in.
     * Extreme caution should be used before doing this because Object identity will no longer
     * be maintained for any Objects currently read in.  This should only be called
     * if the application knows that it no longer has references to Objects held in the cache.
     */
    void clear();
    
    /**
     * ADVANCED:
     * Resets the cache for only the instances of the given Class type.
     * For inheritance the user must make sure that they only use the root class,
     * clearing a subclass cache is not allowed (as they share their parents cache).
     * <p> NOTE: Caution must be used in doing this to ensure that the Objects within the cache
     * are not referenced from other Objects of other classes or from the application.
     */
    void clear(Class cls);

    /**
     * Clear all the query caches.
     */
    void clearQueryCache();

    /**
     * Clear the named query cache associated with the query name.
     */
    void clearQueryCache(String queryName);

    /**
     * Returns the remaining life of the given Object (in milliseconds).  This method is associated with use of
     * cache invalidation feature and returns the difference between the next expiry
     * time of the Object and its read time.  The method will return 0 for invalidated Objects.
     */
    long timeToLive(Object object);

    /**
     * Returns true if the Object with the same id and Class type of the
     * the given Object is valid in the cache.
     */
    boolean isValid(Object object);

    /**
     * Returns true if the Object with the id and Class type is valid in the cache.
     */
    boolean isValid(Class cls, Object id);

    /**
     * Used to print all the Objects in the cache.
     * The output of this method will be logged to this persistence unit's SessionLog at SEVERE level.
     */
    void print();

    /**
     * Used to print all the Objects in the cache of the Class type.
     * The output of this method will be logged to this persistence unit's SessionLog at SEVERE level.
     */
    void print(Class cls);

    /**
     * Used to print all the currently locked cache keys in the cache.
     * The output of this method will be logged to this persistence unit's SessionLog at SEVERE level.
     */
    void printLocks();

    /**
     * This can be used to help debugging an Object identity problem.
     * An Object identity problem is when an Object in the cache references an 
     * Object that is not in the cache. This method will validate that all cached 
     * Objects are in a correct state.
     */
    void validate();

    /**
     * Returns the Object from the cache map with the id 
     * and Class type.
     */
    Object getObject(Class cls, Object id);

    /**
     * ADVANCED:
     * Puts the given Object into the cache.
     * This is a very advanced method, and caution should be used in adding objects to the cache
     * as other objects may have relationships to previous object, or this object may have
     * relationships to other objects.
     */
    Object putObject(Object object);

    /**
     * ADVANCED:
     * Removes the Object from the cache.
     * <p> NOTE: Caution should be used when calling to avoid violating Object identity.
     * The application should only call this if its known that no references to the Object exist.
     */
    Object removeObject(Object object);

    /**
     * ADVANCED:
     * Removes the Object with the id and Class type from the cache.
     * <p> NOTE: Caution should be used when calling to avoid violating Object identity.
     * The application should only call this if its known that no references to the Object exist.
     */
    Object removeObject(Class cls, Object id);

    /**
     * Returns true if the cache contains an Object with the same id and Class type of the given object.
     */
    boolean contains(Object object);

    /**
     * Sets an Object to be invalid in the cache.
     */
    void evict(Object object);

    /**
     * Returns the object's Id.
     */
    Object getId(Object object);

}
