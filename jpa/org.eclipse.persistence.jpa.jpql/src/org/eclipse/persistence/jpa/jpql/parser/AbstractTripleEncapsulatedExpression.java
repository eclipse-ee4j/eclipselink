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
 * This {@link Expression} takes care of parsing an expression that encapsulates three expressions
 * separated by a comma.
 *
 * <div><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(first_expression, second_expression, third_expression)</code><p></div>
 *
 * @see LocateExpression
 * @see SubstringExpression
 *
 * @version 2.5.1
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractTripleEncapsulatedExpression extends AbstractEncapsulatedExpression {

    /**
     * The {@link Expression} that represents the first expression.
     */
    private AbstractExpression firstExpression;

    /**
     * Determines whether the comma separating the first and second expression was parsed.
     */
    private boolean hasFirstComma;

    /**
     * Determines whether the comma separating the first and second expression was parsed.
     */
    private boolean hasSecondComma;

    /**
     * Determines whether a whitespace is following the comma.
     */
    private boolean hasSpaceAfterFirstComma;

    /**
     * Determines whether a whitespace is following the comma.
     */
    private boolean hasSpaceAfterSecondComma;

    /**
     * Determines which child expression is been currently parsed.
     */
    protected int parameterIndex;

    /**
     * The {@link Expression} that represents the second expression.
     */
    private AbstractExpression secondExpression;

    /**
     * The {@link Expression} that represents the first expression.
     */
    private AbstractExpression thirdExpression;

    /**
     * Creates a new <code>AbstractTripleEncapsulatedExpression</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier that starts this expression
     */
    protected AbstractTripleEncapsulatedExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        getFirstExpression().accept(visitor);
        getSecondExpression().accept(visitor);
        getThirdExpression().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getFirstExpression());
        children.add(getSecondExpression());
        children.add(getThirdExpression());
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
        if (hasFirstComma) {
            children.add(buildStringExpression(COMMA));
        }

        if (hasSpaceAfterFirstComma) {
            children.add(buildStringExpression(SPACE));
        }

        // Second expression
        if (secondExpression != null) {
            children.add(secondExpression);
        }

        // ','
        if (hasSecondComma) {
            children.add(buildStringExpression(COMMA));
        }

        if (hasSpaceAfterSecondComma) {
            children.add(buildStringExpression(SPACE));
        }

        // Third expression
        if (thirdExpression != null) {
            children.add(thirdExpression);
        }
    }

    /**
     * Creates a new {@link CollectionExpression} that will wrap the first, second and third
     * expressions.
     *
     * @return The first, second and third expressions represented by a temporary collection
     */
    public final CollectionExpression buildCollectionExpression() {

        List<AbstractExpression> children = new ArrayList<AbstractExpression>(3);
        children.add((AbstractExpression) getFirstExpression());
        children.add((AbstractExpression) getSecondExpression());
        children.add((AbstractExpression) getThirdExpression());

        List<Boolean> commas = new ArrayList<Boolean>(3);
        commas.add(hasFirstComma);
        commas.add(hasSecondComma);
        commas.add(Boolean.FALSE);

        List<Boolean> spaces = new ArrayList<Boolean>(3);
        spaces.add(hasSpaceAfterFirstComma);
        spaces.add(hasSpaceAfterSecondComma);
        spaces.add(Boolean.FALSE);

        return new CollectionExpression(this, children, commas, spaces, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((firstExpression != null) && firstExpression.isAncestor(expression)) {
            return getQueryBNF(getParameterQueryBNFId(0));
        }

        if ((secondExpression != null) && secondExpression.isAncestor(expression)) {
            return getQueryBNF(getParameterQueryBNFId(1));
        }

        if ((thirdExpression != null) && thirdExpression.isAncestor(expression)) {
            return getQueryBNF(getParameterQueryBNFId(2));
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
     * Returns the unique identifier of the {@link JPQLQueryBNF} to be used to parse one of the
     * encapsulated expression at the given position.
     *
     * @param index The position of the encapsulated {@link Expression} that needs to be parsed
     * within the parenthesis, which starts at position 0
     * @return The ID of the {@link JPQLQueryBNF} to be used to parse one of the encapsulated expression
     */
    public abstract String getParameterQueryBNFId(int index);

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
     * Returns the {@link Expression} that represents the first expression.
     *
     * @return The expression that was parsed representing the first expression
     */
    public final Expression getThirdExpression() {
        if (thirdExpression == null) {
            thirdExpression = buildNullExpression();
        }
        return thirdExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEncapsulatedExpression() {
        return hasFirstExpression()  || hasFirstComma  ||
               hasSecondExpression() || hasSecondComma ||
               hasThirdExpression();
    }

    /**
     * Determines whether the comma was parsed after the first expression.
     *
     * @return <code>true</code> if a comma was parsed after the first expression;
     * <code>false</code> otherwise
     */
    public final boolean hasFirstComma() {
        return hasFirstComma;
    }

    /**
     * Determines whether the first expression of the query was parsed.
     *
     * @return <code>true</code> if the first expression was parsed; <code>false</code> if it was not
     * parsed
     */
    public final boolean hasFirstExpression() {
        return firstExpression != null &&
              !firstExpression.isNull();
    }

    /**
     * Determines whether the comma was parsed after the second expression.
     *
     * @return <code>true</code> if a comma was parsed after the second expression; <code>false</code>
     * otherwise
     */
    public final boolean hasSecondComma() {
        return hasSecondComma;
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
     * Determines whether a whitespace was parsed after the first comma.
     *
     * @return <code>true</code> if there was a whitespace after the first comma; <code>false</code>
     * otherwise
     */
    public final boolean hasSpaceAfterFirstComma() {
        return hasSpaceAfterFirstComma;
    }

    /**
     * Determines whether a whitespace was parsed after the second comma.
     *
     * @return <code>true</code> if there was a whitespace after the second comma; <code>false</code>
     * otherwise
     */
    public final boolean hasSpaceAfterSecondComma() {
        return hasSpaceAfterSecondComma;
    }

    /**
     * Determines whether the third expression of the query was parsed.
     *
     * @return <code>true</code> if the third expression was parsed; <code>false</code> if it was not
     * parsed
     */
    public final boolean hasThirdExpression() {
        return thirdExpression != null &&
              !thirdExpression.isNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        char character = wordParser.character();

        // When parsing an invalid JPQL query (eg: LOCATE + ABS(e.name)) then "+ ABS(e.name)"
        // should not be parsed as an invalid first expression
        if ((parameterIndex == 0) &&
            ((character == '+') || (character == '-')) &&
            !hasLeftParenthesis()) {

            parameterIndex = -1;
            return true;
        }

        return super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * Determines whether the third expression is an optional expression, which means a valid query
     * can have it or not.
     *
     * @return <code>true</code> if the third expression can either be present or not in a valid
     * query; <code>false</code> if it's mandatory
     */
    protected abstract boolean isThirdExpressionOptional();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parseEncapsulatedExpression(WordParser wordParser,
                                               int whitespaceCount,
                                               boolean tolerant) {

        int count = 0;

        // Parse the first expression
        parameterIndex = 0;
        firstExpression = parse(wordParser, getParameterQueryBNFId(0), tolerant);

        if (firstExpression != null) {
            count = wordParser.skipLeadingWhitespace();
        }
        // See comment in isParsingComplete()
        else if (parameterIndex == -1) {
            return;
        }

        // Parse ','
        hasFirstComma = wordParser.startsWith(COMMA);

        if (hasFirstComma) {
            count = 0;
            wordParser.moveForward(1);
            hasSpaceAfterFirstComma = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse the second expression
        parameterIndex = 1;
        secondExpression = parse(wordParser, getParameterQueryBNFId(1), tolerant);

        if (!hasFirstComma) {
            hasSpaceAfterFirstComma = (count > 0);
        }

        count = wordParser.skipLeadingWhitespace();

        // Parse ','
        hasSecondComma = wordParser.startsWith(COMMA);

        if (hasSecondComma) {
            count = 0;
            wordParser.moveForward(1);
            hasSpaceAfterSecondComma = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse the third expression
        parameterIndex = 2;
        thirdExpression = parse(wordParser, getParameterQueryBNFId(2), tolerant);

        if (!hasSecondComma && (!isThirdExpressionOptional() || (thirdExpression != null))) {
            hasSpaceAfterSecondComma = (count > 0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void removeEncapsulatedExpression() {
        hasFirstComma = false;
        hasSecondComma = false;
        firstExpression = null;
        thirdExpression = null;
        secondExpression = null;
        hasSpaceAfterFirstComma = false;
        hasSpaceAfterSecondComma = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

        // First expression
        if (firstExpression != null) {
            firstExpression.toParsedText(writer, actual);
        }

        // ','
        if (hasFirstComma) {
            writer.append(COMMA);
        }

        if (hasSpaceAfterFirstComma) {
            writer.append(SPACE);
        }

        // Second expression
        if (secondExpression != null) {
            secondExpression.toParsedText(writer, actual);
        }

        // ','
        if (hasSecondComma) {
            writer.append(COMMA);
        }

        if (hasSpaceAfterSecondComma) {
            writer.append(SPACE);
        }

        // Third expression
        if (thirdExpression != null) {
            thirdExpression.toParsedText(writer, actual);
        }
    }
}
