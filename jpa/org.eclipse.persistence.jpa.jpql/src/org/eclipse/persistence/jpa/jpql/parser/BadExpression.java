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

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This wraps another {@link Expression} that was correctly parsed by it is located in an invalid
 * location within the JPQL query.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class BadExpression extends AbstractExpression {

    /**
     * The {@link Expression} that was parsed in a location that was not supposed to contain that
     * expression.
     */
    private AbstractExpression expression;

    /**
     * Creates a new <code>BadExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public BadExpression(AbstractExpression parent) {
        super(parent);
    }

    /**
     * Creates a new <code>BadExpression</code>.
     *
     * @param parent The parent of this expression
     * @param expression The {@link Expression} that was parsed in a location that was not supposed
     * to contain that expression
     */
    public BadExpression(AbstractExpression parent, AbstractExpression expression) {
        super(parent);
        this.expression = expression;
        this.expression.setParent(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        // Don't traverse the invalid expression
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        // Don't traverse the invalid expression
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {
        children.add(buildStringExpression(toParsedText()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {
        return getParent().findQueryBNF(expression);
    }

    /**
     * Returns the {@link Expression} that was parsed but grammatically, it is not a valid location.
     *
     * @return The invalid portion of the JPQL query that was parsed
     */
    public Expression getExpression() {
        if (expression == null) {
            expression = buildNullExpression();
        }
        return expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(BadExpressionBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        char character = wordParser.character();

        return character == AbstractExpression.LEFT_PARENTHESIS  ||
               character == AbstractExpression.RIGHT_PARENTHESIS ||
               character == AbstractExpression.COMMA             ||
               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isUnknown() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {
        expression = parse(wordParser, BadExpressionBNF.ID, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {
        if (expression != null) {
            expression.toParsedText(writer, actual);
        }
    }
}
