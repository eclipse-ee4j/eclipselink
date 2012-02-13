/*******************************************************************************
 * Copyright (c) 2008 - 2012 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/
package javax.persistence;

/**
 * Interface used to interact with the second-level cache.
 * If a cache is not in use, the methods of this interface have
 * no effect, except for <code>contains</code>, which returns false.
 *
 * @since Java Persistence 2.0
 */
public interface Cache {

    /**
     * Whether the cache contains data for the given entity.
     * @param cls  entity class 
     * @param primaryKey  primary key
     * @return boolean indicating whether the entity is in the cache
     */
    public boolean contains(Class cls, Object primaryKey);

    /**
     * Remove the data for the given entity from the cache.
     * @param cls  entity class
     * @param primaryKey  primary key
     */
    public void evict(Class cls, Object primaryKey);

    /**
     * Remove the data for entities of the specified class (and its
     * subclasses) from the cache.
     * @param cls  entity class
     */
    public void evict(Class cls);

    /**
     * Clear the cache.
     */
    public void evictAll();

    /**
     * Return an object of the specified type to allow access to the
     * provider-specific API.  If the provider's Cache
     * implementation does not support the specified class, the
     * PersistenceException is thrown.
     * @param cls  the class of the object to be returned.  This is
     * normally either the underlying Cache implementation
     * class or an interface that it implements.
     * @return an instance of the specified class
     * @throws PersistenceException if the provider does not
     * support the call
     * @since Java Persistence 2.1
     */
    public <T> T unwrap(Class<T> cls);
}
