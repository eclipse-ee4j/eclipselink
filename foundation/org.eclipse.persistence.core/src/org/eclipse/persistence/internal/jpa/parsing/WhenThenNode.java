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


/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an WHEN x THEN y (part of CASE statement) in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an WHEN THEN in EJBQL
 * </ul>
 *    @author tware
 *    @since EclipseLink 1.2
 */
public class WhenThenNode extends Node {

    public WhenThenNode(){
        super();
    }
    
    /**
     * INTERNAL
     * Generate the a new EclipseLink Expression for this WHEN portion of this node.
     */
    public Expression generateExpressionForWhen(GenerationContext context) {
        return getLeft().generateExpression(context);
    }
    
    /**
     * INTERNAL
     * Generate the a new EclipseLink the THEN portion of this node.
     */
    public Expression generateExpressionForThen(GenerationContext context) {
        return getRight().generateExpression(context);
    }
    
    public void validate(ParseTreeContext context) {
        left.validate(context);
        right.validate(context);
        setType(right.getType());
    }
}
