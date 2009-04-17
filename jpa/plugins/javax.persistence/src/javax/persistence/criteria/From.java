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

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Collection;
import javax.persistence.metamodel.List;
import javax.persistence.metamodel.Map;
import javax.persistence.metamodel.Set;

/**
 * Represents a bound type, usually an entity that appears in the from clause,
 * but may also be an embeddable belonging to an entity in the from clause.
 * Serves as a factory for Joins of associations, embeddables and collections
 * belonging to the type, and for Paths of attributes belonging to the type.
 * 
 * @param <Z>
 * @param <X>
 */
public interface From<Z, X> extends Path<X>, FetchParent<Z, X> {
    /**
     * Return the joins that have been made from this type.
     * 
     * @return joins made from this type
     */
    java.util.Set<Join<X, ?>> getJoins();

    /**
     * Join to the specified attribute using an inner join.
     * 
     * @param attribute
     *            target of the join
     * @return the resulting join
     */
    <Y> Join<X, Y> join(Attribute<? super X, Y> attribute);

    /**
     * Join to the specified attribute, using the given join type.
     * 
     * @param attribute
     *            target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <Y> Join<X, Y> join(Attribute<? super X, Y> attribute, JoinType jt);

    /**
     * Join to the specified Collection-valued attribute using an inner join.
     * 
     * @param collection
     *            target of the join
     * @return the resulting join
     */
    <Y> CollectionJoin<X, Y> join(Collection<? super X, Y> collection);

    /**
     * Join to the specified Set-valued attribute using an inner join.
     * 
     * @param set
     *            target of the join
     * @return the resulting join
     */
    <Y> SetJoin<X, Y> join(Set<? super X, Y> set);

    /**
     * Join to the specified List-valued attribute using an inner join.
     * 
     * @param list
     *            target of the join
     * @return the resulting join
     */
    <Y> ListJoin<X, Y> join(List<? super X, Y> list);

    /**
     * Join to the specified Map-valued attribute using an inner join.
     * 
     * @param map
     *            target of the join
     * @return the resulting join
     */
    <K, V> MapJoin<X, K, V> join(Map<? super X, K, V> map);

    /**
     * Join to the specified Collection-valued attribute using the given join
     * type.
     * 
     * @param collection
     *            target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <Y> CollectionJoin<X, Y> join(Collection<? super X, Y> collection, JoinType jt);

    /**
     * Join to the specified Set-valued attribute using the given join type.
     * 
     * @param set
     *            target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <Y> SetJoin<X, Y> join(Set<? super X, Y> set, JoinType jt);

    /**
     * Join to the specified List-valued attribute using the given join type.
     * 
     * @param list
     *            target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <Y> ListJoin<X, Y> join(List<? super X, Y> list, JoinType jt);

    /**
     * Join to the specified Map-valued attribute using the given join type.
     * 
     * @param map
     *            target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <K, V> MapJoin<X, K, V> join(Map<? super X, K, V> map, JoinType jt);

    // String-based:
    /**
     * Join to the specified attribute using an inner join.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @return the resulting join
     */
    <W, Y> Join<W, Y> join(String attributeName);

    /**
     * Join to the specified Collection-valued attribute using an inner join.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @return the resulting join
     */
    <W, Y> CollectionJoin<W, Y> joinCollection(String attributeName);

    /**
     * Join to the specified Set-valued attribute using an inner join.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @return the resulting join
     */
    <W, Y> SetJoin<W, Y> joinSet(String attributeName);

    /**
     * Join to the specified List-valued attribute using an inner join.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @return the resulting join
     */
    <W, Y> ListJoin<W, Y> joinList(String attributeName);

    /**
     * Join to the specified Map-valued attribute using an inner join.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @return the resulting join
     */
    <W, K, V> MapJoin<W, K, V> joinMap(String attributeName);

    /**
     * Join to the specified attribute using the given join type.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <W, Y> Join<W, Y> join(String attributeName, JoinType jt);

    /**
     * Join to the specified Collection-valued attribute using the given join
     * type.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <W, Y> CollectionJoin<W, Y> joinCollection(String attributeName, JoinType jt);

    /**
     * Join to the specified Set-valued attribute using the given join type.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <W, Y> SetJoin<W, Y> joinSet(String attributeName, JoinType jt);

    /**
     * Join to the specified List-valued attribute using the given join type.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <W, Y> ListJoin<W, Y> joinList(String attributeName, JoinType jt);

    /**
     * Join to the specified Map-valued attribute using the given join type.
     * 
     * @param attributeName
     *            name of the attribute for the target of the join
     * @param jt
     *            join type
     * @return the resulting join
     */
    <W, K, V> MapJoin<W, K, V> joinMap(String attributeName, JoinType jt);
}