/*******************************************************************************
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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


/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a float literal in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for the literal
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class FloatLiteralNode extends LiteralNode {

    /**
     * FloatLiteralNode constructor comment.
     */
    public FloatLiteralNode() {
        super();
    }

    public FloatLiteralNode(Object newFloat) {
        super();
        setLiteral(newFloat);
    }

    /**
     * INTERNAL
     * Validate the current node and calculate its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getFloatType());
    }
}
