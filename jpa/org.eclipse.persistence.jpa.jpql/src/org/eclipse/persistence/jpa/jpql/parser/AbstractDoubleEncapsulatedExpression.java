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
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link Expression} takes care of parsing an expression that encapsulates two expressions
 * separated by a comma.
 *
 * <div><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(first_expression, second_expression)</code><p></div>
 *
 * @see ConcatExpression
 * @see ModExpression
 * @see NullIfExpression
 *
 * @version 2.5.1
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractDoubleEncapsulatedExpression extends AbstractEncapsulatedExpression {

    /**
     * The {@link Expression} that represents the first expression.
     */
    private AbstractExpression firstExpression;

    /**
     * Determines whether the comma separating the first and second expression was parsed.
     */
    private boolean hasComma;

    /**
     * Determines whether a whitespace is following the comma.
     */
    private boolean hasSpaceAfterComma;

    /**
     * The {@link Expression} that represents the second expression.
     */
    private AbstractExpression secondExpression;

    /**
     * Creates a new <code>AbstractDoubleEncapsulatedExpression</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier that starts this expression
     */
    protected AbstractDoubleEncapsulatedExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * {@inheritDoc}
     */
    public void acceptChildren(ExpressionVisitor visitor) {
        getFirstExpression().accept(visitor);
        getSecondExpression().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getFirstExpression());
        children.add(getSecondExpression());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedEncapsulatedExpressionTo(List<Expression> children) {

        // Fist expression
        if (firstExpression != null) {
            children.add(firstExpression);
        }

        // ','
        if (hasComma) {
            children.add(buildStringExpression(COMMA));
        }

        if (hasSpaceAfterComma) {
            children.add(buildStringExpression(SPACE));
        }

        // Second expression
        if (secondExpression != null) {
            children.add(secondExpression);
        }
    }

    /**
     * Creates a new {@link CollectionExpression} that will wrap the first and second expressions.
     *
     * @return The first and second expressions wrapped by a temporary collection
     */
    public final CollectionExpression buildCollectionExpression() {

        List<AbstractExpression> children = new ArrayList<AbstractExpression>(3);
        children.add((AbstractExpression) getFirstExpression());
        children.add((AbstractExpression) getSecondExpression());

        List<Boolean> commas = new ArrayList<Boolean>(2);
        commas.add(hasComma);
        commas.add(Boolean.FALSE);

        List<Boolean> spaces = new ArrayList<Boolean>(2);
        spaces.add(hasSpaceAfterComma);
        spaces.add(Boolean.FALSE);

        return new CollectionExpression(this, children, commas, spaces, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((firstExpression != null) && firstExpression.isAncestor(expression)) {
            return getQueryBNF(parameterExpressionBNF(0));
        }

        if ((secondExpression != null) && secondExpression.isAncestor(expression)) {
            return getQueryBNF(parameterExpressionBNF(1));
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the {@link Expression} that represents the first expression.
     *
     * @return The expression that was parsed representing the first expression
     */
    public final Expression getFirstExpression() {
        if (firstExpression == null) {
            firstExpression = buildNullExpression();
        }
        return firstExpression;
    }

    /**
     * Returns the {@link Expression} that represents the second expression.
     *
     * @return The expression that was parsed representing the second expression
     */
    public final Expression getSecondExpression() {
        if (secondExpression == null) {
            secondExpression = buildNullExpression();
        }
        return secondExpression;
    }

    /**
     * Determines whether the comma was parsed after the first expression.
     *
     * @return <code>true</code> if a comma was parsed after the first expression; <code>false</code>
     * otherwise
     */
    public final boolean hasComma() {
        return hasComma;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEncapsulatedExpression() {
        return hasFirstExpression() || hasComma || hasSecondExpression();
    }

    /**
     * Determines whether the first expression of the query was parsed.
     *
     * @return <code>true</code> if the first expression was parsed; <code>false</code> if it was
     * not parsed
     */
    public final boolean hasFirstExpression() {
        return firstExpression != null &&
              !firstExpression.isNull();
    }

    /**
     * Determines whether the second expression of the query was parsed.
     *
     * @return <code>true</code> if the second expression was parsed; <code>false</code> if it was
     * not parsed
     */
    public final boolean hasSecondExpression() {
        return secondExpression != null &&
              !secondExpression.isNull();
    }

    /**
     * Determines whether a whitespace was parsed after the comma.
     *
     * @return <code>true</code> if there was a whitespace after the comma; <code>false</code>
     * otherwise
     */
    public final boolean hasSpaceAfterComma() {
        return hasSpaceAfterComma;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
        char character = wordParser.character();

        return character == COMMA             ||
               character == RIGHT_PARENTHESIS ||
               word.equalsIgnoreCase(AND)     ||
               word.equalsIgnoreCase(OR)      ||
               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * Determines whether the second expression is optional or not.
     *
     * @return <code>true</code> if the second expression is optional; <code>false</code> if it is
     * required
     */
    protected boolean isSecondExpressionOptional() {
        return false;
    }

    /**
     * Returns the BNF to be used to parse one of the encapsulated expression.
     *
     * @param index The position of the encapsulated {@link Expression} that needs to be parsed
     * within the parenthesis, which starts at position 0
     * @return The BNF to be used to parse one of the encapsulated expression
     */
    public abstract String parameterExpressionBNF(int index);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parseEncapsulatedExpression(WordParser wordParser,
                                               int whitespaceCount,
                                               boolean tolerant) {

        int count = 0;

        // Parse the first expression
        firstExpression = parse(wordParser, parameterExpressionBNF(0), tolerant);

        if (firstExpression != null) {
            count = wordParser.skipLeadingWhitespace();
        }

        // Parse ','
        hasComma = wordParser.startsWith(COMMA);

        if (hasComma) {
            count = 0;
            wordParser.moveForward(1);
            hasSpaceAfterComma = wordParser.skipLeadingWhitespace() > 0;
        }
        else if (hasFirstExpression()) {
            hasSpaceAfterComma = (count > 0);
        }

        // Parse the second expression
        secondExpression = parse(wordParser, parameterExpressionBNF(1), tolerant);

        if (secondExpression == null) {
            if (!hasComma && isSecondExpressionOptional()) {
                hasSpaceAfterComma = false;
            }
            wordParser.moveBackward(count);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void removeEncapsulatedExpression() {
        hasComma = false;
        firstExpression = null;
        secondExpression = null;
        hasSpaceAfterComma = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

        // First expression
        if (firstExpression != null) {
            firstExpression.toParsedText(writer, actual);
        }

        // ','
        if (hasComma) {
            writer.append(COMMA);
        }

        if (hasSpaceAfterComma) {
            writer.append(SPACE);
        }

        // Second expression
        if (secondExpression != null) {
            secondExpression.toParsedText(writer, actual);
        }
    }
}
