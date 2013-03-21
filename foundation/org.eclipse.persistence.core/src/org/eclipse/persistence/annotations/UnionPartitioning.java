/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
 
/** 
 * UnionPartitioning sends queries to all connection pools and unions the results.
 * This is for queries or relationships that span partitions when partitioning is used,
 * such as on a ManyToMany cross partition relationship.
 * <p>
 * Partitioning can be enabled on an Entity, relationship, query, or session/persistence unit.
 * Partition policies are globally named to allow reuse,
 * the partitioning policy must also be set using the @Partitioned annotation to be used.
 * 
 * @see Partitioned
 * @see org.eclipse.persistence.descriptors.partitioning.UnionPartitioningPolicy
 * @author James Sutherland
 * @since EclipseLink 2.2
 */ 
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface UnionPartitioning {
    /**
     * The name of the partition policy, names must be unique for the persistence unit.
     */
    String name();
    
    /**
     * List of connection pool names to load balance across.
     * Defaults to all defined pools in the ServerSession.
     */
    String[] connectionPools() default {};
    
    /**
     * Defines if write queries should be replicated.
     * Writes are normally not replicated when unioning,
     * but can be for ManyToMany relationships, when the join table needs to be replicated.
     */
    boolean replicateWrites() default false;
}
