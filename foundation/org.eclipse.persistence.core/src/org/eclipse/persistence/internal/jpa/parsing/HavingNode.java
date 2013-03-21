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

/**
 * INTERNAL
 * <p><b>Purpose</b>: This node represents a HAVING
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for HAVING
 * </ul>
 */
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.expressions.Expression;

public class HavingNode extends MajorNode {

    private Node having = null;

    /**
     * INTERNAL
     * Validate the current node.
     */
    public void validate(ParseTreeContext context, GroupByNode groupbyNode) {
        if (having != null) {
            having.validate(context);
        }
    }
    
    /**
     * INTERNAL
     * Add the having expression to the passed query
     */
    public void addHavingToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()) {
            Expression havingExpression = getHaving().generateExpression(context);
            ((ReportQuery)theQuery).setHavingExpression(havingExpression);
        }
    }

    /**
     * INTERNAL
     * Return the HAVING expression
     */
    public Node getHaving() {
        return having;
    }

    /**
     * INTERNAL
     * Set the HAVING expression
     */
    public void setHaving(Node having) {
        this.having = having;
    }
}
