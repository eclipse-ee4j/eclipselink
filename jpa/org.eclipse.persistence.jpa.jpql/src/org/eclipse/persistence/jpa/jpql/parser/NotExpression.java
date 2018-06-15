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
 * <div><b>BNF:</b> <code>expression ::= NOT conditional_primary</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class NotExpression extends AbstractExpression {

    /**
     * The expression being negated by this expression.
     */
    private AbstractExpression expression;

    /**
     * Determines whether a space was parsed after <b>NOT</b>.
     */
    private boolean hasSpaceAfterNot;

    /**
     * The actual <b></b> identifier found in the string representation of the JPQL query.
     */
    private String identifier;

    /**
     * Creates a new <code>NotExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public NotExpression(AbstractExpression parent) {
        super(parent, NOT);
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

        children.add(buildStringExpression(NOT));

        if (hasSpaceAfterNot) {
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
            return getQueryBNF(ConditionalPrimaryBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the actual <b>NOT</b> found in the string representation of the JPQL query, which has
     * the actual case that was used.
     *
     * @return The <b>NOT</b> identifier that was actually parsed
     */
    public String getActualIdentifier() {
        return identifier;
    }

    /**
     * Returns the {@link Expression} representing the expression that is negated.
     *
     * @return The expression representing the expression that is negated
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
        return getQueryBNF(ConditionalPrimaryBNF.ID);
    }

    /**
     * Determines whether the expression to negate was parsed.
     *
     * @return <code>true</code> if the expression to negate was parsed; <code>false</code> if it was
     * not parsed
     */
    public boolean hasExpression() {
        return expression != null &&
              !expression.isNull();
    }

    /**
     * Determines whether a whitespace was parsed after <b>NOT</b>.
     *
     * @return <code>true</code> if a whitespace was parsed after the identifier <b>NOT</b>;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceAfterNot() {
        return hasSpaceAfterNot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
        return wordParser.startsWithIdentifier(AND) ||
               wordParser.startsWithIdentifier(OR)  ||
               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {
        identifier = wordParser.moveForward(NOT);
        hasSpaceAfterNot = wordParser.skipLeadingWhitespace() > 0;
        expression = parse(wordParser, ConditionalPrimaryBNF.ID, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        // NOT
        writer.append(actual ? identifier : getText());

        if (hasSpaceAfterNot) {
            writer.append(SPACE);
        }

        // Expression
        if (expression != null) {
            expression.toParsedText(writer, actual);
        }
    }
}
