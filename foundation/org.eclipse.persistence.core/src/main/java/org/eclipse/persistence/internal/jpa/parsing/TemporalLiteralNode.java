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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.DateConstantExpression;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a Date, Time or TimeStamp literal
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for the temporal expression
 * </ul>
 */
public class TemporalLiteralNode extends LiteralNode {

    public enum TemporalType {DATE, TIME, TIMESTAMP}

    private TemporalType type;

    /**
     * Return a new StringLiteralNode
     */
    public TemporalLiteralNode(TemporalType type) {
        super();
        this.type = type;
    }

    /**
     * Return a new StringLiteralNode with the internal string set to the
     * passed value
     */
    public TemporalLiteralNode(String theString, TemporalType type) {
        this(type);
        setLiteral(theString);
    }

    /**
     * INTERNAL
     * Generate the a new EclipseLink ConstantExpression for this node.
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        DateConstantExpression whereClause = new DateConstantExpression(literal, context.getBaseExpression());
        return whereClause;
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        if (type == TemporalType.DATE){
            setType(typeHelper.getSQLDateType());
        } else if (type == TemporalType.TIME) {
            setType(typeHelper.getTimeType());
        } else if (type == TemporalType.TIMESTAMP){
            setType(typeHelper.getTimestampType());
        }
    }
}
