/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * A <b>WHEN</b> predicate is used to calculate a condition and when it's true, its <b>THEN</b> will
 * be executed.
 *
 * <div><b>BNF:</b> <code>when_clause ::= WHEN conditional_expression THEN scalar_expression</code><p></div>
 * or
 * <div><b>BNF:</b> <code>simple_when_clause ::= WHEN scalar_expression THEN scalar_expression</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class WhenClause extends AbstractExpression {

    /**
     * Determines whether a whitespace was parsed after the identifier <b>THEN</b>.
     */
    private boolean hasSpaceAfterThen;

    /**
     * Determines whether a whitespace was parsed after the identifier <b>WHEN</b>.
     */
    private boolean hasSpaceAfterWhen;

    /**
     * Determines whether a whitespace was parsed after the conditional expression.
     */
    private boolean hasSpaceAfterWhenExpression;

    /**
     * The {@link Expression} representing the expression following the identifier <b>THEN</b>.
     */
    private AbstractExpression thenExpression;

    /**
     * The actual <b>THEN</b> identifier found in the string representation of the JPQL query.
     */
    private String thenIdentifier;

    /**
     * The {@link Expression} representing the conditional predicate of the clause.
     */
    private AbstractExpression whenExpression;

    /**
     * The actual <b>WHEN</b> identifier found in the string representation of the JPQL query.
     */
    private String whenIdentifier;

    /**
     * Creates a new <code>WhenClause</code>.
     *
     * @param parent The parent of this expression
     */
    public WhenClause(AbstractExpression parent) {
        super(parent, WHEN);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    public void acceptChildren(ExpressionVisitor visitor) {
        getWhenExpression().accept(visitor);
        getThenExpression().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getWhenExpression());
        children.add(getThenExpression());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {

        // WHEN
        children.add(buildStringExpression(WHEN));

        if (hasSpaceAfterWhen) {
            children.add(buildStringExpression(SPACE));
        }

        // WHEN Expression
        if (whenExpression != null) {
            children.add(whenExpression);
        }

        if (hasSpaceAfterWhenExpression) {
            children.add(buildStringExpression(SPACE));
        }

        // THEN
        if (thenIdentifier != null) {
            children.add(buildStringExpression(THEN));
        }

        if (hasSpaceAfterThen) {
            children.add(buildStringExpression(SPACE));
        }

        // THEN expression
        if (thenExpression != null) {
            children.add(thenExpression);
        }
    }

    /**
     * Creates a new {@link CollectionExpression} that will wrap the single <code><b>WHEN </b></code>
     * expression.
     *
     * @return The single <code><b>WHEN</b></code> expression represented by a temporary collection
     */
    public CollectionExpression buildWhenCollectionExpression() {

        List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
        children.add(getWhenExpression());

        List<Boolean> commas = new ArrayList<Boolean>(1);
        commas.add(Boolean.FALSE);

        List<Boolean> spaces = new ArrayList<Boolean>(1);
        spaces.add(hasSpaceAfterWhenExpression);

        return new CollectionExpression(this, children, commas, spaces, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((whenExpression != null) && whenExpression.isAncestor(expression)) {
            return getQueryBNF(InternalWhenClauseBNF.ID);
        }

        if ((thenExpression != null) && thenExpression.isAncestor(expression)) {
            return getQueryBNF(ScalarExpressionBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the actual <b>THEN</b> found in the string representation of the JPQL query, which
     * has the actual case that was used.
     *
     * @return The <b>THEN</b> identifier that was actually parsed, or an empty string if it was not parsed
     */
    public String getActualThenIdentifier() {
        return (thenIdentifier != null) ? thenIdentifier : ExpressionTools.EMPTY_STRING;
    }

    /**
     * Returns the actual <b>WHEN</b> found in the string representation of the JPQL query, which
     * has the actual case that was used.
     *
     * @return The <b>WHEN</b> identifier that was actually parsed
     */
    public String getActualWhenIdentifier() {
        return whenIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(WhenClauseBNF.ID);
    }

    /**
     * Returns the {@link Expression} representing the expression following the identifier <b>THEN</b>.
     *
     * @return The expression representing the expression executed if the predicate is true
     */
    public AbstractExpression getThenExpression() {
        if (thenExpression == null) {
            thenExpression = buildNullExpression();
        }
        return thenExpression;
    }

    /**
     * Returns the {@link Expression} representing the conditional predicate of the clause.
     *
     * @return The expression following the <b>WHEN</b> identifier
     */
    public AbstractExpression getWhenExpression() {
        if (whenExpression == null) {
            whenExpression = buildNullExpression();
        }
        return whenExpression;
    }

    /**
     * Determines whether a whitespace was parsed after <b>THEN</b>.
     *
     * @return <code>true</code> if there was a whitespace after <b>THEN</b>; <code>false</code> otherwise
     */
    public boolean hasSpaceAfterThen() {
        return hasSpaceAfterThen;
    }

    /**
     * Determines whether a whitespace was parsed after <b>WHEN</b>.
     *
     * @return <code>true</code> if there was a whitespace after <b>WHEN</b>; <code>false</code> otherwise
     */
    public boolean hasSpaceAfterWhen() {
        return hasSpaceAfterWhen;
    }

    /**
     * Determines whether a whitespace was parsed after the conditional expression.
     *
     * @return <code>true</code> if there was a whitespace after the conditional expression;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceAfterWhenExpression() {
        return hasSpaceAfterWhenExpression;
    }

    /**
     * Determines whether the identifier <b>THEN</b> was part of the query.
     *
     * @return <code>true</code> if the identifier <b>THEN</b> was parsed; <code>false</code> otherwise
     */
    public boolean hasThen() {
        return thenIdentifier != null;
    }

    /**
     * Determines whether the <b>THEN</b> expression of the query was parsed.
     *
     * @return <code>true</code> the <b>THEN</b> expression was parsed; <code>false</code> if nothing
     * was parsed
     */
    public boolean hasThenExpression() {
        return thenExpression != null &&
              !thenExpression.isNull();
    }

    /**
     * Determines whether the conditional expression of the query was parsed.
     *
     * @return <code>true</code> the conditional expression was parsed; <code>false</code> if nothing
     * was parsed
     */
    public boolean hasWhenExpression() {
        return whenExpression != null &&
              !whenExpression.isNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
        return word.equalsIgnoreCase(WHEN) ||
               word.equalsIgnoreCase(THEN) ||
               word.equalsIgnoreCase(ELSE) ||
               word.equalsIgnoreCase(END)  ||
               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // Parse 'WHEN'
        whenIdentifier = wordParser.moveForward(WHEN);
        hasSpaceAfterWhen = wordParser.skipLeadingWhitespace() > 0;

        // Parse the expression
        whenExpression = parse(wordParser, InternalWhenClauseBNF.ID, tolerant);
        hasSpaceAfterWhenExpression = wordParser.skipLeadingWhitespace() > 0;

        // Parse 'THEN'
        if (!tolerant || wordParser.startsWithIdentifier(THEN)) {
            thenIdentifier = wordParser.moveForward(THEN);
            hasSpaceAfterThen = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse the then expression
        thenExpression = parse(wordParser, ScalarExpressionBNF.ID, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        // 'WHEN'
        writer.append(actual ? whenIdentifier : WHEN);

        if (hasSpaceAfterWhen) {
            writer.append(SPACE);
        }

        // Expression
        if (whenExpression != null) {
            whenExpression.toParsedText(writer, actual);
        }

        if (hasSpaceAfterWhenExpression) {
            writer.append(SPACE);
        }

        // 'THEN'
        if (thenIdentifier != null) {
            writer.append(actual ? thenIdentifier : THEN);
        }

        if (hasSpaceAfterThen) {
            writer.append(SPACE);
        }

        // Then expression
        if (thenExpression != null) {
            thenExpression.toParsedText(writer, actual);
        }
    }
}
