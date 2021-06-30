/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/26/2012-2.5 Chris Delahunt 
 *       - 350469: JPA 2.1 Criteria Query framework Bulk Update/Delete support
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the CriteriaUpdate interface of
 * the JPA criteria API.
 * <p>
 * <b>Description</b>: This is the container class for the components that
 * define an Update Query.
 * <p>
 * 
 * @see javax.persistence.criteria CriteriaUpdate
 * 
 * @author Chris Delahunt
 * @since EclipseLink 2.5
 */
public class CriteriaUpdateImpl<T> extends CommonAbstractCriteriaImpl<T> implements CriteriaUpdate<T>, Serializable {
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

    protected void integrateRoot(RootImpl root) {
        if (this.root != root) {
            this.root = root;
        }
    }

    protected org.eclipse.persistence.expressions.Expression getBaseExpression() {
        return query.getExpressionBuilder();
    }

    protected DatabaseQuery getDatabaseQuery() {
        return (DatabaseQuery)query.clone();
    }
}
