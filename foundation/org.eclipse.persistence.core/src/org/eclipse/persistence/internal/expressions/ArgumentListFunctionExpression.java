/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial API and implementation from for JPA 2.0 criteria API
//     IBM - Bug 537795: CASE THEN and ELSE scalar expression Constants should not be casted to CASE operand type
package org.eclipse.persistence.internal.expressions;

import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.expressions.ListExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;

/**
 * INTERNAL:
 * This an extended FunctionExpression that allows the argument list to grow after it is created.
 * New expressions may be added to the list and the printing of the database string is handled automatically
 *
 * This expression's addChild() method is used to construct the list.
 *
 * Note: This expression is designed to handle addition of children up until the first normalization (execution)
 * of a query involving this expression.  After normalization, the behavior is undefined.
 *
 * @see ListExpressionOperator
 * @see Expression#coalesce()
 * @see Expression#caseStatement()
 * @author tware
 *
 */
public class ArgumentListFunctionExpression extends FunctionExpression {

    protected Boolean hasLastChild = Boolean.FALSE;

    /**
     * INTERNAL:
     * Add a new Expression to the list of arguments.
     * This method will update the list of arguments and any constant strings that are required
     * to be printed with the arguments
     * @param argument
     */
    @Override
    public synchronized void addChild(Expression argument){
        if (hasLastChild != null && hasLastChild.booleanValue()){
            getChildren().add(getChildren().size() - 1, argument);
        } else {
            super.addChild(argument);
        }
        setBaseExpression(getChildren().firstElement());
        ((ListExpressionOperator)operator).incrementNumberOfItems();
    }

    /**
     * INTERNAL:
     * Add a child and ensure it is the rightmost in the tree as long as it
     * is in the tree
     * If there is already a node that is set as therightmost node, replace it
     * @param argument
     */
    public synchronized void addRightMostChild(Expression argument){
        if (hasLastChild != null && hasLastChild.booleanValue()){
            getChildren().remove(super.getChildren().size() - 1);
            super.addChild(argument);
        } else {
            this.addChild(argument);
        }
        this.hasLastChild = Boolean.TRUE;
    }

    /**
     * INTERNAL:
     * Set the operator for this expression.  The operator must be a ListExpressionOperator
     * This method asserts that the passed argument is a ListExpressionOperator rather than
     * throwing an exception since this method is entirely internal and the user should never get
     * this behavior
     */
    @Override
    public void setOperator(ExpressionOperator theOperator) {
        assert(theOperator instanceof ListExpressionOperator);
        super.setOperator(theOperator);
        ((ListExpressionOperator)theOperator).setNumberOfItems(0);
    }

    /**
     * INTERNAL:
     * Print SQL
     */
    @Override
    public void printSQL(ExpressionSQLPrinter printer) {
        ListExpressionOperator platformOperator = (ListExpressionOperator)getPlatformOperator(printer.getPlatform());
        platformOperator.copyTo(operator);
        ((ListExpressionOperator)operator).setIsComplete(true);
        operator.printCollection(getChildren(), printer);
    }

    @Override
    protected void postCopyIn(Map alreadyDone)
    {
        // The current ArgumentListFunctionExpression just got cloned shallow.
        // We need a new operator: as our ListExpressionOperator instance has a
        // state, "numberOfItems", it must be cloned not to be shared across
        // parallel threads when an unnamed, un-query-hinted, shared, cached
        // query is executed.
        // This is typical for dynamic JPQL.
        final ListExpressionOperator originalOperator = ((ListExpressionOperator) this.operator);
        this.operator = new ListExpressionOperator();
        originalOperator.copyTo(this.operator);

        // New operator implicitly initialized to:
        // ((ListExpressionOperator) operator).setNumberOfItems(0);
        final Boolean hasLastChildCopy = hasLastChild;
        hasLastChild = null;
        super.postCopyIn(alreadyDone);
        hasLastChild = hasLastChildCopy;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initializePlatformOperator(DatabasePlatform platform) {
        super.initializePlatformOperator(platform);
        ((ListExpressionOperator)platformOperator).setNumberOfItems(((ListExpressionOperator)operator).getNumberOfItems());
    }


}

