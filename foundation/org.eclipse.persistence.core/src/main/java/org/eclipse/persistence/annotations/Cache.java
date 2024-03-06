/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The Cache annotation is used to configure the EclipseLink object cache.
 * By default, EclipseLink uses a shared object cache to cache all objects.
 * The caching type and options can be configured on a per-class basis to allow
 * optimal caching.
 * This includes options for configuring the type of caching,
 * setting the size, disabling the shared cache, expiring objects, refreshing,
 * and cache coordination (clustering).
 * <p>
 * A Cache annotation may be defined on an Entity or MappedSuperclass. In the
 * case of inheritance, a Cache annotation should only be defined on the root
 * of the inheritance hierarchy.
 *
 * @see CacheType
 * @see CacheCoordinationType
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
     * The type of cache to use.
     * <p>
     * The default is {@linkplain CacheType#SOFT_WEAK}.
     */
    CacheType type() default CacheType.SOFT_WEAK;

    /**
     * The size of cache to use.
     * <p>
     * The default is 100.
     */
    int size() default 100;

    /**
     * Controls the level of caching this Entity will use.
     * <p>
     * The default is {@linkplain CacheIsolationType#SHARED} which has EclipseLink
     * Caching all Entities in the Shared Cache.
     * @see CacheIsolationType
     */
    CacheIsolationType isolation() default CacheIsolationType.SHARED;

    /**
     * Expire cached instance after a fix period of time (ms).
     * Queries executed against the cache after this will be forced back
     * to the database for a refreshed copy.
     * <p>
     * By default, there is no expiry.
     */
    int expiry() default -1; // minus one is no expiry.

    /**
     * Expire cached instance a specific time of day. Queries
     * executed against the cache after this will be forced back to the
     * database for a refreshed copy.
     */
    TimeOfDay expiryTimeOfDay() default @TimeOfDay(specified=false);

    /**
     * Force all queries that go to the database to always
     * refresh the cache.
     * Consider disabling the shared cache instead of forcing refreshing.
     * <p>
     * Default is false.
     */
    boolean alwaysRefresh() default false;

    /**
     * For all queries that go to the database, refresh the cache
     * only if the data received from the database by a query is newer than
     * the data in the cache (as determined by the optimistic locking field).
     * This is normally used in conjunction with alwaysRefresh, and by itself
     * it only affects explicit refresh calls or queries.
     * <p>
     * Default is false.
     */
    boolean refreshOnlyIfNewer() default false;

    /**
     * Setting to true will force all queries to bypass the
     * cache for hits but still resolve against the cache for identity.
     * This forces all queries to hit the database.
     */
    boolean disableHits() default false;

    /**
     * The cache coordination mode.
     * <p>
     * Note that cache coordination must also be configured for the persistence unit/session.
     */
    CacheCoordinationType coordinationType() default CacheCoordinationType.SEND_OBJECT_CHANGES;

    /**
     * The database change notification mode.
     * <p>
     * Note that database event listener must also be configured for the persistence unit/session.
     */
    DatabaseChangeNotificationType databaseChangeNotificationType() default DatabaseChangeNotificationType.INVALIDATE;
}
