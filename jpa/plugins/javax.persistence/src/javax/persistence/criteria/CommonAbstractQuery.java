/*******************************************************************************
 * Copyright (c) 2008 - 2012 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/ 
package javax.persistence.criteria;

import javax.persistence.metamodel.EntityType;

/**
 * The <code>CommonAbstractQuery</code> interface defines functionality that is 
 * common to both top-level queries and subqueries as well as to update and
 * delete criteria operations.
 * It is not intended to be used directly in query construction.
 *
 * <p> All queries must have:
 *         a set of root entities (which may in turn own joins).
 * <p> All queries may have:
 *         a conjunction of restrictions.
 *
 * <p> Note that criteria queries and criteria update and delete operations
 * are typed differently.
 * Criteria queries are typed according to the query result type.
 * Update and delete operations are typed according to the target of the
 * update or delete.
 *
 * @since Java Persistence 2.1
 */
public interface CommonAbstractQuery {

    /**
     * Create and add a query root corresponding to the given entity.
     * @param entityClass  the entity class
     * @return query root corresponding to the given entity
     */
    <X> Root<X> from(Class<X> entityClass);

    /**
     * Create and add a query root corresponding to the given entity,
     * @param entity  metamodel entity representing the entity
     *                of type X
     * @return query root corresponding to the given entity
     */
    <X> Root<X> from(EntityType<X> entity);

    /**
     * Modify the query to restrict the query results according
     * to the specified boolean expression.
     * Replaces the previously added restriction(s), if any.
     * @param restriction  a simple or compound boolean expression
     * @return the modified query
     */    
    CommonAbstractQuery where(Expression<Boolean> restriction);

    /**
     * Modify the query to restrict the query results according 
     * to the conjunction of the specified restriction predicates.
     * Replaces the previously added restriction(s), if any.
     * If no restrictions are specified, any previously added
     * restrictions are simply removed.
     * @param restrictions  zero or more restriction predicates
     * @return the modified query
     */
    CommonAbstractQuery where(Predicate... restrictions);

    /**
     * Create a subquery of the query. 
     * @param type  the subquery result type
     * @return subquery 
     */
    <U> Subquery<U> subquery(Class<U> type);

    /**
     * Return the predicate that corresponds to the where clause
     * restriction(s), or null if no restrictions have been
     * specified.
     * @return where clause predicate
     */
    Predicate getRestriction();
 
}
