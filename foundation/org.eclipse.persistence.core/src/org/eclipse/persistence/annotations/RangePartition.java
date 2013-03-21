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
 * Represent a specific range partition.
 * Values >= startValue and <= endValue will be routed to the connection pool.
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
