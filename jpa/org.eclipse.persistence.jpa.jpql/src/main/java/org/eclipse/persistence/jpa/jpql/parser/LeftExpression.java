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
 * The LEFT function return the leftmost substring, respectively,
 * of the first argument whose length is given by the second
 * argument. <b>LEFT</b> function returns a string.
 * <br>
 * Jakarta Persistence 3.2:
 * <div><b>BNF</b> ::= <code>LEFT(string_expression, simple_arithmetic_expression)</code></div>
 *
 * <div>Example: <b>UPDATE</b> Employee e <b>SET</b> e.firstName = <b>LEFT</b>('TopLink Workbench', 7)</div>
 *
 * @since 4.1
 * @author Radek Felcman
 */
public final class LeftExpression extends AbstractDoubleEncapsulatedExpression {

    /**
     * Creates a new <code>LeftExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public LeftExpression(AbstractExpression parent) {
        //At this moment it's clear, that LEFT() string function is found.
        super(parent, LEFT);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String parameterExpressionBNF(int index) {
        switch (index) {
            case 0:  return InternalLeftStringExpressionBNF.ID;
            default: return InternalLeftPositionExpressionBNF.ID;
        }
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningStringsBNF.ID);
    }

}
