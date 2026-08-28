/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.BooleanExpression;
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

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.persistence.internal.jpa.querydef.PredicateImpl.toPredicate;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Join interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents a join of an attribute from a "From"element.
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
        return toPredicate(metamodel, on);
    }

    @Override
    public JoinImpl<Z, X> on(Expression<Boolean> restriction) {
        on = restriction;
        org.eclipse.persistence.expressions.Expression onExp = restriction == null ? null : ((ExpressionImpl) restriction).getCurrentNode();
        ((PathImpl) pathParent).getCurrentNode().join(currentNode, onExp);

        return this;
    }

    @Override
    public JoinImpl<Z, X> on(BooleanExpression... restrictions) {
        return on(restrictions != null ? List.of(restrictions) : null);
    }

    @Override
    public JoinImpl<Z, X> on(List<? extends Expression<Boolean>> restrictions) {
        if (restrictions == null || restrictions.isEmpty()) {
            return on((Expression<Boolean>) null);
        }

        CompoundExpressionImpl conjunction = (CompoundExpressionImpl) toPredicate(metamodel, restrictions.get(0));

        for (int i = 1; i < restrictions.size(); i++) {
            CompoundExpressionImpl next = (CompoundExpressionImpl) toPredicate(metamodel, restrictions.get(i));

            org.eclipse.persistence.expressions.Expression node =
                conjunction.getCurrentNode()
                           .and(next.getCurrentNode());

            conjunction.setParentNode(node);
            next.setParentNode(node);

            List<Expression<?>> operands = new ArrayList<>();
            operands.add(conjunction);
            operands.add(next);

            conjunction = new PredicateImpl(metamodel, node, operands, BooleanOperator.AND);
        }

        return on(conjunction);
    }

    @Override
    public <T extends X> Join<Z, T> treat(@Nonnull Class<T> type) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
