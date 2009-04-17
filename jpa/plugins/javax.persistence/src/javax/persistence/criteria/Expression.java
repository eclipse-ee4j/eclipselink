/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence.criteria;

/**
 * Type for query expressions.
 * 
 * @param <T>
 *            the type of the expression
 */
public interface Expression<T> extends Selection<T> {
    /**
     * Return the Java type of the expression.
     * 
     * @return the Java type of the expression
     */
    Class<T> getJavaType();

    /**
     * Apply a predicate to test whether the expression is null.
     * 
     * @return predicate testing whether the expression is null
     */
    Predicate isNull();

    /**
     * Apply a predicate to test whether the expression is not null.
     * 
     * @return predicate testing whether the expression is not null.
     */
    Predicate isNotNull();

    /**
     * Apply a predicate to test whether the expression is a member of the
     * argument list.
     * 
     * @param values
     * @return predicate testing for membership in the list
     */
    Predicate in(Object... values);

    /**
     * Perform a typecast upon the expression. Warning: may result in a runtime
     * failure.
     * 
     * @param type
     * @return expression
     */
    <X> Expression<X> as(Class<X> type);
}