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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Conditional expressions are composed of other conditional expressions, comparison operations,
 * logical operations, path expressions that evaluate to boolean values, boolean literals, and
 * boolean input parameters. Arithmetic expressions can be used in comparison expressions.
 * Arithmetic expressions are composed of other arithmetic expressions, arithmetic operations, path
 * expressions that evaluate to numeric values, numeric literals, and numeric input parameters.
 * Arithmetic operations use numeric promotion. Standard bracketing () for ordering expression
 * evaluation is supported.
 *
 * <div><b>BNF:</b> <code>expression ::= identifier conditional_expression</code><p></div>
 *
 * @see HavingClause
 * @see OnClause
 * @see StartWithClause
 * @see WhereClause
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractConditionalClause extends AbstractExpression {

    /**
     * The expression representing the composition of the conditional expressions.
     */
    private AbstractExpression conditionalExpression;

    /**
     * Determines whether a whitespace was parsed after the identifier.
     */
    private boolean hasSpaceAfterIdentifier;

    /**
     * The actual identifier found in the string representation of the JPQL query.
     */
    private String identifier;

    /**
     * Creates a new <code>AbstractConditionalClause</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The identifier of this conditional clause
     */
    protected AbstractConditionalClause(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        getConditionalExpression().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void addChildrenTo(Collection<Expression> children) {
        children.add(getConditionalExpression());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void addOrderedChildrenTo(List<Expression> children) {

        // Identifier
        children.add(buildStringExpression(getText()));

        // Space
        if (hasSpaceAfterIdentifier) {
            children.add(buildStringExpression(SPACE));
        }

        // Conditional Expression
        if (conditionalExpression != null) {
            children.add(conditionalExpression);
        }
    }

    /**
     * Creates a new {@link CollectionExpression} that will wrap the single select item.
     *
     * @return The single select item represented by a temporary collection
     */
    public CollectionExpression buildCollectionExpression() {

        List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
        children.add((AbstractExpression) getConditionalExpression());

        List<Boolean> commas = new ArrayList<Boolean>(1);
        commas.add(Boolean.FALSE);

        List<Boolean> spaces = new ArrayList<Boolean>(1);
        spaces.add(Boolean.FALSE);

        return new CollectionExpression(this, children, commas, spaces, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((conditionalExpression != null) && conditionalExpression.isAncestor(expression)) {
            return getQueryBNF(ConditionalExpressionBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the actual identifier found in the string representation of the JPQL query, which has
     * the actual case that was used.
     *
     * @return The identifier that was actually parsed
     */
    public final String getActualIdentifier() {
        return identifier;
    }

    /**
     * Returns the expression representing the composition of the conditional expressions.
     *
     * @return The actual conditional expression
     */
    public final Expression getConditionalExpression() {
        if (conditionalExpression == null) {
            conditionalExpression = buildNullExpression();
        }
        return conditionalExpression;
    }

    /**
     * Returns the JPQL identifier of this expression.
     *
     * @return The JPQL identifier
     */
    public final String getIdentifier() {
        return getText();
    }

    /**
     * Determines whether the conditional expression was parsed.
     *
     * @return <code>true</code> if there is a conditional expression; <code>false</code> otherwise
     */
    public final boolean hasConditionalExpression() {
        return conditionalExpression != null &&
              !conditionalExpression.isNull();
    }

    /**
     * Determines whether a whitespace was parsed after the identifier or not.
     *
     * @return <code>true</code> if there was a whitespace after the identifier; <code>false</code>
     * otherwise
     */
    public final boolean hasSpaceAfterIdentifier() {
        return hasSpaceAfterIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
        return wordParser.character() == RIGHT_PARENTHESIS ||
               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void parse(WordParser wordParser, boolean tolerant) {

        // Parse the identifier
        identifier = wordParser.moveForward(getText());

        hasSpaceAfterIdentifier = wordParser.skipLeadingWhitespace() > 0;

        // Parse the conditional expression
        conditionalExpression = parse(wordParser, ConditionalExpressionBNF.ID, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldSkipLiteral(AbstractExpression expression) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void toParsedText(StringBuilder writer, boolean actual) {

        writer.append(actual ? identifier : getText());

        if (hasSpaceAfterIdentifier) {
            writer.append(SPACE);
        }

        if (conditionalExpression != null) {
            conditionalExpression.toParsedText(writer, actual);
        }
    }
}
