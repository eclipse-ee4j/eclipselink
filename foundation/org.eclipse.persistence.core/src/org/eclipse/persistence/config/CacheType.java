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
package org.eclipse.persistence.config;

/**
 * Cache type persistence property values.
 * The cache type defines the type of cache usage for the entities.
 * Its usage depends on the memory and caching requirements of the entity.
 * If no caching is desired at all the PersistenceUnitProperties.CACHE_SHARED_ should
 * instead be used.
 * 
 * <p>JPA persistence property Usage:
 * 
 * <p>for all entities append DEFAULT suffix to CACHE_TYPE_ prefix:
 * <p><code>properties.add(PersistenceUnitProperties.CACHE_TYPE_DEFAULT, CacheType.Weak);</code>
 * 
 * <p>for a single entity append either entity name or a full class name to CACHE_TYPE_ prefix:
 * <p><code>properties.add(PersistenceUnitProperties.CACHE_TYPE_ + "Employee", CacheType.Weak);</code>
 * <p><code>properties.add(PersistenceUnitProperties.CACHE_TYPE_ + "my.test.Employee", CacheType.Weak);</code>
 * 
 * <p>Values are case-insensitive.
 * "" could be used instead of default value CacheType.DEFAULT.
 * 
 * @see PersistenceUnitProperties#CACHE_SHARED_
 * @see PersistenceUnitProperties#CACHE_TYPE_
 */
public class CacheType {
    /**
     * A Weak cache holds all objects in use by the application,
     * but allows any un-referenced objects to be free to garbage collection.
     * This guarantees object identity, allows optimal garbage collection, but provides little caching benefit.
     */
    public static final String  Weak = "Weak";
    
    /**
     * A Soft cache holds all objects read by the application,
     * but allows any un-referenced objects to be free to garbage collection only when the JVM decides memory is low.
     * This guarantees object identity, allows garbage collection when memory is low, and provides optimal caching benefit.
     */
    public static final String  Soft = "Soft";

    /**
     * A SoftWeak cache holds all objects in use by the application,
     * and a fixed size sub-cache of MRU objects using Soft references.
     * It allows any un-referenced objects not in the sub-cache to be free to garbage collection,
     * and the objects in the sub-cache are free to garbage collect only when the JVM decides memory is low.
     * This guarantees object identity, allows configurable garbage collection, and provides configurable caching benefit.
     */
    public static final String  SoftWeak = "SoftWeak";

    /**
     * A HardWeak cache holds all objects in use by the application,
     * and a fixed size sub-cache of MRU objects using normal (hard) references.
     * It allows any un-referenced objects not in the sub-cache to be free to garbage collection,
     * but not objects in the sub-cache.
     * This guarantees object identity, allows configurable garbage collection, and provides configurable caching benefit.
     */
    public static final String  HardWeak = "HardWeak";    

    /**
     * A Soft cache holds all objects read by the application.
     * It does not allow any garbage collection.     * 
     * This guarantees object identity, allows no garbage collection, and provides complete caching benefit.
     * <p>WARNING: This cache type should only be used for a fixed sized number of objects,
     * otherwise it will lead to an eventual memory leak.
     */
    public static final String  Full = "Full";

    /**
     * NONE does not cache any objects.
     * It allows any un-referenced objects to be free to garbage collection.
     * This provides no object identity, allows complete garbage collection, and provides no caching benefit.
     * <p>WARNING: This cache type should normally not be used.  Instead disable the share cache through PersistenceUnitProperties.CACHE_SHARED_.
     * Lack of object identity can lead to infinite loops for objects that have circular references and no indirection.
     * @see PersistenceUnitProperties#CACHE_SHARED_
     */
    public static final String  NONE = "NONE";
 
    /**
     * The default cache type is SoftWeak.
     */
    public static final String DEFAULT = SoftWeak;
}
