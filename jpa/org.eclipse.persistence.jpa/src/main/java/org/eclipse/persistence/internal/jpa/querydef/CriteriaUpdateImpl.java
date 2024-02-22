/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
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

import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;

import java.io.Serial;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the CriteriaUpdate interface of
 * the JPA criteria API.
 * <p>
 * <b>Description</b>: This is the container class for the components that
 * define an Update Query.
 *
 * @see jakarta.persistence.criteria CriteriaUpdate
 *
 * @author Chris Delahunt
 * @since EclipseLink 2.5
 */
public class CriteriaUpdateImpl<T> extends CommonAbstractCriteriaImpl<T> implements CriteriaUpdate<T> {

    @Serial
    private static final long serialVersionUID = 5069513243268181150L;

    protected Root<T> root;
    protected UpdateAllQuery query;

    public CriteriaUpdateImpl(Metamodel metamodel, CriteriaBuilderImpl queryBuilder, Class<T> resultType){
        super(metamodel, queryBuilder, resultType);
        //initialize the query.
        query = new UpdateAllQuery(this.queryType);
        query.setShouldDeferExecutionInUOW(false);
    }

    @Override
    public Root<T> from(Class<T> entityClass) {
        return getRoot();
    }


    @Override
    public Root<T> from(EntityType<T> entity) {
        return getRoot();
    }

    @Override
    public Root<T> getRoot() {
        if (this.root == null) {
            if (getResultType() !=null) {
                EntityType entity = this.metamodel.entity(this.queryType);
                RootImpl newRoot = new RootImpl(entity, this.metamodel, this.queryType, query.getExpressionBuilder(), entity);
                this.root = newRoot;
            }
        }
        return this.root;
    }

    @Override
    public <Y, X extends Y> CriteriaUpdate<T> set(
            SingularAttribute<? super T, Y> attribute, X value) {
        if(value instanceof Expression) {
            findRootAndParameters((Expression)value);
        }
        query.addUpdate(attribute.getName(), value);
        return this;
    }

    @Override
    public <Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> attribute,
            Expression<? extends Y> value) {
        findRootAndParameters(value);
        query.addUpdate(attribute.getName(), ((InternalSelection)value).getCurrentNode());
        return this;
    }


    @Override
    public <Y, X extends Y> CriteriaUpdate<T> set(Path<Y> attribute, X value) {
        if(value instanceof Expression) {
            findRootAndParameters((Expression)value);
        }
        query.addUpdate(((PathImpl)attribute).getCurrentNode(), value);
        return this;
    }

    @Override
    public <Y> CriteriaUpdate<T> set(Path<Y> attribute,
            Expression<? extends Y> value) {
        findRootAndParameters(value);
        query.addUpdate(((PathImpl)attribute).getCurrentNode(), ((InternalSelection)value).getCurrentNode());
        return this;
    }


    @Override
    public CriteriaUpdate<T> set(String attributeName, Object value) {
        if(value instanceof Expression) {
            findRootAndParameters((Expression)value);
            value = ((InternalSelection)value).getCurrentNode();
        }
        query.addUpdate(attributeName, value);
        return this;
    }

    @Override
    public CriteriaUpdate<T> where(Expression<Boolean> restriction) {
        return (CriteriaUpdate<T>)super.where(restriction);
    }

    @Override
    public CriteriaUpdate<T> where(Predicate... restrictions) {
        return (CriteriaUpdate<T>)super.where(restrictions);
    }

    @Override
    protected void integrateRoot(RootImpl root) {
        if (this.root != root) {
            this.root = root;
        }
    }

    @Override
    protected org.eclipse.persistence.expressions.Expression getBaseExpression() {
        return query.getExpressionBuilder();
    }

    @Override
    protected DatabaseQuery getDatabaseQuery() {
        return (DatabaseQuery)query.clone();
    }
}
