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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a SIZE function
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for SIZE
 * </ul>
 */
public class SizeNode extends ArithmeticFunctionNode {

    /**
     * Return a new SizeNode.
     */
    public SizeNode() {
        super();
    }

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            reportQuery.addAttribute("size", getLeft().generateExpression(context).count(), (Class)getType());
            reportQuery.addGrouping(getLeft().getLeft().generateExpression(context));
        }
    }
    
    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        if (left != null) {
            left.validate(context);
        }
        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getIntType());
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        DotNode dotNode = (DotNode)getLeft();
        Node prefix = dotNode.getLeft();
        String variableName = ((AttributeNode)dotNode.getRight()).getAttributeName();

        // check whether variable denotes a collection valued field
        if (!dotNode.endsWithCollectionField(context)) {
            throw JPQLException.invalidSizeArgument(
                context.getParseTreeContext().getQueryInfo(), 
                getLine(), getColumn(), variableName);
        }
        Expression whereClause = prefix.generateExpression(context);
        whereClause = whereClause.size(variableName);
        return whereClause;
    }

}
