/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     10/26/2012-2.5 Chris Delahunt
//       - 350469: JPA 2.1 Criteria Query framework Bulk Update/Delete support
package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

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
 * <p>
 *
 * @see javax.persistence.criteria CommonAbstractCriteria
 *
 * @author Chris Delahunt
 * @since EclipseLink 2.5
 */
public abstract class CommonAbstractCriteriaImpl<T> implements CommonAbstractCriteria, Serializable {

    private static final long serialVersionUID = -2729946665208116620L;

    protected Metamodel metamodel;
    protected Expression<Boolean> where;
    protected CriteriaBuilderImpl queryBuilder;
    protected Class queryType;

    protected Set<ParameterExpression<?>> parameters;

    public CommonAbstractCriteriaImpl(Metamodel metamodel, CriteriaBuilderImpl queryBuilder, Class<T> resultType){
        this.metamodel = metamodel;
        this.queryBuilder = queryBuilder;
        this.queryType = resultType;
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
        if (((ExpressionImpl)this.where).isPredicate()) {
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
    public Class<T> getResultType(){
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
    public Root internalFrom(EntityType entity) {
        RootImpl root = new RootImpl(entity, this.metamodel, entity.getBindableJavaType(), new ExpressionBuilder(entity.getBindableJavaType()), entity);
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
    public Root internalFrom(Class entityClass) {
        EntityType entity = this.metamodel.entity(entityClass);
        return this.internalFrom(entity);
    }

    /**
     * Modify the query to restrict the query results according to the specified
     * boolean expression. Replaces the previously added restriction(s), if any.
     *
     * @param restriction
     *            a simple or compound boolean expression
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
     * @param restrictions
     *            zero or more restriction predicates
     * @return the modified query
     */
    public CommonAbstractCriteria where(Predicate... restrictions) {
        if (restrictions == null || restrictions.length == 0){
            this.where = null;
        }
        Predicate predicate = this.queryBuilder.and(restrictions);
        findRootAndParameters(predicate);
        this.where = predicate;
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
        return new SubQueryImpl<U>(metamodel, type, queryBuilder, this);
    }

    /**
     *  Used to use a root from a different query.
     */
    protected abstract void integrateRoot(RootImpl root);

    protected void findRootAndParameters(Expression<?> predicate) {
        ((InternalSelection) predicate).findRootAndParameters(this);
    }

    protected abstract org.eclipse.persistence.expressions.Expression getBaseExpression();

    public void addParameter(ParameterExpression<?> parameter) {
        if (this.parameters == null) {
            this.parameters = new HashSet<ParameterExpression<?>>();
        }
        this.parameters.add(parameter);
    }

    protected abstract DatabaseQuery getDatabaseQuery();

    /**
     * Return the parameters of the query
     *
     * @return the query parameters
     */
    public Set<ParameterExpression<?>> getParameters() {
        if (this.parameters == null) {
            this.parameters = new HashSet<ParameterExpression<?>>();
        }
        return this.parameters;
    }


    /**
     * Translates from the criteria query to a EclipseLink Database Query.
     */
    public DatabaseQuery translate() {
        DatabaseQuery query = getDatabaseQuery();
        for (ParameterExpression<?> parameter : getParameters()) {
            query.addArgument(((ParameterExpressionImpl)parameter).getInternalName(), parameter.getJavaType());
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
