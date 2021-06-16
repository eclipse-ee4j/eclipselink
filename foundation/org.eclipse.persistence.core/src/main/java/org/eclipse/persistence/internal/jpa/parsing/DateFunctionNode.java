/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
    @Override
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
    @Override
    public void validate(ParseTreeContext context) {
        setType(type);
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    @Override
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
