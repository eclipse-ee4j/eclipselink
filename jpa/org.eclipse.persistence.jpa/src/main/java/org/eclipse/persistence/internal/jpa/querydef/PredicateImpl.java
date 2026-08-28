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

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.Metamodel;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Predicate interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: The predicate forms the least specific expression node.  Predicates
 * result in boolean expressions that are combined to form the final expression.
 *
 * @see jakarta.persistence.criteria Predicate
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */
public class PredicateImpl extends CompoundExpressionImpl implements Predicate {

    @Serial
    private static final long serialVersionUID = 1L;

    protected BooleanOperator booloperator;

    public PredicateImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> parentExpressions, BooleanOperator operator){
        super(metamodel, expressionNode, parentExpressions);
        this.booloperator = operator;
    }

    static Predicate toPredicate(Metamodel metamodel, Expression<Boolean> expression) {
        if (expression == null) {
            return null;
        }

        if (((InternalExpression) expression).isPredicate()) {
            return (Predicate) expression;
        }

        org.eclipse.persistence.expressions.Expression node = ((InternalSelection) expression).getCurrentNode();

        List<Expression<?>> operands = new ArrayList<>();
        operands.add(expression);

        return new CompoundExpressionImpl(metamodel, node.equal(true), operands, "equals");
    }

    /**
     * INTERNAL:
     * This method returns null if this is not a conjunction/disjunction
     * TRUE if this is a conjunction, FALSE for disjunction.
     */
    public Boolean getJunctionValue() {
        if (currentNode != null) {
            return null;
        }

        return this.getOperator() == BooleanOperator.AND;
    }

    /**
     * Return the boolean operator for the predicate. If the predicate is
     * simple, this is AND.
     *
     * @return boolean operator for the predicate
     */
    @Override
    public BooleanOperator getOperator(){
        return this.booloperator;
    }

    /**
     * Return the top-level conjuncts or disjuncts of the predicate.
     *
     * @return list boolean expressions forming the predicate
     */
    @Override
    @SuppressWarnings("unchecked") // Otherwise generics won't match
    public List<Expression<Boolean>> getExpressions(){
        return (List<Expression<Boolean>>)(List<?>) this.expressions;
    }

    /**
     * Apply negation to the predicate.
     *
     * @return the negated predicate
     */
    @Override
    public Predicate not(){
        PredicateImpl predicateImpl;
        if (isJunction()) {
            if (getJunctionValue()) {
                predicateImpl = new PredicateImpl(metamodel, null, null, BooleanOperator.OR);
            } else {
                predicateImpl = new PredicateImpl(metamodel, null, null, BooleanOperator.AND);
            }
            predicateImpl.setIsNegated(true);
            return predicateImpl;
        }

        List<Expression<?>> list = new ArrayList<>();
        list.add(this);

        predicateImpl = new PredicateImpl(metamodel, currentNode.not(), list, this.booloperator);
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

    @Override
    public Predicate nullif(Expression<? extends Boolean> y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate nullif(Boolean y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate coalesce(Boolean y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate coalesce(Expression<? extends Boolean> y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
