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

import java.util.Map;

/**
 * The interface MapJoin is the type of the result of joining to a collection
 * over an association or element collection that has been specified as a
 * java.util.Map.
 * 
 * @param <Z>
 *            The source type of the join
 * @param <K>
 *            The type of the target Map key
 * @param <V>
 *            The type of the target Map value
 */
public interface MapJoin<Z, K, V> extends AbstractCollectionJoin<Z, Map<K, V>, V> {
    /**
     * Return the metamodel representation for the map.
     * 
     * @return metamodel type representing the Map that is the target of the
     *         join
     */
    javax.persistence.metamodel.Map<? super Z, K, V> getModel();

    /**
     * Specify an innerjoin over the map key.
     * 
     * @return result of joining over the map key
     */
    Join<Map<K, V>, K> joinKey();

    /**
     * Specify a join over the map key using the given join type.
     * 
     * @param jt
     *            join type
     * @return result of joining over the map key
     */
    Join<Map<K, V>, K> joinKey(JoinType jt);

    /**
     * Return a path expression that corresponds to the map key.
     * 
     * @return Path corresponding to map key
     */
    Path<K> key();

    /**
     * Return a path expression that corresponds to the map value. This method
     * is for stylistic use only: it just returns this.
     * 
     * @return Path corresponding to the map value
     */
    Path<V> value(); // Unnecessary - just returns this

    /**
     * Return an expression that corresponds to the map entry.
     * 
     * @return Expression corresponding to the map entry
     */
    Expression<Map.Entry<K, V>> entry();
}