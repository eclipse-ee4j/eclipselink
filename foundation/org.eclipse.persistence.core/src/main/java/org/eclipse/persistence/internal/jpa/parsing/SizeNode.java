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
    @Override
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
    @Override
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
    @Override
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
