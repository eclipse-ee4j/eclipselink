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

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an AND in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an AND in EJBQL
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class AndNode extends LogicalOperatorNode {

    /**
     * AndNode constructor comment.
     */
    public AndNode() {
        super();
    }

    /**
     * INTERNAL
     * Return a EclipseLink expression by 'AND'ing the expressions from the left and right nodes
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        // Get the left expression
        Expression whereClause = getLeft().generateExpression(context);

        // Or it with whatever the right expression is
        whereClause = whereClause.and(getRight().generateExpression(context));

        // and return the expression...
        return whereClause;
    }
}
