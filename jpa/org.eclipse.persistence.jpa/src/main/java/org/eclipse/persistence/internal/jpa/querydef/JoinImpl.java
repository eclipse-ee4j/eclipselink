/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - Initial development
//

package org.eclipse.persistence.internal.jpa.querydef;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Predicate.BooleanOperator;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Join interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents a join of an attribute from a "From"element.
 * <p>
 *
 * @see jakarta.persistence.criteria Join
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */

public class JoinImpl<Z, X> extends FromImpl<Z, X> implements Join<Z, X>, Fetch<Z, X> {

    protected JoinType joinType;
    protected Expression<Boolean> on;

    public <T> JoinImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact){
        this(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact,JoinType.INNER);
    }

    public <T> JoinImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact, JoinType joinType){
        super(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact);
        this.joinType = joinType;
    }

    public <T> JoinImpl(Path<Z> parentPath, ManagedType managedType, Metamodel metamodel, Class<X> javaClass, org.eclipse.persistence.expressions.Expression expressionNode, Bindable<T> modelArtifact, JoinType joinType, FromImpl correlatedParent){
        super(parentPath, managedType, metamodel, javaClass, expressionNode, modelArtifact, correlatedParent);
        this.joinType = joinType;
    }

    /**
     * Return the metamodel Attribute corresponding to the join.
     *
     * @return metamodel Attribute type for the join
     */
    @Override
    public Attribute<? super Z, ?> getAttribute(){
        //Only attributes will be stored in this field so it is ok
        //to perform the cast.
        return (Attribute<? super Z, ?>) this.modelArtifact;
    }

    /**
     * Return the parent of the join.
     *
     * @return join parent
     */
    @Override
    public From<?, Z> getParent(){
        // this cast is acceptable as by design the parent of a Join must be a from implementor
        return (From<?, Z>)pathParent;
    }

    /**
     * Return the join type.
     *
     * @return join type
     */
    @Override
    public JoinType getJoinType(){
        return joinType;
    }

    @Override
    protected <T> Expression<T> buildExpressionForAs(Class<T> type) {
        managedType = metamodel.managedType(type);
        currentNode = currentNode.treat(type);
        return (Expression<T>)this;
    }

    @Override
    public Predicate getOn() {
        if (this.on == null) {
            return null;
        }
        if (((ExpressionImpl)this.on).isPredicate()) return (Predicate)this.on;

        //see queryBuilder.isTrue(this.on);
        List list = new ArrayList();
        list.add(this.on);
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)this.on).getCurrentNode().equal(true), list, "equals");
    }

    @Override
    public JoinImpl<Z, X> on(Expression<Boolean> restriction) {
        this.on = restriction;
        org.eclipse.persistence.expressions.Expression onExp = restriction==null? null:((ExpressionImpl)restriction).getCurrentNode();
        ((PathImpl)this.pathParent).getCurrentNode().join(this.currentNode, onExp);

        return this;
    }

    @Override
    public JoinImpl<Z, X> on(Predicate... restrictions) {
        org.eclipse.persistence.expressions.Expression onExp;
        if (restrictions == null || restrictions.length == 0){
            this.on = null;
            onExp = null;
        } else {
            //from criteriaQueryImpl.where(Predicate... restrictions)
            Predicate a = restrictions[0];
            for (int i = 1; i < restrictions.length; ++i){
                org.eclipse.persistence.expressions.Expression currentNode = ((CompoundExpressionImpl)a).getCurrentNode().and(
                        ((CompoundExpressionImpl)restrictions[i]).getCurrentNode());
                ((CompoundExpressionImpl)a).setParentNode(currentNode);
                ((CompoundExpressionImpl)restrictions[i]).setParentNode(currentNode);
                ArrayList list = new ArrayList();
                list.add(a);
                list.add(restrictions[i]);
                a = new PredicateImpl(this.metamodel, currentNode, list, BooleanOperator.AND);
            }
            this.on = a;
            onExp = ((ExpressionImpl)a).getCurrentNode();
        }

        ((PathImpl)this.pathParent).getCurrentNode().join(this.currentNode, onExp);
        return this;
    }

}
