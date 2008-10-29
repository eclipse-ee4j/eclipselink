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
package org.eclipse.persistence.config;

/**
 * The class defines EclipseLink query hints.
 * These query hints allow a JPA Query to be customized or optimized beyond
 * what is available in the JPA specification.
 * 
 * <p>JPA Query Hint Usage:
 * 
 * <p><code>query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);</code>
 * <p>or 
 * <p><code>@QueryHint(name=QueryHints.CACHE_USAGE, value=CacheUsage.CheckCacheOnly)</code>
 * 
 * <p>Hint values are case-insensitive; "" could be used instead of default value.
 * 
 * @see HintValues
 * @see CacheUsage
 * @see PessimisticLock
 * @see QueryType
 */
public class QueryHints {
    /**
     * Configures parameter binding to be disabled or enabled just for this query (overrides persistent unit setting, which default to true).
     * Valid values are:  HintValues.PERSISTENCE_UNIT_DEFAULT, HintValues.TRUE, HintValues.FALSE,
     * "" could be used instead of default value HintValues.PERSISTENCE_UNIT_DEFAULT
     */
    public static final String BIND_PARAMETERS = "eclipselink.jdbc.bind-parameters";

    /**
     * Configures the query to utilize the EclipseLink cache, by default the cache is not checked on queries before accessing the database.
     * Valid values are all declared in CacheUsage class.
     * For primary key cache hits the QUERY_TYPE hint must also be set to QueryType.ReadObject.
     */
    public static final String CACHE_USAGE = "eclipselink.cache-usage";
    
    /**
     * Configures the query to use a results cache.
     * By default the query will cache 100 query results, such that the same named query with the same arguments
     * is re-executed it will skip the database and just return the cached results.
     * Valid values are:  HintValues.PERSISTENCE_UNIT_DEFAULT, HintValues.TRUE, HintValues.FALSE,
     * "" could be used instead of default value HintValues.PERSISTENCE_UNIT_DEFAULT.
     * By default query results are not cached.
     * This is not, and is independent from the object cache.
     */
    public static final String QUERY_RESULTS_CACHE = "eclipselink.query-results-cache";

    /**
     * Configures the size of the query's results cache.
     * By default the query will cache 100 query results, such that the same named query with the same arguments
     * is re-executed it will skip the database and just return the cached results.
     * If the query has no arguments a size of 1 should be used (as there is only a single result).
     * Valid values are Integer or Strings that can be parsed to int values.
     */
    public static final String QUERY_RESULTS_CACHE_SIZE = "eclipselink.query-results-cache.size";

    /**
     * Configures the time to live, or expiry time of the query's results cache.
     * By default the query results cache will not expiry results.     * 
     * Valid values are number of milliseconds, Integer or Strings that can be parsed to int values.
     */
    public static final String QUERY_RESULTS_CACHE_EXPIRY = "eclipselink.query-results-cache.expiry";
    
    /**
     * Configures the time of day expiry time of the query's results cache.
     * By default the query results cache will not expiry results.     * 
     * Valid values are String Time format, "HH:MM:SS".
     */
    public static final String QUERY_RESULTS_CACHE_EXPIRY_TIME_OF_DAY = "eclipselink.query-results-cache.expiry-time-of-day";
    
    /**
     * Used to provide a Query Redirector to the executing query.
     */
    public static final String QUERY_REDIRECTOR = "eclipselink.query.redirector";

    /**
     * Configures the EclipseLink query type to use for the query.
     * By default EclipseLink ReportQuery or ReadAllQuery are used for most JPQL queries, this allows other query types to be used,
     * such as ReadObjectQuery which can be used for queries that are know to return a single object, and has different caching semantics.
     * Valid values are all declared in QueryType class.
     */
    public static final String QUERY_TYPE = "eclipselink.query-type";
    
    /**
     * Configures  the query to acquire a pessimistic lock (write-lock) on the resulting rows in the database.
     * Valid values are all declared in PessimisticLock class.
     */
    public static final String PESSIMISTIC_LOCK = "eclipselink.pessimistic-lock";
    
    /**
     * Configures the WAIT timeout used in pessimistic locking, if the database 
     * query exceeds the timeout the database will terminate the query and 
     * return an exception. Valid values are Integer or Strings that can be 
     * parsed to int values.
     */
    public static final String PESSIMISTIC_LOCK_TIMEOUT = "javax.persistence.lock.timeout";
    
    /**
     * Configures the query to refresh the resulting objects in the cache and persistent context with the current state of the database.
     * Valid values are:  HintValues.FALSE, HintValues.TRUE,
     * "" could be used instead of default value HintValues.FALSE
     */
    public static final String REFRESH = "eclipselink.refresh";
    
    /**
     * Configures the query to optimize the retrieval of the related objects,
     * the related objects for all the resulting objects will be read in a single query (instead of n queries).
     * Valid values are strings that represent JPQL style navigations to a relationship.
     * <p>e.g. e.manager.phoneNumbers
     */
    public static final String BATCH = "eclipselink.batch";
    
    /**
     * Configures the query to optimize the retrieval of the related objects,
     * the related objects will be joined into the query instead of being queried independently.
     * This allow for nested join fetching which is not supported in JPQL.
     * Valid values are strings that represent JPQL style navigations to a relationship.
     * <p>e.g. e.manager.phoneNumbers
     */
    public static final String FETCH = "eclipselink.join-fetch";
    
    /**
     * Configures the query to return shared (read-only) objects from the cache,
     * instead of objects registered with the persistence context.
     * This improves performance by avoiding the persistence context registration and change tracking overhead to read-only objects.
     * Valid values are:  HintValues.FALSE, HintValues.TRUE,
     * "" could be used instead of default value HintValues.FALSE
     */
    public static final String READ_ONLY = "eclipselink.read-only";
    
    /**
     * Configures the JDBC timeout of the query execution, if the database query exceeds the timeout
     * the database will terminate the query and return an exception.
     * Valid values are Integer or Strings that can be parsed to int values.
     */
    public static final String JDBC_TIMEOUT = "eclipselink.jdbc.timeout";
    
    /**
     * Configures the JDBC fetch-size for the queries result-set.
     * This can improve the performance for queries that return large result-sets.
     * Valid values are Integer or Strings that can be parsed to int values.
     */
    public static final String JDBC_FETCH_SIZE = "eclipselink.jdbc.fetch-size";
    
    /**
     * Configures the JDBC max-rows, if the query returns more rows than the max-rows
     * the trailing rows will not be returned by the database.
     * Valid values are Integer or Strings that can be parsed to int values.
     */
    public static final String JDBC_MAX_ROWS = "eclipselink.jdbc.max-rows";
    
    /**
     * Configures the collection class implementation for the queries result.
     * The fully qualified class name must be used, without the .class.     * 
     * Valid values are a Class representing a collection type or a String representing the class' name 
     * of the collection type.
     * <p>e.g. java.util.ArrayList
     */
    public static final String RESULT_COLLECTION_TYPE = "eclipselink.result-collection-type";
    
    /**
     * Valid values are all declared in CascadePolicy class.
     */
    public static final String REFRESH_CASCADE = "eclipselink.refresh.cascade";
}
