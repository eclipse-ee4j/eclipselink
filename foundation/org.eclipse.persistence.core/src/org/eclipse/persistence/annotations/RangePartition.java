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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represent a specific range partition.
 * Values {@literal >=} startValue and {@literal <=} endValue will be routed to the connection pool.
 *
 * @see RangePartitioning
 * @see org.eclipse.persistence.descriptors.partitioning.RangePartitioningPolicy
 * @see org.eclipse.persistence.descriptors.partitioning.RangePartition
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface RangePartition {
    /** The String representation of the range start value. */
    String startValue() default "";
    /** The String representation of the range start value. */
    String endValue() default "";
    /** The connection pool to route queries to for this range. */
    String connectionPool();
}
