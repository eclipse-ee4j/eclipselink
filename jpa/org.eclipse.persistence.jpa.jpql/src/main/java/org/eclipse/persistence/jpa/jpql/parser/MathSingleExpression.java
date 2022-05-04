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
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * Math single argument functions expressions.
 */
public abstract class MathSingleExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * The <code>CEILING</code> function takes a numeric argument and return a number (integer,
     * float, or double) of the same type as the argument.
     * <br>
     * Jakarta Persistence 3.1:
     * <div><b>BNF:</b> <code>expression ::= CEILING(arithmetic_expression)</code></div>
     */
    public static final class Ceiling extends MathSingleExpression {
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
     * The <code>EXP</code> function takes a numeric argument and returns a double.
     * <br>
     * Jakarta Persistence 3.1:
     * <div><b>BNF:</b> <code>expression ::= EXP(arithmetic_expression)</code></div>
     */
    public static final class Exp extends MathSingleExpression {
        /**
         * Creates a new instance of <code>EXP</code> math function expression.
         *
         * @param parent The parent of this expression
         */
        public Exp(AbstractExpression parent) {
            super(parent, EXP);
        }

        @Override
        public void accept(ExpressionVisitor visitor) {
            visitor.visit(this);
        }
    }

    /**
     * The <code>FLOOR</code> function takes a numeric argument and returns a number (integer,
     * float, or double) of the same type as the argument.
     * <br>
     * Jakarta Persistence 3.1:
     * <div><b>BNF:</b> <code>expression ::= FLOOR(arithmetic_expression)</code></div>
     */
    public static final class Floor extends MathSingleExpression {
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
     * The <code>LN</code> function takes a numeric argument and returns a double.
     * <br>
     * Jakarta Persistence 3.1:
     * <div><b>BNF:</b> <code>expression ::= LN(arithmetic_expression)</code></div>
     */
    public static final class Ln extends MathSingleExpression {
        /**
         * Creates a new instance of <code>LN</code> math function expression.
         *
         * @param parent The parent of this expression
         */
        public Ln(AbstractExpression parent) {
            super(parent, LN);
        }

        @Override
        public void accept(ExpressionVisitor visitor) {
            visitor.visit(this);
        }
    }


    /**
     * The <code>SIGN</code> function takes a numeric argument and returns an integer.
     * <br>
     * Jakarta Persistence 3.1:
     * <div><b>BNF:</b> <code>expression ::= SIGN(arithmetic_expression)</code></div>
     */
    public static final class Sign extends MathSingleExpression {
        /**
         * Creates a new instance of <code>SIGN</code> math function expression.
         *
         * @param parent The parent of this expression
         */
        public Sign(AbstractExpression parent) {
            super(parent, SIGN);
        }

        @Override
        public void accept(ExpressionVisitor visitor) {
            visitor.visit(this);
        }
    }

    /**
     * Creates a new instance of math single argument function expression.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier that starts this expression
     */
    protected MathSingleExpression(AbstractExpression parent, String identifier) {
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
