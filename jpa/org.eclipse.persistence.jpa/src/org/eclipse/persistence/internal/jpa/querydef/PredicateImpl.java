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
//     Gordon Yorke - Initial development
//
package org.eclipse.persistence.internal.jpa.querydef;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Metamodel;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Predicate interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: The predicate forms the least specific expression node.  Predicates
 * result in boolean expressions that are combined to form the final expression.
 * <p>
 *
 * @see javax.persistence.criteria Predicate
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */
public class PredicateImpl extends CompoundExpressionImpl implements Predicate {

    protected BooleanOperator booloperator;

    public <T> PredicateImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> parentExpressions, BooleanOperator operator){
        super(metamodel, expressionNode, parentExpressions);
        this.booloperator = operator;
    }

    /**
     * INTERNAL:
     * This method returns null if this is not a conjunction/disjunction
     * TRUE if this is a conjunction, FALSE for disjunction.
     */
    public Boolean getJunctionValue() {
        if (this.currentNode != null) {
            return null;
        }
        return this.getOperator() == BooleanOperator.AND;
    }

    @Override
    /**
     * Return the boolean operator for the predicate. If the predicate is
     * simple, this is AND.
     *
     * @return boolean operator for the predicate
     */
    public BooleanOperator getOperator(){
        return this.booloperator;
    }

    /**
     * Return the top-level conjuncts or disjuncts of the predicate.
     *
     * @return list boolean expressions forming the predicate
     */
    @Override
    public List<Expression<Boolean>> getExpressions(){
        return this.expressions;
    }

    /**
     * Apply negation to the predicate.
     *
     * @return the negated predicate
     */
    @Override
    public Predicate not(){
        PredicateImpl predicateImpl = null;
        if (isJunction()) {
            if (getJunctionValue()) {
                predicateImpl = new PredicateImpl(this.metamodel, null, null, BooleanOperator.OR);
            } else {
                predicateImpl = new PredicateImpl(this.metamodel, null, null, BooleanOperator.AND);
            }
            predicateImpl.setIsNegated(true);
            return predicateImpl;
        }
        List<Expression<?>> list = new ArrayList();
        list.add(this);
        predicateImpl = new PredicateImpl(this.metamodel, this.currentNode.not(), list, this.booloperator);
        predicateImpl.setIsNegated(true);
        return predicateImpl;
    }

    /**
     * @param operator the operator to set
     */
    @Override
    public void setOperator(BooleanOperator operator) {
        this.booloperator = operator;
    }

    @Override
    public boolean isJunction(){
        return this.currentNode == null;
    }

    @Override
    public boolean isCompoundExpression(){
        return false;
    }
}
