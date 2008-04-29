/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.config;

/**
 * Cache usage hint values.
 * Cache usage allows the cache to be used on queries to avoid accessing the database.
 * By default for JPA queries the cache is not checked before accessing the database,
 * but is used after accessing the database to avoid re-building the objects and avoid
 * accessing the database for relationships.
 * 
 * The class contains all the valid values for EclipseLinkQueryHints.CACHE_USAGE query hint.
 * 
 * <p>JPA Query Hint Usage:
 * 
 * <p><code>query.setHint(EclipseLinkQueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);</code>
 * <p>or 
 * <p><code>@QueryHint(name=EclipseLinkQueryHints.CACHE_USAGE, value=CacheUsage.CheckCacheOnly)</code>
 * 
 * <p>Hint values are case-insensitive.
 * "" could be used instead of default value CacheUsage.DEFAULT.
 * 
 * @see EclipseLinkQueryHints
 */
public class CacheUsage {
    /**
     * By default the descriptor default is used, which is to not check the cache first.
     */
    public static final String  UseEntityDefault = "UseEntityDefault";
    /**
     * Do not check the cache first, this is the default for JPA Queries.
     */
    public static final String  DoNotCheckCache = "DoNotCheckCache";
    /**
     * Configure the cache to be checked first if the query is by primary key (only).
     * This can only be used on queries that return a single entity.
     */
    public static final String  CheckCacheByExactPrimaryKey = "CheckCacheByExactPrimaryKey";
    /**
     * Configure the cache to be checked first if the query contains the primary key.
     * This can only be used on queries that return a single entity.
     */
    public static final String  CheckCacheByPrimaryKey = "CheckCacheByPrimaryKey";
    /**
     * Configure the cache to be searched for any matching object before accesing the database.
     * This can only be used on queries that return a single entity.
     */
    public static final String  CheckCacheThenDatabase = "CheckCacheThenDatabase";
    /**
     * Configure the cache to be searched for any matching objects.
     * Any objects not currently in the cache will not be returned.
     * This can only be used on queries that return a single set of entities.
     */
    public static final String  CheckCacheOnly = "CheckCacheOnly";
    /**
     * Configure the query results to be conformed with the current persistence context.
     * This allows non-flushed changes to be included in the query.
     * This can only be used on queries that return a single set of entities.
     */
    public static final String  ConformResultsInUnitOfWork = "ConformResultsInUnitOfWork";
 
    public static final String DEFAULT = UseEntityDefault;
}
