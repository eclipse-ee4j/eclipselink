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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.persistence.config.CacheIsolationType;
import static org.eclipse.persistence.config.CacheIsolationType.SHARED;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static org.eclipse.persistence.annotations.CacheType.SOFT_WEAK;
import static org.eclipse.persistence.annotations.CacheCoordinationType.SEND_OBJECT_CHANGES;

/** 
 * The Cache annotation is used to configure the EclipseLink object cache.
 * By default EclipseLink uses a shared object cache to cache all objects.
 * The caching type and options can be configured on a per class basis to allow
 * optimal caching.
 * <p>
 * This includes options for configuring the type of caching,
 * setting the size, disabling the shared cache, expiring objects, refreshing,
 * and cache coordination (clustering).
 * <p>
 * A Cache annotation may be defined on an Entity or MappedSuperclass. In the 
 * case of inheritance, a Cache annotation should only be defined on the root 
 * of the inheritance hierarchy.
 * 
 * @see org.eclipse.persistence.annotations.CacheType
 * @see org.eclipse.persistence.annotations.CacheCoordinationType
 * 
 * @see org.eclipse.persistence.descriptors.ClassDescriptor
 * @see org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy
 *  
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
@Target({TYPE})
@Retention(RUNTIME)
public @interface Cache {
    /**
     * (Optional) The type of cache to use.
     * The default is SOFT_WEAK.
     */ 
    CacheType type() default SOFT_WEAK;
    
    /**
     * (Optional) The size of cache to use.
     * The default is 100.
     */ 
    int size() default 100;

    /**
     * (Optional) Cached instances in the shared cache,
     * or only a per EntityManager isolated cache.
     * The default is shared.
     * @deprecated  As of Eclipselink 2.2.  See the attribute 'isolation'
     */ 
    @Deprecated
    boolean shared() default true;
    
    /**
     * (Optional) Controls the level of caching this Entity will use.
     * The default is CacheIsolationType.SHARED which has EclipseLink
     * Caching all Entities in the Shared Cache.
     * @see org.eclipse.persistence.config.CacheIsolationType
     */
    CacheIsolationType isolation() default SHARED;

    /**
     * (Optional) Expire cached instance after a fix period of time (ms). 
     * Queries executed against the cache after this will be forced back 
     * to the database for a refreshed copy.
     * By default there is no expiry.
     */ 
    int expiry() default -1; // minus one is no expiry.

    /**
     * (Optional) Expire cached instance a specific time of day. Queries 
     * executed against the cache after this will be forced back to the 
     * database for a refreshed copy.
     */ 
    TimeOfDay expiryTimeOfDay() default @TimeOfDay(specified=false);

    /**
     * (Optional) Force all queries that go to the database to always 
     * refresh the cache.
     * Default is false.
     * Consider disabling the shared cache instead of forcing refreshing.
     */ 
    boolean alwaysRefresh() default false;

    /**
     * (Optional) For all queries that go to the database, refresh the cache 
     * only if the data received from the database by a query is newer than 
     * the data in the cache (as determined by the optimistic locking field).
     * This is normally used in conjunction with alwaysRefresh, and by itself
     * it only affect explicit refresh calls or queries.
     * Default is false.
     */ 
    boolean refreshOnlyIfNewer() default false;

    /**
     * (Optional) Setting to true will force all queries to bypass the 
     * cache for hits but still resolve against the cache for identity. 
     * This forces all queries to hit the database.
     */ 
    boolean disableHits() default false;

    /**
     * (Optional) The cache coordination mode.
     * Note that cache coordination must also be configured for the persistence unit/session.
     */ 
    CacheCoordinationType coordinationType() default SEND_OBJECT_CHANGES;
    
    /**
     * (Optional) The database change notification mode.
     * Note that database event listener must also be configured for the persistence unit/session.
     */ 
    DatabaseChangeNotificationType databaseChangeNotificationType() default DatabaseChangeNotificationType.INVALIDATE;
}
