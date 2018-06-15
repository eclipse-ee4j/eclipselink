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
 * This expression simply adds a plus or minus sign to the arithmetic primary expression.
 *
 * <div><b>BNF:</b> <code>arithmetic_factor ::= [{+|-}] arithmetic_primary</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class ArithmeticFactor extends AbstractExpression {

    /**
     * The {@link Expression} representing the arithmetic primary.
     */
    private AbstractExpression expression;

    /**
     * Determines whether there is a whitespace after the arithmetic operator.
     */
    private boolean hasSpaceAfterArithmeticOperator;

    /**
     * The arithmetic operator, either '+' or '-'.
     */
    private char operator;

    /**
     * Creates a new <code>ArithmeticFactor</code>.
     *
     * @param parent The parent of this expression
     * @param arithmeticFactor The arithmetic factor, which is either '+' or '-'
     */
    public ArithmeticFactor(AbstractExpression parent, String arithmeticFactor) {
        super(parent, arithmeticFactor);
        operator = arithmeticFactor.charAt(0);
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
        getExpression().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getExpression());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {

        children.add(buildStringExpression(operator));

        if (hasSpaceAfterArithmeticOperator) {
            children.add(buildStringExpression(SPACE));
        }

        if (expression != null) {
            children.add(expression);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((this.expression != null) && this.expression.isAncestor(expression)) {
            return getQueryBNF(ArithmeticPrimaryBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the {@link Expression} representing the arithmetic primary.
     *
     * @return The expression representing the arithmetic primary
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
        return getQueryBNF(ArithmeticFactorBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleAggregate(JPQLQueryBNF queryBNF) {
        return false;
    }

    /**
     * Determines whether the arithmetic primary was parsed.
     *
     * @return <code>true</code> the arithmetic primary was parsed; <code>false</code> if nothing was parsed
     */
    public boolean hasExpression() {
        return expression != null &&
              !expression.isNull();
    }

    /**
     * Determines whether a whitespace was parsed after the arithmetic operator.
     *
     * @return <code>true</code> if there was a whitespace after the arithmetic operator;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceAfterArithmeticOperator() {
        return hasSpaceAfterArithmeticOperator;
    }

    /**
     * Determines if the arithmetic primary is prepended with the minus sign.
     *
     * @return <code>true</code> if the expression is prepended with '-'; <code>false</code> otherwise
     */
    public boolean isNegative() {
        return operator == '-';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
        return wordParser.isArithmeticSymbol(wordParser.character()) ||
               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * Determines if the arithmetic primary is prepended with the plus sign.
     *
     * @return <code>true</code> if the expression is prepended with '+'; <code>false</code> otherwise
     */
    public boolean isPositive() {
        return operator == '+';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // Remove the arithmetic operator
        wordParser.moveForward(1);

        hasSpaceAfterArithmeticOperator = (wordParser.skipLeadingWhitespace() > 0);

        // Parse the expression
        expression = parse(wordParser, ArithmeticFactorBNF.ID, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        writer.append(operator);

        if (hasSpaceAfterArithmeticOperator) {
            writer.append(SPACE);
        }

        if (expression != null) {
            expression.toParsedText(writer, actual);
        }
    }
}
