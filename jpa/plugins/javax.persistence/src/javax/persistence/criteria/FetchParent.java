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

import javax.persistence.metamodel.AbstractCollection;
import javax.persistence.metamodel.Attribute;

/**
 * Represents an element of the from clause which may function as the parent of
 * Fetches.
 * 
 * @param <Z>
 * @param <X>
 */
public interface FetchParent<Z, X> {
    /**
     * Return the fetch joins that have been made from this type.
     * 
     * @return fetch joins made from this type
     */
    java.util.Set<Fetch<X, ?>> getFetches();

    /**
     * Fetch join to the specified attribute using an inner join.
     * 
     * @param assoc
     *            target of the join
     * @return the resulting fetch join
     */
    <Y> Fetch<X, Y> fetch(Attribute<? super X, Y> assoc);

    /**
     * Fetch join to the specified attribute using the given join type.
     * 
     * @param assoc
     *            target of the join
     * @param jt
     *            join type
     * @return the resulting fetch join
     */
    <Y> Fetch<X, Y> fetch(Attribute<? super X, Y> assoc, JoinType jt);

    /**
     * Fetch join to the specified collection using an inner join.
     * 
     * @param assoc
     *            target of the join
     * @return the resulting join
     */
    <Y> Fetch<X, Y> fetch(AbstractCollection<? super X, ?, Y> assoc);

    /**
     * Fetch join to the specified collection using the given join type.
     * 
     * @param assoc
     *            target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <Y> Fetch<X, Y> fetch(AbstractCollection<? super X, ?, Y> assoc, JoinType jt);

    // String-based:
    /**
     * Fetch join to the specified attribute or association using an inner join.
     * 
     * @param assocName
     *            name of the attribute or association for the target of the
     *            join
     * @return the resulting fetch join
     */
    <Y> Fetch<X, Y> fetch(String assocName);

    /**
     * Fetch join to the specified attribute or association using the given join
     * type.
     * 
     * @param name
     *            assocName of the attribute or association for the target of
     *            the join
     * @param jt
     *            join type
     * @return the resulting fetch join
     */
    <Y> Fetch<X, Y> fetch(String assocName, JoinType jt);
}