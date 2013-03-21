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
 * Partitioning is used to partition the data for a class across multiple difference databases
 * or across a database cluster such as Oracle RAC.
 * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
 * This annotation configures the named partitioning policy to be used for this Entity or relationship.
 * <p>
 * Partitioning can be enabled on an Entity, relationship, query, or session/persistence unit.
 * 
 * @see Partitioning
 * @see RoundRobinPartitioning
 * @see ReplicationPartitioning
 * @see RangePartitioning
 * @see ValuePartitioning
 * @see HashPartitioning
 * @see PinnedPartitioning
 * @see UnionPartitioning
 * @see org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy
 * @author James Sutherland
 * @since EclipseLink 2.2
 */ 
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface Partitioned {
    /**
     * (Required) name of partitioning policy to use.
     */
    String value();
}
