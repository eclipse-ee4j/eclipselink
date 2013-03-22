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
 * <p><b>Purpose</b>: Represent a LOCATE
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for a LOCATE
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class LocateNode extends ArithmeticFunctionNode {
    private Node find = null;
    private Node findIn = null;
    private Node startPosition = null;

    /**
     * Return a new LocateNode.
     */
    public LocateNode() {
        super();
    }

    /** 
     * INTERNAL 
     * Check the child nodes for an unqualified field access and if so,
     * replace them by a qualified field access.     
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
       if (find != null) {
           find = find.qualifyAttributeAccess(context);
       }
       if (findIn != null) {
           findIn = findIn.qualifyAttributeAccess(context);
       }
       if (startPosition != null) {
           startPosition = startPosition.qualifyAttributeAccess(context);
       }
       return this;
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        if (findIn != null) {
            findIn.validate(context);
            findIn.validateParameter(context, typeHelper.getStringType());
        }
        if (find != null) {
            find.validate(context);
            find.validateParameter(context, typeHelper.getStringType());
        }
        if (startPosition != null) {
            startPosition.validate(context);
            startPosition.validateParameter(context, typeHelper.getIntType());
        }
        setType(typeHelper.getIntType());
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getFindIn().generateExpression(context);
        Expression findExpr = getFind().generateExpression(context);
        if (startPosition != null) {
            whereClause = whereClause.locate(findExpr, getStartPosition().generateExpression(context));
        } else {
            whereClause = whereClause.locate(findExpr);
        }
        return whereClause;
    }

    // Accessors
    public Node getFind() {
        return find;
    }

    public Node getFindIn() {
        return findIn;
    }

    public void setFind(Node newFind) {
        find = newFind;
    }

    public void setFindIn(Node newFindIn) {
        findIn = newFindIn;
    }

    public Node getStartPosition() {
        return startPosition;
    }
    
    public void setStartPosition(Node newStartPosition) {
        startPosition = newStartPosition;
    }
    
}
