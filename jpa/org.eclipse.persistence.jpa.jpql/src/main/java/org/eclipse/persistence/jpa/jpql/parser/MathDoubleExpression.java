/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for JPA 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * Math double argument functions expressions.
 */
public abstract class MathDoubleExpression extends AbstractDoubleEncapsulatedExpression {

    /**
     * The <code>POWER</code> function takes two numeric arguments and returns a double.
     * <br>
     * JPA 3.1:
     * <div><b>BNF:</b> <code>expression ::= POWER(arithmetic_expression)</code></div>
     */
    public static final class Power extends MathDoubleExpression {
        /**
         * Creates a new instance of <code>POWER</code> math function expression.
         *
         * @param parent The parent of this expression
         */
        public Power(AbstractExpression parent) {
            super(parent, POWER);
        }

        @Override
        public void accept(ExpressionVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public String parameterExpressionBNF(int index) {
            return InternalPowerExpressionBNF.ID;
        }
    }

    /**
     * The <code>ROUND</code> function takes numeric argument and an integer argument and returns
     * a number of the same type as the first argument.
     * <br>
     * JPA 3.1:
     * <div><b>BNF:</b> <code>expression ::= ROUND(arithmetic_expression)</code></div>
     */
    public static final class Round extends MathDoubleExpression {
        /**
         * Creates a new instance of <code>ROUND</code> math function expression.
         *
         * @param parent The parent of this expression
         */
        public Round(AbstractExpression parent) {
            super(parent, ROUND);
        }

        @Override
        public void accept(ExpressionVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public String parameterExpressionBNF(int index) {
            return InternalRoundExpressionBNF.ID;
        }
    }

    /**
     * Creates a new instance of math double argument function expression.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier that starts this expression
     */
    protected MathDoubleExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningNumericsBNF.ID);
    }

}
