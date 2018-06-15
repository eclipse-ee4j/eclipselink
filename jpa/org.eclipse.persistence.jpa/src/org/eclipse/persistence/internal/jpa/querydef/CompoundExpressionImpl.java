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

import org.eclipse.persistence.internal.helper.ClassConstants;

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
public class CompoundExpressionImpl extends FunctionExpressionImpl<Boolean> implements Predicate{

    protected boolean isNegated = false;

    public <T> CompoundExpressionImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions){
        super(metamodel, (Class<Boolean>)ClassConstants.BOOLEAN, expressionNode, compoundExpressions);
    }

    public <T> CompoundExpressionImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions, String operator){
        super(metamodel, (Class<Boolean>)ClassConstants.BOOLEAN, expressionNode, compoundExpressions, operator);
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
    /**
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
        List<Expression<?>> list = new ArrayList();
        list.add(this);
        CompoundExpressionImpl expr = new CompoundExpressionImpl(this.metamodel, this.currentNode.not(), list, "not");
        expr.setIsNegated(true);
        return expr;
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

}
