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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.expressions.*;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a date function: CURRENT_DATE, CURRENT_TIME,
 * CURRENT_TIMESTAMP.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for the date function
 * </ul>
 */
public class DateFunctionNode extends FunctionalExpressionNode {

    private Class type;

    /**
     * DateFunctionNode constructor.
     */
    public DateFunctionNode() {
        super();
    }

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()){
            ReportQuery reportQuery = (ReportQuery)theQuery;
            reportQuery.addAttribute("date", generateExpression(context), type);
        }
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        setType(type);
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        Expression expr = context.getBaseExpression();
        if (expr == null) {
            expr = new ExpressionBuilder();
        }
        Expression result = null;
        if (type == Date.class) {
            result = expr.currentDateDate();
        } else if (type == Time.class) {
            result = expr.currentTime();
        } else if (type == Timestamp.class) {
            result = expr.currentDate();
        }
        return result;
    }

    /** */
    public void useCurrentDate() {
        type = Date.class;
    }

    /** */
    public void useCurrentTime() {
        type = Time.class;
    }

    /** */
    public void useCurrentTimestamp() {
        type = Timestamp.class;
    }
    
}
