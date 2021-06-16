/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
