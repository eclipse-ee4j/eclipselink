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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a '+' in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for a '+'
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since July 2003
 */
public class PlusNode extends BinaryOperatorNode implements AliasableNode {
    public PlusNode() {
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
            reportQuery.addAttribute("plus", generateExpression(context), (Class)getType());
        }
    }

    /**
     * INTERNAL
     * Validate node and calculates its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        super.validate(context);
        if ((left != null) && (right != null)) {
            TypeHelper typeHelper = context.getTypeHelper();
            setType(typeHelper.extendedBinaryNumericPromotion(
                    left.getType(), right.getType()));
        }
    }

    /**
     * INTERNAL
     * Generate the expression. The steps are:
     * 1. Generate the expression for the left node
     * 2. Add the .plus to the where clause returned from step 1
     * 3. Generate the expression for the right side and use it as the parameter for the .plus()
     * 4. Return the completed where clause to the caller
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context);
        whereClause = ExpressionMath.add(whereClause, getRight().generateExpression(context));
        return whereClause;
    }

    @Override
    public boolean isPlusNode() {
        return true;
    }

    @Override
    public boolean isAliasableNode(){
        return true;
    }
}
