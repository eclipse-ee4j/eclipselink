/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Model a AVG
 * <p><b>Responsibilities</b>:<ul>
 * <li> Apply itself to a query correctly
 * </ul>
 */
public class AvgNode extends AggregateNode {

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    @Override
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            reportQuery.addAttribute(resolveAttribute(),
                                     generateExpression(context), Double.class);
        }
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        if (left != null) {
            left.validate(context);
        }
        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getDoubleClassType());
    }

    /**
     * INTERNAL
     */
    @Override
    protected Expression addAggregateExression(Expression expr) {
        return expr.average();
    }

    /**
     * INTERNAL
     * Get the string representation of this node.
     */
    @Override
    public String getAsString() {
        return "AVG(" + left.getAsString() + ")";
    }
}
