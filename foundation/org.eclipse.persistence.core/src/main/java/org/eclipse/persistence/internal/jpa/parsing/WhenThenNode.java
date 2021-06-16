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
//     tware - initial implementation as part of JPA 2.0 RI
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

    @Override
    public void validate(ParseTreeContext context) {
        left.validate(context);
        right.validate(context);
        setType(right.getType());
    }
}
