/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland - initial API and implementation
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A Partitioning is used to partition the data for a class across multiple difference databases
 * or across a database cluster such as Oracle RAC.
 * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
 * This annotation configures a custom PartitioningPolicy.
 * <p>
 * If multiple partitions are used to process a single transaction, JTA should be used for proper XA transaction support.
 * <p>
 * Partitioning can be enabled on an Entity, relationship, query, or session/persistence unit.
 * Partition policies are globally named to allow reuse,
 * the partitioning policy must also be set using the @Partitioned annotation to be used.
 *
 * @see Partitioned
 * @see org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface Partitioning {
    /**
     * The name of the partition policy, names must be unique for the persistence unit.
     */
    String name();

    /**
     * (Required) Full package.class name of a subclass of PartitioningPolicy.
     */
    Class<? extends PartitioningPolicy> partitioningClass();
}
