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

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a long literal in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for a long literal
 * </ul>
 */
public class LongLiteralNode extends LiteralNode {

    /**
     * IntegerLiteralNode constructor comment.
     */
    public LongLiteralNode() {
        super();
    }

    /**
     * IntegerLiteralNode constructor comment.
     */
    public LongLiteralNode(Long newLong) {
        super();
        setLiteral(newLong);
    }

    /**
     * INTERNAL
     * Validate the current node and calculate its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getLongType());
    }
}
