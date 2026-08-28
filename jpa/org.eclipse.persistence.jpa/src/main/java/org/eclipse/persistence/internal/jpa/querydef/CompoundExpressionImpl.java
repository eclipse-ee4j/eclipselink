/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.criteria.ComparableExpression;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.Metamodel;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;

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
public class CompoundExpressionImpl extends FunctionExpressionImpl<Boolean> implements Predicate {

    @Serial
    private static final long serialVersionUID = 1L;

    protected boolean isNegated = false;

    public <T> CompoundExpressionImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions){
        super(metamodel, CoreClassConstants.BOOLEAN, expressionNode, compoundExpressions);
    }

    public <T> CompoundExpressionImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions, String operator){
        super(metamodel, CoreClassConstants.BOOLEAN, expressionNode, compoundExpressions, operator);
    }

    /**
     * Return the boolean operator for the predicate. If the predicate is
     * simple, this is AND.
     *
     * @return boolean operator for the predicate
     */
    @Override
    public BooleanOperator getOperator(){
        return BooleanOperator.AND;
    }

    @Override
    /*
     * Return the top-level conjuncts or disjuncts of the predicate.
     *
     * @return list boolean expressions forming the predicate
     */
    public List<Expression<Boolean>> getExpressions(){
        return new ArrayList<>();
    }

    @Override
    public boolean isCompoundExpression(){
        return true;
    }

    @Override
    public boolean isExpression(){
        return false;
    }

    /**
     * Has negation been applied to the predicate.
     *
     * @return boolean indicating if the predicate has been negated
     */
    @Override
    public boolean isNegated(){
        return isNegated;
    }

    /**
     * Apply negation to the predicate.
     *
     * @return the negated predicate
     */
    @Override
    public Predicate not(){
        List<Expression<?>> list = new ArrayList<>();
        list.add(this);

        CompoundExpressionImpl expression = new CompoundExpressionImpl(metamodel, currentNode.not(), list, "not");
        expression.setIsNegated(true);

        return expression;
    }

    @Override
    public boolean isPredicate(){
        return true;
    }

    protected void setIsNegated(boolean isNegated){
        this.isNegated = isNegated;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(BooleanOperator operator) {
        //
    }

    /**
     * This method is used to store what will be the parent EclipseLink expression in the case the tree needs to be altered.
     * Currently used for In.
     */
    public void setParentNode(org.eclipse.persistence.expressions.Expression parentNode){
        //no-op but can not be abstract as CompoundExpressionImpl is not abstract
    }

    @Override
    public Predicate nullif(Boolean y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate nullif(Expression<? extends Boolean> y) {
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

    @Override
    public Predicate and(Expression<Boolean> y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate or(Expression<Boolean> y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate greaterThan(Expression<? extends Boolean> y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate greaterThan(Boolean y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate greaterThanOrEqualTo(Expression<? extends Boolean> y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate greaterThanOrEqualTo(Boolean y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate lessThan(Expression<? extends Boolean> y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate lessThan(Boolean y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate lessThanOrEqualTo(Expression<? extends Boolean> y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate lessThanOrEqualTo(Boolean y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate between(Expression<? extends Boolean> x, Expression<? extends Boolean> y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Predicate between(Boolean x, Boolean y) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ComparableExpression<Boolean> max() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ComparableExpression<Boolean> min() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Order asc() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Order desc() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Order asc(Nulls nullPrecedence) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Order desc(Nulls nullPrecedence) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
