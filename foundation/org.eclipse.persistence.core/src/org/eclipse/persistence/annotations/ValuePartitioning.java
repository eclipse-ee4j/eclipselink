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

import javax.persistence.Column;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
 
/** 
 * ValuePartitioning partitions access to a database cluster by a field value from the object,
 * such as the object's location, or tenant.
 * Each value is assigned a specific server.
 * All write or read request for object's with that value are sent to the server.
 * If a query does not include the field as a parameter, then it can either be sent
 * to all server's and unioned, or left to the sesion's default behavior.
 * <p>
 * Partitioning can be enabled on an Entity, relationship, query, or session/persistence unit.
 * Partition policies are globally named to allow reuse,
 * the partitioning policy must also be set using the @Partitioned annotation to be used.
 * 
 * @see Partitioned
 * @see org.eclipse.persistence.descriptors.partitioning.ValuePartitioningPolicy
 * @author James Sutherland
 * @since EclipseLink 2.2
 */ 
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface ValuePartitioning {
    /**
     * The name of the partition policy, names must be unique for the persistence unit.
     */
    String name();
        
    /**
     * The database column or query parameter to partition queries by.
     * This is the table column name, not the class attribute name.
     * The column value must be included in the query and should normally be part of the object's Id.
     * This can also be the name of a query parameter.
     * If a query does not contain the field the query will not be partitioned.
     */
    Column partitionColumn();
    
    /** Store the value partitions. Each partition maps a value to a connectionPool. */
    ValuePartition[] partitions();
    
    /** The type of the start and end values. */
    Class partitionValueType() default String.class;
    
    /** The default connection pool is used for any unmapped values. */
    String defaultConnectionPool() default "";
        
    /**
     * Defines if queries that do not contain the partition field should be sent
     * to every database and have the result unioned.
     */
    boolean unionUnpartitionableQueries() default false;
}
