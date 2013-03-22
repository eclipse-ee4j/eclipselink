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

import org.eclipse.persistence.expressions.*;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a TRIM
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for TRIM
 * </ul>
 */
public class TrimNode extends StringFunctionNode {

    private Node trimChar;
    private boolean leading;
    private boolean trailing;
    private boolean both;

    /**
     * TrimNode constructor.
     */
    public TrimNode() {
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
            left.validateParameter(context, typeHelper.getStringType());
        }
        if (trimChar != null) {
            trimChar.validate(context);
            trimChar.validateParameter(context, typeHelper.getCharType());
        }
        setType(typeHelper.getStringType());
    }

    /**
     * INTERNAL
     * Generate the TopLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context);
        if (leading) {
            // use leftTrim
            if (trimChar != null) {
                Expression trimCharExpr = trimChar.generateExpression(context);
                whereClause = whereClause.leftTrim(trimCharExpr);
            } else {
                whereClause = whereClause.leftTrim();
            }
        } else if (trailing) {
            if (trimChar != null) {
                Expression trimCharExpr = trimChar.generateExpression(context);
                whereClause = whereClause.rightTrim(trimCharExpr);
            } else {
                whereClause = whereClause.rightTrim();
            }
        } else {
            if (trimChar != null) {
                Expression trimCharExpr = trimChar.generateExpression(context);
                whereClause = whereClause.leftTrim(trimCharExpr).rightTrim(trimCharExpr);
            } else {
                whereClause = whereClause.leftTrim().rightTrim();
            }
        }
        return whereClause;
    }

    /** */
    public void setTrimChar(Node trimChar) {
        this.trimChar = trimChar;
    }

    /** */
    public boolean isLeading() {
        return leading;
    }

    /** */
    public void setLeading(boolean newLeading) {
        this.leading = newLeading;
    }
    
    /** */
    public boolean isTrailing() {
        return trailing;
    }
    
    /** */
    public void setTrailing(boolean newTrailing) {
        this.trailing = newTrailing;
    }
    
    /** */
    public boolean isBoth() {
        return both;
    }
    
    /** */
    public void setBoth(boolean newBoth) {
        this.both = newBoth;
    }
    
}
