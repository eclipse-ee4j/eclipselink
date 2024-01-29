/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The RIGHT function return the rightmost substring, respectively,
 * of the first argument whose length is given by the second
 * argument. <b>RIGHT</b> function returns a string.
 * <br>
 * Jakarta Persistence 3.2:
 * <div><b>BNF</b> ::= <code>RIGHT(string_expression, simple_arithmetic_expression)</code></div>
 *
 * <div>Example: <b>UPDATE</b> Employee e <b>SET</b> e.firstName = <b>RIGHT</b>('TopLink Workbench', 9)</div>
 *
 * @since 4.1
 * @author Radek Felcman
 */
public final class RightExpression extends AbstractDoubleEncapsulatedExpression {

    /**
     * Creates a new <code>RightExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public RightExpression(AbstractExpression parent) {
        super(parent, RIGHT);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String parameterExpressionBNF(int index) {
        return switch (index) {
            case 0 -> InternalRightStringExpressionBNF.ID;
            default -> InternalRightPositionExpressionBNF.ID;
        };
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningStringsBNF.ID);
    }

}
