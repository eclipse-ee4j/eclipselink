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
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *               Specification available from http://jcp.org/en/jsr/detail?id=317
 *     gyorke  - Post PFD updates
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence.criteria;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.MapAttribute;

/**
 * Represents a simple or compound attribute path from a bound type or
 * collection, and is a "primitive" expression.
 * 
 * @param <X>
 *            Type referenced by the path
 *            
 * @since Java Persistence 2.0
 */
public interface Path<X> extends Expression<X> {
    /**
     * Return the Bindable object that corresponds to the path expression.
     * 
     * @return Bindable object corresponding to the path
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
    <Y> Path<Y> get(SingularAttribute<? super X, Y> attribute);

    /**
     * Return the path corresponding to the referenced collection-valued
     * attribute.
     * 
     * @param model
     *            collection-valued attribute
     * @return expression corresponding to the referenced attribute
     */
    <E, C extends java.util.Collection<E>> Expression<C> get(PluralAttribute<X, C, E> collection);

    /**
     * Return the path corresponding to the referenced map-valued attribute.
     * 
     * @param model
     *            map-valued attribute
     * @return expression corresponding to the referenced attribute
     */
    <K, V, M extends java.util.Map<K, V>> Expression<M> get(MapAttribute<X, K, V> map);

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
    <Y> Path<Y> get(String attributeName);
}