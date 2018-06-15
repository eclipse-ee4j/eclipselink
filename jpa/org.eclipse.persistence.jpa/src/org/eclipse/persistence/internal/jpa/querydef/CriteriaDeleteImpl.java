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

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the CriteriaDelete interface of
 * the JPA criteria API.
 * <p>
 * <b>Description</b>: This is the container class for the components that
 * define a Delete Query.
 * <p>
 *
 * @see javax.persistence.criteria CriteriaDelete
 *
 * @author Chris Delahunt
 * @since EclipseLink 2.5
 */
public class CriteriaDeleteImpl<T> extends CommonAbstractCriteriaImpl<T> implements CriteriaDelete<T> {

    private static final long serialVersionUID = -3858349449764066901L;

    protected Root<T> root;

    public CriteriaDeleteImpl(Metamodel metamodel, CriteriaBuilderImpl queryBuilder, Class<T> resultType){
        super(metamodel, queryBuilder, resultType);
    }

    @Override
    public Root<T> from(Class<T> entityClass) {
        return this.internalFrom(entityClass);
    }

    @Override
    public Root<T> from(EntityType<T> entity) {
        return this.internalFrom(entity);
    }

    @Override
    public Root<T> getRoot() {
        if (this.root == null) {
            if (getResultType() !=null) {
                return this.from(getResultType());
            }
        }
        return this.root;
    }

    @Override
    public CriteriaDelete<T> where(Expression<Boolean> restriction) {
        return (CriteriaDelete<T>)super.where(restriction);
    }

    @Override
    public CriteriaDelete<T> where(Predicate... restrictions) {
        return (CriteriaDelete<T>)super.where(restrictions);
    }

    @Override
    protected void integrateRoot(RootImpl root) {
        if (this.root !=root) {
            this.root =root;
        }
    }

    @Override
    protected org.eclipse.persistence.expressions.Expression getBaseExpression() {
        if (this.root == null) {
            return new ExpressionBuilder();
        } else {
            return ((RootImpl)this.root).getCurrentNode();
        }
    }

    @Override
    protected DatabaseQuery getDatabaseQuery() {
        org.eclipse.persistence.queries.DeleteAllQuery query = new org.eclipse.persistence.queries.DeleteAllQuery(this.queryType, getBaseExpression());
        query.setShouldDeferExecutionInUOW(false);
        return query;
    }
}
