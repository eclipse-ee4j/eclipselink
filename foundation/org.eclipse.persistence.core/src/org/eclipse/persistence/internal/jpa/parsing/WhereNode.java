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

import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a WHERE
 * <p><b>Responsibilities</b>:<ul>
 * <li> Apply this where clause to a query
 * </ul>
 *    @author Jon Driscoll
 *    @since TopLink 5.0
 */
public class WhereNode extends MajorNode {

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        if (left != null) {
            left.validate(context);
            TypeHelper typeHelper = context.getTypeHelper();
            setType(typeHelper.getBooleanType());
        }
    }

    /**
     * INTERNAL
     * @param context The generation context
     * @return An EclipseLink expression
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = null;
        if (getLeft() != null) {
            whereClause = getLeft().generateExpression(context);
        }
        return whereClause;
    }

}
