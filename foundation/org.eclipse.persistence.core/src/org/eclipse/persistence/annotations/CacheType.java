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
package org.eclipse.persistence.annotations;

/** 
 * The CacheType enum is used with the Cache annotation for a persistent class.
 * It defines the type of cache (IdentityMap) used for the class. By default the 
 * SOFT_WEAK cache type is used.
 * 
 * @see org.eclipse.persistence.annotations.Cache
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
public enum CacheType {
    /**
     * Provides full caching and guaranteed identity. Caches all objects
     * and does not remove them. 
     * WARNING: This method may be memory intensive when many objects are 
     * read.  If used on a large data set it will eventually causes an out of memory error.
     */
    FULL,

    /**
     * Similar to the FULL identity map except that the map holds the
     * objects using weak references. This method allows full garbage
     * collection and guaranteed identity.  It will only hold objects
     * that are referenced by the application so may not provide a large caching benefit.
     */
    WEAK,

    /**
     * Similar to the FULL identity map except that the map holds the
     * objects using soft references. This method allows full garbage
     * collection when memory is low and provides full caching and guaranteed identity.
     */
    SOFT,
    
    /**
     * Similar to the WEAK identity map except that it maintains a
     * most-frequently-used sub-cache. The size of the sub-cache is
     * proportional to the size of the identity map as specified by
     * @Cache size attribute. The sub-cache
     * uses soft references to ensure that these objects are
     * garbage-collected only if the system is low on memory.
     */
    SOFT_WEAK,

    /**
     * Identical to the soft cache weak (SOFT_WEAK) identity map except 
     * that it uses hard references in the sub-cache. Use this identity 
     * map if soft references do not behave properly on your platform.
     */
    HARD_WEAK,

    /**
     * A cache identity map maintains a fixed number of objects
     * specified by the application. Objects are removed from the cache
     * on a least-recently-used basis. This method allows object
     * identity for the most commonly used objects.
     * WARNING: Furnishes caching and identity, but does not guarantee 
     * identity.  This cache type is not recommend and should normally not be used,
     * except for objects that have no relationships to them.
     */
    CACHE,

    /**
     * WARNING: Does not preserve object identity and does not cache 
     * objects.  This cache type is not recommend and should normally not be used.
     * This cache type should not be used to disable caching, to properly disable
     * caching set the @Cache isolation attribute to ISOLATED.
     */
    NONE
}
