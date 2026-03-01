/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     10/26/2012-2.5 Chris Delahunt
//       - 350469: JPA 2.1 Criteria Query framework Bulk Update/Delete support
//     08/22/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the CommonAbstractCriteria interface of
 * the JPA criteria API.
 * <p>
 * <b>Description</b>: This is the container class for the components that
 * define a query. This is the superclass of CriteriaQuery, SubQuery, CriteriaDelete
 * and CriteriaUpdate.
 *
 * @see jakarta.persistence.criteria CommonAbstractCriteria
 *
 * @author Chris Delahunt
 * @since EclipseLink 2.5
 */
public abstract class CommonAbstractCriteriaImpl<T>
        implements CommonAbstractCriteria, Serializable, CriteriaSelectInternal<T> {

    @Serial
    private static final long serialVersionUID = -2729946665208116620L;

    protected Metamodel metamodel;
    protected Expression<Boolean> where;
    protected CriteriaBuilderImpl queryBuilder;
    protected Class<T> queryType;

    protected Set<ParameterExpression<?>> parameters;

    public CommonAbstractCriteriaImpl(Metamodel metamodel, CriteriaBuilderImpl queryBuilder, Class<T> resultType){
        this.metamodel = metamodel;
        this.queryBuilder = queryBuilder;
        this.queryType = resultType;
    }

    // Allows complete copy of CommonAbstractCriteriaImpl. Required for cast implementation and shall remain pkg private.
    CommonAbstractCriteriaImpl(Metamodel metamodel, Expression<Boolean> where, CriteriaBuilderImpl queryBuilder,
                                         Class<T> queryType, Set<ParameterExpression<?>> parameters) {
        this.metamodel = metamodel;
        this.where = where;
        this.queryBuilder = queryBuilder;
        this.queryType = queryType;
        this.parameters = parameters;
    }

    /**
     * Return the predicate that corresponds to the where clause restriction(s).
     *
     * @return where clause predicate
     */
    @Override
    public Predicate getRestriction(){
        if (this.where == null) {
            return null;
        }
        if (((ExpressionImpl<?>)this.where).isPredicate()) {
            return (Predicate)this.where;
        }
        return this.queryBuilder.isTrue(this.where);
    }

    /**
     * Return the result type of the query.
     * If a result type was specified as an argument to the
     * createQuery method, that type will be returned.
     * If the query was created using the createTupleQuery
     * method, the result type is Tuple.
     * Otherwise, the result type is Object.
     * @return result type
     */
    @Override
    public Class<T> getResultType() {
        return this.queryType;
    }

    /**
     * Add a query root corresponding to the given entity, forming a Cartesian
     * product with any existing roots.
     *
     * @param entity
     *            metamodel entity representing the entity of type X
     * @return query root corresponding to the given entity
     */
    public <R> Root<R> internalFrom(EntityType<R> entity) {
        RootImpl<R> root = new RootImpl<>(
                entity,
                this.metamodel,
                entity.getBindableJavaType(),
                new ExpressionBuilder(entity.getBindableJavaType()),
                entity);
        integrateRoot(root);
        return root;
    }

    /**
     * Add a query root corresponding to the given entity, forming a Cartesian
     * product with any existing roots.
     *
     * @param entityClass
     *            the entity class
     * @return query root corresponding to the given entity
     */
    public <R> Root<R> internalFrom(Class<R> entityClass) {
        EntityType<R> entity = this.metamodel.entity(entityClass);
        return this.internalFrom(entity);
    }

    /**
     * Modify the query to restrict the query results according to the specified
     * boolean expression. Replaces the previously added restriction(s), if any.
     *
     * @param restriction a simple or compound boolean expression
     * @return the modified query
     */
    public CommonAbstractCriteria where(Expression<Boolean> restriction) {
        findRootAndParameters(restriction);
        this.where = restriction;
        return this;
    }

    /**
     * Modify the query to restrict the query results according to the
     * conjunction of the specified restriction predicates. Replaces the
     * previously added restriction(s), if any. If no restrictions are
     * specified, any previously added restrictions are simply removed.
     *
     * @param restrictions zero or more restriction predicates
     * @return the modified query
     */
    public CommonAbstractCriteria where(Predicate... restrictions) {
        return where(restrictions != null ? List.of(restrictions) : null);
    }

    /**
     * Modify the query to restrict the query results according to the
     * conjunction of the specified restriction predicates. Replaces the
     * previously added restriction(s), if any. If no restrictions are
     * specified, any previously added restrictions are simply removed.
     *
     * @param restrictions zero or more restriction predicates
     * @return the modified query
     * @since 5.0
     */
    public CommonAbstractCriteria where(List<Predicate> restrictions) {
        Predicate predicate = queryBuilder.and(restrictions);
        findRootAndParameters(predicate);
        where = predicate;
        return this;
    }

    /**
     * Specify that the query is to be used as a subquery having the specified
     * return type.
     *
     * @return subquery corresponding to the query
     */
    @Override
    public <U> Subquery<U> subquery(Class<U> type) {
        return new SubQueryImpl<>(metamodel, type, queryBuilder, this);
    }

    @Override
    public <U> Subquery<U> subquery(EntityType<U> type) {
        return subquery(type.getJavaType());
    }

    /**
     *  Used to use a root from a different query.
     */
    protected abstract void integrateRoot(RootImpl<?> root);

    protected void findRootAndParameters(Expression<?> predicate) {
        Objects.requireNonNull(predicate, "Predicate expression is null");
        ((InternalSelection) predicate).findRootAndParameters(this);
    }

    protected void findRootAndParameters(Order order) {
        Objects.requireNonNull(order, "Order is null");
        ((OrderImpl) order).findRootAndParameters(this);
    }

    protected abstract org.eclipse.persistence.expressions.Expression getBaseExpression();

    public void addParameter(ParameterExpression<?> parameter) {
        if (this.parameters == null) {
            this.parameters = new HashSet<>();
        }
        this.parameters.add(parameter);
    }

    protected abstract DatabaseQuery getDatabaseQuery();

    /**
     * Return the parameters of the query
     *
     * @return the query parameters
     */
    @Override
    public Set<ParameterExpression<?>> getParameters() {
        if (this.parameters == null) {
            this.parameters = new HashSet<>();
        }
        return this.parameters;
    }


    /**
     * Translates from the criteria query to a EclipseLink Database Query.
     *
     * @return EclipseLink {@link DatabaseQuery}
     */
    @Override
    public DatabaseQuery translate() {
        return translate(getDatabaseQuery());
    }

    /**
     * Translates from the criteria query to a EclipseLink Database Query.
     * Target {@link DatabaseQuery} instance is supplied.
     *
     * @param query target {@link DatabaseQuery} instance
     * @return EclipseLink {@link DatabaseQuery}
     */
    protected DatabaseQuery translate(DatabaseQuery query) {
        for (ParameterExpression<?> parameter : getParameters()) {
            query.addArgument(((ParameterExpressionImpl<?>)parameter).getInternalName(), parameter.getJavaType());
        }
        if (this.where != null) {
            if (((InternalExpression) this.where).isJunction()) {
                if (!((PredicateImpl) this.where).getJunctionValue()) {
                    query.setSelectionCriteria(new ConstantExpression(1, getBaseExpression()).equal(0));
                }
            } else {
                query.setSelectionCriteria(((InternalSelection) this.where).getCurrentNode());
            }
        }

        return query;
    }

}
