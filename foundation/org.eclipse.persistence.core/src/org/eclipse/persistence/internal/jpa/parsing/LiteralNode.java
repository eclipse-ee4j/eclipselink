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

import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.expressions.*;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Superclass for literals (String, Integer, Float, Character, ...)
 * <p><b>Responsibilities</b>:<ul>
 * <li> Maintain the literal being represented
 * <li> Print to a string
 * <li> Answer if the node is completely built
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class LiteralNode extends Node implements AliasableNode {
    public java.lang.Object literal;

    /**
     * Return a new LiteralNode.
     */
    public LiteralNode() {
        super();
    }

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            reportQuery.addAttribute("CONSTANT", generateExpression(context));
        }
        
    }

    /**
     * INTERNAL
     * Generate the a new EclipseLink ConstantExpression for this node.
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = new ConstantExpression(getLiteral(), context.getBaseExpression());
        return whereClause;
    }

    /**
     * INTERNAL
     * Return the literal
     */
    public String getAsString() {
        return getLiteral().toString();
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/21/00 10:51:48 AM)
     * @return java.lang.Object
     */
    public java.lang.Object getLiteral() {
        return literal;
    }

    /**
     * INTERNAL
     * Is this a literal node
     */
    public boolean isLiteralNode() {
        return true;
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/21/00 10:51:48 AM)
     * @param newLiteral java.lang.Object
     */
    public void setLiteral(java.lang.Object newLiteral) {
        literal = newLiteral;
    }

    public String toString(int indent) {
        StringBuffer buffer = new StringBuffer();
        toStringIndent(indent, buffer);
        buffer.append(toStringDisplayName() + "[" + getLiteral() + "]");
        return buffer.toString();
    }
    
    public boolean isAliasableNode(){
        return true;
    }
}
