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
//     tware - initial implemenation as part of JPA 2.0 RI
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an INDEX in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an INDEX in EJBQL
 * </ul>
 *    @author tware
 *    @since EclipseLink 1.2
 */
public class IndexNode extends Node implements AliasableNode {

    public IndexNode(){
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
            reportQuery.addAttribute(left.resolveAttribute() + "Index", expression, Integer.class);
        }
    }

    /**
     * INTERNAL
     * Generate the a new EclipseLink TableEntryExpression for this node.
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        Expression owningExpression = getLeft().generateExpression(context);
        Expression whereClause = owningExpression.index();
        return whereClause;
    }

    @Override
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        left.validate(context);
        if (!left.isVariableNode()){
            throw JPQLException.indexOnlyAllowedOnVariable(context.getQueryInfo(), getLine(), getColumn(), left.getAsString());
        }
        setType(typeHelper.getIntType());
    }

    @Override
    public boolean isAliasableNode(){
        return true;
    }
}
