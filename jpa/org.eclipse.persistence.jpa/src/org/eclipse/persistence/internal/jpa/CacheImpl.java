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
 *     12/04/2008 - 2.0 Darani Yallapragada 
 *       - 248780: Initial contribution for JPA 2.0
 *     06/03/2010 - 2.1 Michael O'Brien 
 *       - 248780: Refactor Cache Implementation surrounding evict()
 *         Fix evict() to handle non-Entity classes
 *         Refactor to get IdentityMapAccessor state through EMF reference
 *         Refactor dependencies to use Interfaces instead of Impl subclasses
 *         Handle no CMPPolicy case for getId()
 *         Handle no associated descriptor for Class parameter
 *         MappedSuperclasses passed to evict() cause implementing subclasses to be evicted
 *         Throw an IAE for Interfaces and Embeddable classes passed to evict()
 *     
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaCache;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.Session;

/**
 * Implements the JPA Cache interface using the EclipseLink cache API through IdentityMapAccessor.
 * @author DaraniY
 */
public class CacheImpl implements JpaCache {

    /** The EntityManagerFactory associated with this Cache */
    private EntityManagerFactoryImpl emf;

    /**
     * @param emf
     */
    public CacheImpl(EntityManagerFactoryImpl emf) {
        this.emf = emf;
    }

    /**
     * Returns true if the cache contains an Object with the id and Class type, and is valid.
     * @see Cache#contains(Class, Object)
     */
    public boolean contains(Class cls, Object id) {
        getEntityManagerFactory().verifyOpen();
        Object pk =  createPrimaryKeyFromId(cls, id);
        if(null == pk) {
            return false;
        }
        ClassDescriptor descriptor = getSession().getClassDescriptor(cls); // getDescriptor() is the same call
        /**
         * Check for no descriptor associated with the class parameter.
         * This will occur if the class represents a MappedSuperclass (concrete or abstract class),
         * an interface or Embeddable class.
         */
        if(null == descriptor) {
            // do not throw an IAException: cache_impl_class_has_no_descriptor_is_not_a_persistent_type - just return false
            return false;
        }

        // we can assume that all implementors of IdentityMapAccessor implement getCacheKeyforObject
        CacheKey key = ((org.eclipse.persistence.internal.sessions.IdentityMapAccessor)getAccessor())
            .getCacheKeyForObject(pk, cls, descriptor);
        return key != null && key.getObject() != null && 
            !descriptor.getCacheInvalidationPolicy().isInvalidated(key);    
    }

    /**
     * INTERNAL:
     * This private method searches the map of descriptors for possible superclasses to the
     * passed in class parameter and invalidates only entities found in the cache.
     * If the class is not an Entity or MappedSuperclass (such as an Embeddable or plain java class)
     *  - nothing will be evicted  
     * @param possibleSuperclass
     * @param id
     */
    private void evictAssignableEntitySuperclass(Class possibleSuperclass, Object id) {
        // just remove the parent entity
        for(ClassDescriptor candidateAssignableDescriptor : getSession().getDescriptors().values()) {
            // In EclipseLink we need only remove the root descriptor that is assignable from this possibleSubclass because the recurse flag defaults to true in invalidateClass()
            // what if we have 2 roots (don't check for !candidateAssignableDescriptor.isChildDescriptor())
            if(!candidateAssignableDescriptor.isAggregateDescriptor() && // a !Embeddable check
               !candidateAssignableDescriptor.isAggregateCollectionDescriptor() && // a !EmbeddableCollection check
               possibleSuperclass.isAssignableFrom(candidateAssignableDescriptor.getJavaClass())) { 
                // id will be null if this private function was called from evict(class)
                if(null == id) {
                    // set the invalidationState to -1 in the cache of a type that can be assigned to the class parameter
                    // this call will invalidate all assignable subclasses from the level of possibleSubclass] in the subtree
                    // we could either loop through each aDescriptor.getJavaClass()
                    // or
                    // let invalidateClass loop for us by passing in the higher [possibleSubclass] - all subclasses of the first parent entity descriptor will be invalidated in this first call
                    getAccessor().invalidateClass(candidateAssignableDescriptor.getJavaClass());
                } else {
                    // evict the class instance that corresponds to the id
                    // initialize the cache of a type that can be assigned to the class parameter
                    getAccessor().invalidateObject(createPrimaryKeyFromId(possibleSuperclass, id), candidateAssignableDescriptor.getJavaClass());
                }
            }
        }
    }
    
    /**
     * Sets an Object with the id and Class type to be invalid in the cache.
     * Remove the data for entities of the specified class (and its
     * subclasses) from the cache.<p>
     * If the class is a MappedSuperclass then the first entity above in the inheritance hierarchy will be evicted
     *   along with all implementing subclasses
     * If the class is not an Entity or MappedSuperclass but is the root of an entity inheritance tree then
     *   evict the subtree 
     * If the class is not an Entity or MappedSuperclass but inherits from one then
     *   evict up to root descriptor
     * @see Cache#evict(Class, Object)
     * @param classToEvict - class to evict - usually representing an Entity or MappedSuperclass
     * @param id - Primary key of the Entity or MappedSuperclass Class
     *    A null id means invalidate the class - possibly the entire tree or subtree
     */
    public void evict(Class classToEvict, Object id) {
        getEntityManagerFactory().verifyOpen();
        /**
         * The following descriptor lookup will return the Entity representing the classToEvict parameter,
         * or it will return the first Entity superclass of a MappedSuperclass in the hierarchy
         * - in this case all subclasses of the parent Entity will be evicted.
         * The descriptor will be null if the classToEvict represents a non-Entity or non-MappedSuperclass like an Embeddable or plain java class
         * - in this case nothing will be evicted.
         */
        ClassDescriptor aPossibleSuperclassDescriptor = getSession().getClassDescriptor(classToEvict);
        // Do not recurse if the class to evict is below its' descriptor in the inheritance tree        
        if(null != aPossibleSuperclassDescriptor) {
            // Evict all Entity or MappedSuperclass classes
            if(null != id) {
                Object cacheKey = createPrimaryKeyFromId(classToEvict, id);
                if(null != cacheKey) {
                    getAccessor().invalidateObject(cacheKey, classToEvict);
                }
            } else {
                // 312503: always evict all implementing entity subclasses of an entity or mappedSuperclass
                boolean invalidateRecursively = aPossibleSuperclassDescriptor.getJavaClass().equals(classToEvict);
                getAccessor().invalidateClass(classToEvict, invalidateRecursively);
            }
        } else {
            // Evict the first possible parent Entity superclass of a non-Entity and non-MappedSuperclass class
            evictAssignableEntitySuperclass(classToEvict, id);
        }
    }

    /**
     * Sets all instances of the class to be invalid.
     * Remove the data for entities of the specified class (and its
     * subclasses) from the cache.<p>
     * If the class is a MappedSuperclass then the first entity above in the inheritance hierarchy will be evicted
     *   along with all implementing subclasses
     * If the class is not an Entity or MappedSuperclass (such as an Embeddable or plain java class)
     *  - nothing will be evicted  
     * @see Cache#evict(Class) 
     * @param entityOrMappedSuperclassToEvict - Entity or MappedSuperclass Class
     */
    public void evict(Class entityOrMappedSuperclassToEvict) {
        // A null id means invalidate the class - possibly the entire tree or subtree
        evict(entityOrMappedSuperclassToEvict, null);
    }    
    
    /**
     * Sets all instances in the cache to be invalid.
     * @see Cache#evict(Object)
     */
    public void evictAll() {
        getEntityManagerFactory().verifyOpen();
        getEntityManagerFactory().getDatabaseSession().getIdentityMapAccessor().invalidateAll();
    }

    /**
     * Return the EclipseLink cache key object from the JPA Id object.
     */
    private Object createPrimaryKeyFromId(Class cls, Object id) {
        Object cacheKey = null;
        ClassDescriptor aDescriptor = getSession().getDescriptor(cls);
        // Check that we have a descriptor associated with the class (Entity or MappedSuperclass)
        if(null == aDescriptor) {
            // No descriptor found, throw exception for Embeddable or non-persistable java class
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "cache_impl_class_has_no_descriptor_is_not_a_persistent_type", 
                    new Object[] {cls}));
        }
        // The policy is not set if the mapping is natively defined outside of JPA
        if(aDescriptor.hasCMPPolicy()) {
            // we assume that the PK id parameter is correct and do not throw a cache_descriptor_has_no_cmppolicy_set_cannot_create_primary_key exception 
            // The primaryKey may be the same object as the id parameter
            cacheKey = aDescriptor.getCMPPolicy().createPrimaryKeyFromId(id, getEntityManagerFactory().getDatabaseSession());
        }
        return cacheKey;
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
        getEntityManagerFactory().verifyOpen();
        getAccessor().initializeAllIdentityMaps();
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
        getEntityManagerFactory().verifyOpen();
        getAccessor().initializeIdentityMap(cls);
    }

    /**
     * Clear all the query caches.
     */
    public void clearQueryCache() {
        getEntityManagerFactory().verifyOpen();
        getAccessor().clearQueryCache();
    }

    /**
     * Clear the named query cache associated with the query name.
     */
    public void clearQueryCache(String queryName) {
        getEntityManagerFactory().verifyOpen();
        getAccessor().clearQueryCache(queryName);
    }

    /**
     * Returns the remaining life of the given Object (in milliseconds).  This method is associated with use of
     * cache invalidation feature and returns the difference between the next expiry
     * time of the Object and its read time.  The method will return 0 for invalidated Objects.
     */
    public long timeToLive(Object object) {
        getEntityManagerFactory().verifyOpen();
        return getAccessor().getRemainingValidTime(object);
    }

    /**
     * Returns true if the Object with the same id and Class type of the
     * the given Object is valid in the cache.
     */
    public boolean isValid(Object object) {
        getEntityManagerFactory().verifyOpen();
        return getAccessor().isValid(object);
    }

    /**
     * Returns true if the Object with the id and Class type is valid in the cache.
     */
    public boolean isValid(Class cls, Object id) {
        getEntityManagerFactory().verifyOpen();
        Object cacheKey = createPrimaryKeyFromId(cls, id);
        if(null != cacheKey) {
            return getAccessor().isValid(cacheKey, cls);
        } else {
            return false;
        }
    }

    /**
     * Used to print all the Objects in the cache.
     * The output of this method will be logged to this persistence unit's SessionLog at SEVERE level.
     */
    public void print() {
        getEntityManagerFactory().verifyOpen();
        getAccessor().printIdentityMaps();
    }

    /**
     * Used to print all the Objects in the cache of the Class type.
     * The output of this method will be logged to this persistence unit's SessionLog at SEVERE level.
     */
    public void print(Class cls) {
        getEntityManagerFactory().verifyOpen();
        getAccessor().printIdentityMap(cls);
    }

    /**
     * Used to print all the currently locked cache keys in the cache.
     * The output of this method will be logged to this persistence unit's SessionLog at SEVERE level.
     */
    public void printLocks() {
        getEntityManagerFactory().verifyOpen();
        getAccessor().printIdentityMapLocks();
    }

    /**
     * This can be used to help debugging an Object identity problem.
     * An Object identity problem is when an Object in the cache references an 
     * Object that is not in the cache. This method will validate that all cached 
     * Objects are in a correct state.
     */
    public void validate() {
        getEntityManagerFactory().verifyOpen();
        getAccessor().validateCache();
    }

    /**
     * Returns the Object from the cache map with the id 
     * and Class type.
     */
    public Object getObject(Class cls, Object id) {
        getEntityManagerFactory().verifyOpen();
        Object cacheKey = createPrimaryKeyFromId(cls, id);        
        return getAccessor().getFromIdentityMap(cacheKey, cls);
    }

    /**
     * ADVANCED:
     * Puts the given Object into the cache.
     * This is a very advanced method, and caution should be used in adding objects to the cache
     * as other objects may have relationships to previous object, or this object may have
     * relationships to other objects.
     */
    public Object putObject(Object object) {
        getEntityManagerFactory().verifyOpen();
        return getAccessor().putInIdentityMap(object);
    }

    /**
     * ADVANCED:
     * Removes the Object from the cache.
     * <p> NOTE: Caution should be used when calling to avoid violating Object identity.
     * The application should only call this if its known that no references to the Object exist.
     */
    public Object removeObject(Object object) {
        getEntityManagerFactory().verifyOpen();
        return getAccessor().removeFromIdentityMap(object);
    }

    /**
     * ADVANCED:
     * Removes the Object with the id and Class type from the cache.
     * <p> NOTE: Caution should be used when calling to avoid violating Object identity.
     * The application should only call this if its known that no references to the Object exist.
     */
    public Object removeObject(Class cls, Object id) {
        getEntityManagerFactory().verifyOpen();
        Object cacheKey = createPrimaryKeyFromId(cls, id);
        return getAccessor().removeFromIdentityMap(cacheKey, cls);
    }

    /**
     * Returns true if the cache contains an Object with the same id and Class type of the given object.
     */
    public boolean contains(Object object) {
        return contains(object.getClass(), getId(object));
    }

    /**
     * Sets the object to be invalid in the cache.
     * @see JpaCache#evict(Object)
     */
    public void evict(Object object) {
        getEntityManagerFactory().verifyOpen();
        getAccessor().invalidateObject(object);
    }

    /**
     * INTERNAL:
     * Return the EntityManagerFactory associated with this CacheImpl.
     * @return
     */
    protected EntityManagerFactoryImpl getEntityManagerFactory() {
        return this.emf;
    }
    
    /**
     * INTERNAL:
     * Return the Session associated with the EntityManagerFactory.
     * @return
     */
    protected Session getSession() {
        return getEntityManagerFactory().getDatabaseSession();
    }

    /**
     * INTERNAL:
     * Return the IdentityMapAccessor associated with the session on the EntityManagerFactory on this CacheImpl.
     * @return
     */
    protected IdentityMapAccessor getAccessor() {
        return getSession().getIdentityMapAccessor();
    }

    /**
     * This method will return the objects's Id.
     * If the descriptor associated with the domain object is null - an IllegalArgumentException is thrown.
     * If the CMPPolicy associated with the domain object's descriptor is null 
     * the Id will be determined using the ObjectBuilder on the descriptor - which may return
     * the Id stored in the weaved _persistence_primaryKey field.
     * @See {@link JpaCache#getId(Object)}
     */
    public Object getId(Object object) {
        getEntityManagerFactory().verifyOpen();
        ClassDescriptor aDescriptor = getSession().getDescriptor(object.getClass());
        // Handle a null descriptor from a detached entity (closed EntityManager), or the entity exists in another session
        if(null == aDescriptor) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "cache_impl_object_has_no_descriptor_is_not_a_persistent_type", 
                    new Object[] {object}));
        }
        
        // Handle a null CMPPolicy from a MappedSuperclass
        if(!aDescriptor.hasCMPPolicy()) {
            // the following code gets the key either from the weaved _persistence_primaryKey field or using valueFromObject() if not bytecode enhanced
            return aDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, (AbstractSession)getSession());
        } else {
            // Get identifier via EMF
            return getEntityManagerFactory().getIdentifier(object);
        }
    }
}
