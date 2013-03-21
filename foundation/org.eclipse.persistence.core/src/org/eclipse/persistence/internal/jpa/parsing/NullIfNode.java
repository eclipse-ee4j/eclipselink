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
 *     tware - initial implementation as part of JPA 2.0 RI
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an NULLIF in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an NULLIF in EJBQL
 * </ul>
 *    @author tware
 *    @since EclipseLink 1.2
 */
public class NullIfNode extends Node implements AliasableNode {

    public NullIfNode(){
        super();
    }
    
    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext generationContext) {
        if (theQuery instanceof ReportQuery) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            Expression expression = generateExpression(generationContext);
            reportQuery.addItem("NullIf(" + getLeft().getAsString() + "," + getRight().getAsString() + ")", expression);
        }
    }
    
    /**
     * INTERNAL
     * Generate the a new EclipseLink NullIf expression for this node.
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context).nullIf(getRight().generateExpression(context));
        return whereClause;
    }
    
    public void validate(ParseTreeContext context) {
        left.validate(context);
        right.validate(context);
        setType(left.getType());
    }
    
    public boolean isAliasableNode(){
        return true;
    }
}
