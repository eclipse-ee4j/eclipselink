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
 *     James Sutherland - initial impl
 ******************************************************************************/  
package org.eclipse.persistence.config;

/**
 * In-memory querying and conforming indirection policy.
 * 
 * The class contains all the valid values for QueryHints.INDIRECTION_POLICY query hint.
 * This can be used on a query with a CACHE_USAGE hint to configure the behavior of in-memory
 * querying and conforming's treatment of uninstantiated indirection/lazy relationships.
 * This is only relevant when the query traverses a join across a lazy relationship.
 * 
 * JPA Query Hint Usage:
 * 
 * <p><code>query.setHint(QueryHints.INDIRECTION_POLICY, CacheUsageIndirectionPolicy.Trigger);</code>
 * <p>or 
 * <p><code>@QueryHint(name=QueryHints.INDIRECTION_POLICY, value=CacheUsageIndirectionPolicy.Trigger)</code>
 * 
 * <p>Hint values are case-insensitive.
 * "" could be used instead of default value CacheUsageIndirectionPolicy.Exception.
 * 
 * @see QueryHints#INDIRECTION_POLICY
 * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#setInMemoryQueryIndirectionPolicyState(int)
 * 
 * @author James Sutherland
 */
public class CacheUsageIndirectionPolicy {
    /** If conforming encounters an uninstantiated indirection/lazy object an exception is thrown. */
    public static final String  Exception = "Exception";

    /** If conforming encounters an uninstantiated indirection/lazy object it is triggered. */
    public static final String  Trigger = "Trigger";
    
    /** If conforming encounters an uninstantiated indirection/lazy object it is assumed to conform. */
    public static final String  Conform = "Conform";
    
    /** If conforming encounters an uninstantiated indirection/lazy object it is assumed to not conform. */
    public static final String  NotConform = "NotConform";
    
    /**
     * The default type is Exception.
     */
    public static final String DEFAULT = Exception;
}
