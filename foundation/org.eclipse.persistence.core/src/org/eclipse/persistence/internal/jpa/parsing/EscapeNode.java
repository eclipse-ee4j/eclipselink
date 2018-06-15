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

public class EscapeNode extends LogicalOperatorNode {

    public EscapeNode() {
    }

    /**
     * INTERNAL
     * Is this an escape node
     */
    @Override
    public boolean isEscape() {
        return true;// Yes it is
    }

    /**
     * INTERNAL
     * Validate the current node and calculates its type.
     */
    @Override
    public void validate(ParseTreeContext context) {

        TypeHelper typeHelper = context.getTypeHelper();
        if (left != null) {
            left.validate(context);
            left.validateParameter(context, typeHelper.getCharType());
        }

        setType(getType());
    }

    @Override
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = left.generateExpression(context);
        return whereClause;

    }
}
