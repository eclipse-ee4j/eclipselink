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
package org.eclipse.persistence.queries;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.internal.helper.ClassConstants;

/**
 * PUBLIC:
 *
 * A QueryResultsCache policy dictates how a query's results will be cached.
 *
 * It allows an invalidation policy and a maximum number of results to be set.
 * Query results are cached based on the parameter values of a query, and the maximum number
 * of results refers to the maximum number of parameter sets results will be cached for.\
 * By default query result caching is not used.
 *
 * @see org.eclipse.persistence.queries.ReadQuery#setQueryCachePolicy(QueryResultsCachePolicy)
 */
public class QueryResultsCachePolicy implements Serializable, Cloneable {
    /** Allows invalidation to be specified. */
    protected CacheInvalidationPolicy invalidationPolicy;
    /** Specifies the cache size. */
    protected int maximumResultSets;
    /** Allows the identity map class type to be set. */
    protected Class cacheType;
    /** Allows the caching of null to be configured. */
    protected boolean isNullIgnored;
    /** Allows the query cache to be invalidated when any object of any of the query classes is modified. */    
    protected boolean invalidateOnChange;
    /** Stores the set of classes that should trigger the query cached results to be invalidated. */    
    protected Set<Class> invalidationClasses;

    /**
     * PUBLIC:
     * Build a QueryResultsCachePolicy with the default settings
     * By default there is no invalidation of query results and the maximum
     * number of results sets is 100.
     */
    public QueryResultsCachePolicy() {
        this(new NoExpiryCacheInvalidationPolicy(), 100);
    }

    /**
     * PUBLIC:
     * Build a QueryResultsCachePolicy and supply a CacheInvalidationPolicy and a maximum
     * number of results sets.
     *
     * @see org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy
     */
    public QueryResultsCachePolicy(CacheInvalidationPolicy policy, int maximumResultSets) {
        this.invalidationPolicy = policy;
        this.maximumResultSets = maximumResultSets;
        this.cacheType = ClassConstants.CacheIdentityMap_Class;
        this.isNullIgnored = false;
        this.invalidateOnChange = true;
        this.invalidationClasses = new HashSet<Class>();
    }
    
    public QueryResultsCachePolicy clone() {
        try {
            QueryResultsCachePolicy clone = (QueryResultsCachePolicy)super.clone();
            clone.invalidationClasses = new HashSet<Class>();
            return clone;
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
    }
    
    /**
     * ADVANCED:
     * Return the set of classes that should trigger the query cached results to be invalidated.
     */
    public Set<Class> getInvalidationClasses() {
        return invalidationClasses;
    }
    
    /**
     * ADVANCED:
     * Set the set of classes that should trigger the query cached results to be invalidated.
     * This is normally computed by the query, but can be set in the case of native queries.
     */
    public void setInvalidationClasses(Set<Class> invalidationClasses) {
        this.invalidationClasses = invalidationClasses;
    }

    /**
     * PUBLIC:
     * Return if null results should be cached or ignored.
     * By default they are cached.
     * They can be ignored to allow a query cache to be used as a secondary cache index,
     * and allow new objects to be insert, and still found.
     */
    public boolean isNullIgnored() {
        return isNullIgnored;
    }

    /**
     * PUBLIC:
     * Set if null results should be cached or ignored.
     * By default they are cached.
     * They can be ignored to allow a query cache to be used as a secondary cache index,
     * and allow new objects to be insert, and still found.
     */
    public void setIsNullIgnored(boolean isNullIgnored) {
        this.isNullIgnored = isNullIgnored;
    }

    /**
     * PUBLIC:
     * Return if any change to any object of the query class should cause the query results to be invalidated.
     */
    public boolean getInvalidateOnChange() {
        return invalidateOnChange;
    }

    /**
     * PUBLIC:
     * Configure if any change to any object of the query class should cause the query results to be invalidated.
     */
    public void setInvalidateOnChange(boolean invalidateOnChange) {
        this.invalidateOnChange = invalidateOnChange;
    }

    /**
     * PUBLIC:
     * Return the type of the cache used for the query results.
     * This defaults to a LRU cache (CacheIdentityMap), but can be
     * set to any IdentityMap class, such as Full or Soft.
     */
    public Class getCacheType() {
        return cacheType;
    }

    /**
     * PUBLIC:
     * Set the type of the cache used for the query results.
     * This defaults to a LRU cache (CacheIdentityMap), but can be
     * set to any IdentityMap class, such as Full or Soft.
     */
    public void setCacheType(Class cacheType) {
        this.cacheType = cacheType;
    }

    /**
     * PUBLIC:
     * Set the type of the cache used for the query results to a FullIdentityMap.
     * This will cache all query results, so caution should be used to avoid running out of memory.
     */
    public void useFullCache() {
        setCacheType(ClassConstants.FullIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the type of the cache used for the query results to a SoftIdentityMap.
     * This will cache all query results, unless the JVM believes memory is low.
     */
    public void useSoftCache() {
        setCacheType(ClassConstants.SoftIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the type of the cache used for the query results to a SoftCacheWeakIdentityMap.
     * This will uses a fixed size LRU cache using Soft references, so will allow garbage collection when memory is low.
     */
    public void useSoftLRUCache() {
        setCacheType(ClassConstants.SoftCacheWeakIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the type of the cache used for the query results to a CacheIdentityMap.
     * This will uses a fixed size LRU cache.
     * This is the default.
     */
    public void useLRUCache() {
        setCacheType(ClassConstants.CacheIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Build a QueryResultsCachePolicy and supply a CacheInvalidationPolicy. The default
     * value of 100 will be used for the maximum number of result sets
     *
     * @see org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy
     */
    public QueryResultsCachePolicy(CacheInvalidationPolicy policy) {
        this(policy, 100);
    }

    /**
     * PUBLIC:
     * Build a QueryResultsCachePolicy and supply a maximum for the number of results sets.
     * Results will be set not to expire in the cache.
     */
    public QueryResultsCachePolicy(int maximumResultSets) {
        this(new NoExpiryCacheInvalidationPolicy(), maximumResultSets);
    }

    /**
     * PUBLIC:
     * Return the query cache invalidation policy.
     * The cache invalidation policy defines how the query results are invalidated.
     */
    public CacheInvalidationPolicy getCacheInvalidationPolicy() {
        return invalidationPolicy;
    }

    /**
     * PUBLIC:
     * Set the query cache invalidation policy.
     * The cache invalidation policy defines how the query results are invalidated.
     */
    public void setCacheInvalidationPolicy(CacheInvalidationPolicy invalidationPolicy) {
        this.invalidationPolicy = invalidationPolicy;
    }

    /**
     * PUBLIC:
     * Return the maximum cached results.
     * This defines the number of query result sets that will be cached.
     * The LRU query results will be discarded when the max size is reached.
     */
    public int getMaximumCachedResults() {
        return maximumResultSets;
    }
    
    /**
     * PUBLIC:
     * Set the maximum cached results.
     * This defines the number of query result sets that will be cached.
     * The LRU query results will be discarded when the max size is reached.
     */
    public void setMaximumCachedResults(int maximumResultSets) {
        this.maximumResultSets = maximumResultSets;
    }
}
