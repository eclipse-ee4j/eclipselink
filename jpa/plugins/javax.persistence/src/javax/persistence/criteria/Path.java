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
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.Map;

/**
 * Represents a simple or compound attribute path from a bound type or
 * collection, and is a "primitive" expression.
 * 
 * @param <X>
 *            Type referenced by the path
 */
public interface Path<X> extends Expression<X> {
    /**
     * Return the bindable object that corresponds to the path expression.
     * 
     * @return bindable object corresponding to the path
     */
    Bindable<X> getModel();

    /**
     * Return the parent "node" in the path.
     * 
     * @return parent
     */
    Path<?> getParentPath();

    /**
     * Return the path corresponding to the referenced non-collection valued
     * attribute.
     * 
     * @param model
     *            attribute
     * @return path corresponding to the referenced attribute
     */
    <Y> Path<Y> get(Attribute<? super X, Y> model);

    /**
     * Return the path corresponding to the referenced collection-valued
     * attribute.
     * 
     * @param model
     *            collection-valued attribute
     * @return expression corresponding to the referenced attribute
     */
    <E, C extends java.util.Collection<E>> Expression<C> get(AbstractCollection<X, C, E> collection);

    /**
     * Return the path corresponding to the referenced map-valued attribute.
     * 
     * @param model
     *            map-valued attribute
     * @return expression corresponding to the referenced attribute
     */
    <K, V, M extends java.util.Map<K, V>> Expression<M> get(Map<X, K, V> collection);

    /**
     * Return an expression corresponding to the type of the path.
     * 
     * @return expression corresponding to the type of the path
     */
    Expression<Class<? extends X>> type();

    // String-based:
    /**
     * Return the path corresponding to the referenced attribute.
     * 
     * @param attName
     *            name of the attribute
     * @return path corresponding to the referenced attribute
     */
    <Y> Path<Y> get(String attName);
}