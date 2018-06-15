/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link ResultVariableFactory} creates a new {@link ResultVariable} when the portion of the
 * query to parse starts with or without <b>AS</b>.
 *
 * @see ResultVariable
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ResultVariableFactory extends ExpressionFactory {

    /**
     * Caches the visitor that determines if the parent {@link Expression} is the top-level
     * <code><b>SELECT</b></code> clause.
     */
    private SelectClauseVisitor selectClauseVisitor;

    /**
     * The unique identifier of this {@link ResultVariableFactory}.
     */
    public static final String ID = "result_variable";

    /**
     * Creates a new <code>ResultVariableFactory</code>.
     */
    public ResultVariableFactory() {
        super(ID, Expression.AS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        SelectClauseVisitor visitor = selectClauseVisitor();

        try {
            if (expression != null) {
                parent.accept(visitor);
                if (!visitor.supported) {
                    expression = null;
                }
            }
        }
        finally {
            visitor.supported = false;
        }

        // There was already an expression parsed, we'll assume it's a valid
        // expression and it will have an identification variable, and the identifier is optional
        // Example: "SELECT e.salary / 1000D n" or "SELECT e.salary / 1000D AS n"
        if (((expression != null) || word.equalsIgnoreCase(Expression.AS)) &&
            (word.indexOf(AbstractExpression.DOT) == -1)) {

            ResultVariable resultVariable = new ResultVariable(parent, expression);
            resultVariable.parse(wordParser, tolerant);
            return resultVariable;
        }

        // Use the default factory
        ExpressionFactory factory = getExpressionRegistry().getExpressionFactory(LiteralExpressionFactory.ID);
        return factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
    }

    private SelectClauseVisitor selectClauseVisitor() {
        if (selectClauseVisitor == null) {
            selectClauseVisitor = new SelectClauseVisitor();
        }
        return selectClauseVisitor;
    }

    /**
     * This visitor determines whether the result variable should set the parsed expression as the
     * select expression or not. The allowed locations are:
     * <ul>
     * <li>Root: To support parsing a JPQL fragment.</li>
     * <li>Top-level <code><b>SELECT</b></code> clause: default valid location.</li>
     * <li>Subquery <code><b>SELECT</b></code> clause: to support from within the
     * <code><b>FROM</b></code> clause.</li>
     * </ul>
     */
    private static class SelectClauseVisitor extends AbstractExpressionVisitor {

        /**
         * Indicates if the result variable should have already parsed expression as its select expression.
         */
        boolean supported;

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(JPQLExpression expression) {
            this.supported = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SelectClause expression) {
            this.supported = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectClause expression) {
            this.supported = true;
        }
    }
}
