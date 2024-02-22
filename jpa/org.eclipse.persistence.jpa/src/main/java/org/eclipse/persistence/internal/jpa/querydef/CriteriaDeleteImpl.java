/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.querydef;

import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.DatabaseQuery;

import java.io.Serial;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the CriteriaDelete interface of
 * the JPA criteria API.
 * <p>
 * <b>Description</b>: This is the container class for the components that
 * define a Delete Query.
 *
 * @see jakarta.persistence.criteria CriteriaDelete
 *
 * @author Chris Delahunt
 * @since EclipseLink 2.5
 */
public class CriteriaDeleteImpl<T> extends CommonAbstractCriteriaImpl<T> implements CriteriaDelete<T> {

    @Serial
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
