/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - Initial development
//

package org.eclipse.persistence.internal.jpa.querydef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.metamodel.Metamodel;

import org.eclipse.persistence.internal.expressions.CompoundExpression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.LogicalExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the In interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This class represents an In predicate.
 *
 * @see jakarta.persistence.criteria Join
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */

public class InImpl<T> extends CompoundExpressionImpl implements In<T> {

    protected org.eclipse.persistence.expressions.Expression parentNode;

    public InImpl(Metamodel metamodel, ExpressionImpl<? extends T> leftExpression, Collection<?> values, List<Expression<?>> expressions) {
        super(metamodel, ((InternalSelection)leftExpression).getCurrentNode().in(values), expressions, "in");
        this.leftExpression = leftExpression;

    }

    public InImpl(Metamodel metamodel, ExpressionImpl<T> leftExpression, ExpressionImpl<?> rightExp, List<Expression<?>> expressions) {
        super(metamodel,
            (rightExp.isParameter()?
                leftExpression.getCurrentNode().in(rightExp.getCurrentNode()):
                leftExpression.getCurrentNode().equal(rightExp.getCurrentNode())),
            expressions, "in");
        this.leftExpression = leftExpression;

    }

    protected ExpressionImpl<? extends T> leftExpression;

    /**
     * Returns the expression to be tested against the
     * list of values.
     * @return expression
     */
    @Override
    @SuppressWarnings("unchecked") // JPA API generics clash
    public Expression<T> getExpression(){
        return (Expression<T>) this.leftExpression;
    }

    @Override
    public void findRootAndParameters(CommonAbstractCriteriaImpl<?> query) {
        super.findRootAndParameters(query);
    }

    @Override
    public boolean isPredicate(){
        return true;
    }

    /**
     *  Add to list of values to be tested against.
     *  @param value value
     *  @return in predicate
     */
    @Override
    @SuppressWarnings("unchecked") // (Collection<T>) ...
    public In<T> value(T value){
        ((Collection<T>) ((ConstantExpression) ((CompoundExpression) this.currentNode).getSecondChild()).getValue()).add(value);
        return this;
    }

    /**
     *  Add to list of values to be tested against.
     *  @param value expression
     *  @return in predicate
     */
    @Override
    @SuppressWarnings("unchecked") // (Collection<org.eclipse.persistence.expressions.Expression>) ...
    public In<T> value(Expression<? extends T> value) {
        if (!(((InternalExpression) value).isLiteral() || ((InternalExpression) value).isParameter())) {
            RelationExpression baseIn = (RelationExpression) this.currentNode;
            this.currentNode = baseIn.getFirstChild().in(((SubQueryImpl<?>) value).subQuery);
            if (this.parentNode != null) {
                if (this.parentNode.isCompoundExpression()) {
                    CompoundExpression logExp = (LogicalExpression) this.parentNode;
                    if (logExp.getFirstChild() == baseIn) {
                        logExp.create(this.currentNode, logExp.getSecondChild(), logExp.getOperator());
                    } else {
                        logExp.create(logExp.getFirstChild(), this.currentNode, logExp.getOperator());
                    }
                } else {
                    FunctionExpression funcExp = (FunctionExpression) this.parentNode;
                    funcExp.getChildren().set(funcExp.getChildren().indexOf(baseIn), this.currentNode);
                }
            }
        } else {
            if (this.currentNode.isRelationExpression()) {
                RelationExpression baseIn = (RelationExpression) this.currentNode;
                org.eclipse.persistence.expressions.Expression resultExp = ((InternalSelection)value).getCurrentNode();
                resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, baseIn.getFirstChild());
                ((Collection<org.eclipse.persistence.expressions.Expression>) ((ConstantExpression) baseIn.getSecondChild()).getValue()).add(resultExp);

                // Add to the expression list for #findRootAndParameters()
                this.expressions.add(value);
            } else {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("CANNOT_ADD_CONSTANTS_TO_SUBQUERY_IN"));
            }
        }
        return this;
    }

    /**
     * This method is used to store what will be the parent EclipseLink expression in the case the tree needs to be altered.
     * Currently used for In.
     */
    @Override
    public void setParentNode(org.eclipse.persistence.expressions.Expression parentNode){
        this.parentNode = parentNode;
    }

    @Override
    public Predicate not(){
        parentNode = this.getCurrentNode().not();
        List<Expression<?>> list = new ArrayList<>();
        list.add(this);
        CompoundExpressionImpl expr = new CompoundExpressionImpl(this.metamodel, parentNode, list, "not");
        expr.setIsNegated(true);
        return expr;
    }
}
