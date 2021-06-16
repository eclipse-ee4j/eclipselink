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
