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
 *               Specification and licensing terms available from
 *               http://jcp.org/en/jsr/detail?id=317
 *     gyorke  - Post PFD updates
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence.criteria;

import java.util.List;
import javax.persistence.metamodel.ListAttribute;

/**
 * The interface ListJoin is the type of the result of joining to a collection
 * over an association or element collection that has been specified as a
 * java.util.List.
 * 
 * @param <Z>
 *            The source type of the join
 * @param <E>
 *            The element type of the target List
 */
public interface ListJoin<Z, E> 
		extends PluralJoin<Z, List<E>, E> {
    /**
     * Return the metamodel representation for the list.
     * 
     * @return metamodel type representing the List that is the target of the
     *         join
     */
    ListAttribute<? super Z, E> getModel();

    /**
     * Return an expression that corresponds to the index of the object in the
     * referenced association or element collection. This method must only be
     * invoked upon an object that represents an association or element
     * collection for which an order column has been defined.
     * 
     * @return Expression denoting the index
     */
    Expression<Integer> index();
}