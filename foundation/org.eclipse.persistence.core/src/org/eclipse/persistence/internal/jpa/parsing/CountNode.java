/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Model a COUNT
 * <p><b>Responsibilities</b>:<ul>
 * <li> Apply itself to a query correctly
 * </ul>
 *    @author Jon Driscoll
 *    @since TopLink 5.0
 */
public class CountNode extends AggregateNode {

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            String attrName = getLeft().isDotNode() ? resolveAttribute() : "COUNT";
            reportQuery.addAttribute(attrName, generateExpression(context), Long.class);
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
            setType(typeHelper.getLongClassType());
        }
    }

    /**
     * INTERNAL
     */
    protected Expression addAggregateExression(Expression expr) {
        return expr.count();
    }

    /**
     * INTERNAL
     * Is this node a CountNode
     */
    public boolean isCountNode() {
        return true;
    }
    /**
     * INTERNAL
     * Get the string representation of this node.
     */
    public String getAsString() {
        return "COUNT(" + left.getAsString() + ")";
    }
}
