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
 * Math functions expressions.
 */
public abstract class MathExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * The <code>CEILING</code> function takes a numeric argument and returns an integer.
     * <br>
     * JPA 3.1:
     * <div><b>BNF:</b> <code>expression ::= CEILING(arithmetic_expression)</code></div>
     */
    public static final class Ceiling extends MathExpression {
        /**
         * Creates a new instance of <code>CEILING</code> math function expression.
         *
         * @param parent The parent of this expression
         */
        public Ceiling(AbstractExpression parent) {
            super(parent, CEILING);
        }

        @Override
        public void accept(ExpressionVisitor visitor) {
            visitor.visit(this);
        }
    }

    /**
     * The <code>FLOOR</code> function takes a numeric argument and returns an integer.
     * <br>
     * JPA 3.1:
     * <div><b>BNF:</b> <code>expression ::= FLOOR(arithmetic_expression)</code></div>
     */
    public static final class Floor extends MathExpression {
        /**
         * Creates a new instance of <code>FLOOR</code> math function expression.
         *
         * @param parent The parent of this expression
         */
        public Floor(AbstractExpression parent) {
            super(parent, FLOOR);
        }

        @Override
        public void accept(ExpressionVisitor visitor) {
            visitor.visit(this);
        }
    }

    /**
     * Creates a new instance of math function expression.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier that starts this expression
     */
    protected MathExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    @Override
    public String getEncapsulatedExpressionQueryBNFId() {
        return SimpleArithmeticExpressionBNF.ID;
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(FunctionsReturningNumericsBNF.ID);
    }

}
