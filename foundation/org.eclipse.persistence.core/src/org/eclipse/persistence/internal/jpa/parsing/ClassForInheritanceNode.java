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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

public class ClassForInheritanceNode extends Node implements AliasableNode {

    public ClassForInheritanceNode(){
        super();
    }

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    @Override
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext generationContext) {
        if (theQuery instanceof ReportQuery) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            Expression expression = generateExpression(generationContext);
            reportQuery.addAttribute(left.resolveAttribute() + " Type", expression, Class.class);
        }
    }

    /**
     * INTERNAL
     * Generate the a new EclipseLink TableEntryExpression for this node.
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        Expression owningExpression = getLeft().generateExpression(context);

        return owningExpression.type();
    }

    @Override
    public void validate(ParseTreeContext context) {
        left.validate(context);
        setType(Class.class);
    }

    @Override
    public boolean isAliasableNode(){
        return true;
    }
}
