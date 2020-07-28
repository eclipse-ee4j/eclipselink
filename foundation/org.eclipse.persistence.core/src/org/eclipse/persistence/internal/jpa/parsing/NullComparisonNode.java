/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.*;

/**
 * INTERNAL
 * <p><b>Purpose</b>: This node represents the IS NULL comparisons
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for IS NULL
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class NullComparisonNode extends SimpleConditionalExpressionNode {

    /**
     * Return a new NullComparisonNode.
     */
    public NullComparisonNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        if (left != null) {
            left.validate(context);
            left.validateParameter(context, typeHelper.getObjectType());
        }
        setType(typeHelper.getBooleanType());
    }

    /**
     * INTERNAL
     * Return a EclipseLink expression for this node.
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context);
        whereClause = whereClause.isNull();
        return whereClause;
    }

}
