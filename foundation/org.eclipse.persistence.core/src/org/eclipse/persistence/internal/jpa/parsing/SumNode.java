/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Model a SUM
 * <p><b>Responsibilities</b>:<ul>
 * <li> Apply itself to a query correctly
 * </ul>
 */
public class SumNode extends AggregateNode {

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            reportQuery.addAttribute(resolveAttribute(),
                                     generateExpression(context),
                                     calculateReturnType(context));

        }
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        if (left != null) {
            left.validate(context);
            TypeHelper typeHelper = context.getTypeHelper();
            setType(calculateReturnType(left.getType(), typeHelper));
        }
    }

    /**
     * INTERNAL
     */
    protected Expression addAggregateExression(Expression expr) {
        return expr.sum();
    }

    /**
     * INTERNAL
     * This method calculates the return type of the SUM operation.
     */
    protected Class calculateReturnType(GenerationContext context) {
        Class returnType = null;
        if (getLeft().isDotNode()){
            DotNode arg = (DotNode)getLeft();
            Class fieldType = arg.getTypeOfDirectToField(context);
            TypeHelper helper = context.getParseTreeContext().getTypeHelper();
            returnType = (Class)calculateReturnType(fieldType, helper);
        }
        return returnType;
    }

    /**
     * INTERNAL
     * Helper method to calculate the return type of the SUM operation.
     */
    protected Object calculateReturnType(Object argType, TypeHelper helper) {
        Object returnType = null;
        if (helper.isIntegralType(argType)) {
            returnType = helper.getLongClassType();
        } else if (helper.isFloatingPointType(argType)) {
            returnType = helper.getDoubleClassType();
        } else if (helper.isBigIntegerType(argType)) {
            returnType = helper.getBigIntegerType();
        } else if (helper.isBigDecimalType(argType)) {
            returnType = helper.getBigDecimalType();
        }
        return returnType;
    }

    /**
     * INTERNAL
     * Get the string representation of this node.
     */
    public String getAsString() {
        return "SUM(" + left.getAsString() + ")";
    }
}
