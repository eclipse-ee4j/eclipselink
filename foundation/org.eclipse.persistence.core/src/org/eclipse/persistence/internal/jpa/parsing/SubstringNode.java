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
 * <p><b>Purpose</b>: Represent a SUBSTRING
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for SUBSTRING
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class SubstringNode extends StringFunctionNode {
    private Node startPosition = null;
    private Node stringLength = null;

    /**
     * SubstringNode constructor comment.
     */
    public SubstringNode() {
        super();
    }

    /** 
     * INTERNAL 
     * Check the child nodes for an unqualified field access and if so,
     * replace them by a qualified field access.     
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
       if (left != null) {
           left = left.qualifyAttributeAccess(context);
       }
       if (startPosition != null) {
           startPosition = startPosition.qualifyAttributeAccess(context);
       }
       if (stringLength != null) {
           stringLength = stringLength.qualifyAttributeAccess(context);
       }
       return this;
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
        if (startPosition != null) {
            startPosition.validate(context);
            startPosition.validateParameter(context, typeHelper.getIntType());
        }
        if (stringLength != null) {
            stringLength.validate(context);
            stringLength.validateParameter(context, typeHelper.getIntType());
        }
        setType(typeHelper.getStringType());
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context);
        Expression startPosition = getStartPosition().generateExpression(context);
        if (getStringLength() != null){
            Expression stringLength = getStringLength().generateExpression(context);
            whereClause = whereClause.substring(startPosition, stringLength);
        } else {
            whereClause = whereClause.substring(startPosition);
        }
        return whereClause;
    }

    /**
     * Return the start position object
     */
    private Node getStartPosition() {
        return startPosition;
    }

    /**
     * Return the string length object
     */
    private Node getStringLength() {
        return stringLength;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/19/01 3:41:55 PM)
     * @param newStartPosition java.lang.Integer
     */
    public void setStartPosition(Node newStartPosition) {
        startPosition = newStartPosition;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/19/01 3:42:26 PM)
     * @param newStringLength java.lang.Integer
     */
    public void setStringLength(Node newStringLength) {
        stringLength = newStringLength;
    }
}
