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

import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an EXISTS subquery.
 */
public class ExistsNode extends Node {

    /** True in case of a NOT EXISTS (...) query. */
    private boolean notIndicated = false;

    /**
     * Return a new ExistsNode.
     */
    public ExistsNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     * Change subquery SELECT clause.
     */
    public void validate(ParseTreeContext context) {
        if (left != null) {
            
            // change SELECT clause of subquery
            SubqueryNode subqueryNode = (SubqueryNode)getLeft();
            // validate changed subquery
            subqueryNode.validate(context);

            TypeHelper typeHelper = context.getTypeHelper();
            setType(typeHelper.getBooleanType());
        }
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        SubqueryNode subqueryNode = (SubqueryNode)getLeft();
        ReportQuery reportQuery = subqueryNode.getReportQuery(context);
        // Replace the SELECT clause of the exists subquery by SELECT 1 to
        // avoid problems with databases not supporting multiple columns in the
        // subquery SELECT clause in SQL.
        // The original select clause expressions might include relationship
        // navigations which should result in FK joins in the generated SQL,
        // e.g. ... EXISTS (SELECT o.customer FROM Order o ...). Add the
        // select clause expressions as non fetch join attributes to the
        // ReportQuery representing the subquery. This make sure the FK joins
        // get generated.  
        List items = reportQuery.getItems();
        for (Iterator i = items.iterator(); i.hasNext();) {
            ReportItem item = (ReportItem)i.next();
            Expression expr = item.getAttributeExpression();
            reportQuery.addNonFetchJoinedAttribute(expr);
        }
        reportQuery.clearItems();
        Expression one = new ConstantExpression(Integer.valueOf(1), new ExpressionBuilder());
        reportQuery.addItem("one", one);
        reportQuery.dontUseDistinct();
        Expression expr = context.getBaseExpression();
        return notIndicated() ? expr.notExists(reportQuery) : 
            expr.exists(reportQuery);
    }

    /**
     * INTERNAL
     * Indicate if a NOT was found in the WHERE clause.
     * Examples: WHERE ... NOT EXISTS(...)
     */
    public void indicateNot() {
        notIndicated = true;
    }

    public boolean notIndicated() {
        return notIndicated;
    }

}
