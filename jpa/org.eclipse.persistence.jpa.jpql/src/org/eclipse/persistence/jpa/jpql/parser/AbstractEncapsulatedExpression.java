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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression handles parsing the identifier followed by an expression encapsulated within
 * parenthesis.
 *
 * <div><b>BNF:</b> <code>expression ::= &lt;identifier&gt;(expression)</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractEncapsulatedExpression extends AbstractExpression {

    /**
     * Flag used to determine if the closing parenthesis is present in the query.
     */
    private boolean hasLeftParenthesis;

    /**
     * Flag used to determine if the opening parenthesis is present in the query.
     */
    private boolean hasRightParenthesis;

    /**
     * Special flag used to separate the identifier with the encapsulated expression when the left
     * parenthesis is missing.
     */
    private boolean hasSpaceAfterIdentifier;

    /**
     * The actual identifier found in the string representation of the JPQL query.
     */
    private String identifier;

    /**
     * Creates a new <code>AbstractEncapsulatedExpression</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The JPQL identifier that starts this expression
     */
    protected AbstractEncapsulatedExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void addOrderedChildrenTo(List<Expression> children) {

        // Identifier
        children.add(buildStringExpression(getText()));

        // '('
        if (hasLeftParenthesis) {
            children.add(buildStringExpression(LEFT_PARENTHESIS));
        }
        else if (hasSpaceAfterIdentifier) {
            children.add(buildStringExpression(SPACE));
        }

        addOrderedEncapsulatedExpressionTo(children);

        // ')'
        if (hasRightParenthesis) {
            children.add(buildStringExpression(RIGHT_PARENTHESIS));
        }
    }

    /**
     * Adds the {@link Expression Expressions} representing the encapsulated {@link
     * Expression}.
     *
     * @param children The list used to store the string representation of the encapsulated {@link
     * Expression}
     */
    protected abstract void addOrderedEncapsulatedExpressionTo(List<Expression> children);

    protected boolean areLogicalIdentifiersSupported() {
        return false;
    }

    /**
     * Returns the actual identifier found in the string representation of the JPQL query, which has
     * the actual case that was used.
     *
     * @return The JPQL identifier that was actually parsed
     */
    public final String getActualIdentifier() {
        return identifier;
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
     * Determines whether something was parsed after the left parenthesis.
     *
     * @return <code>true</code> if something was parsed; <code>false</code> otherwise
     */
    public abstract boolean hasEncapsulatedExpression();

    /**
     * Determines whether the open parenthesis was parsed or not.
     *
     * @return <code>true</code> if the open parenthesis was present in the string version of the
     * query; <code>false</code> otherwise
     */
    public final boolean hasLeftParenthesis() {
        return hasLeftParenthesis;
    }

    /**
     * Determines whether the close parenthesis was parsed or not.
     *
     * @return <code>true</code> if the close parenthesis was present in the string version of the
     * query; <code>false</code> otherwise
     */
    public final boolean hasRightParenthesis() {
        return hasRightParenthesis;
    }

    /**
     * Determines whether a whitespace was parsed after the identifier rather than the left
     * parenthesis. This can happen in incomplete query of this form: <b>ABS 4 + 5)</b>.
     *
     * @return <code>true</code> if a whitespace was parsed after the identifier; <code>false</code>
     * otherwise
     */
    public boolean hasSpaceAfterIdentifier() {
        return hasSpaceAfterIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        if (wordParser.startsWith(RIGHT_PARENTHESIS) ||
            word.equalsIgnoreCase(WHEN)              ||
            word.equalsIgnoreCase(SET)               ||
            word.equalsIgnoreCase(AS)                ||
            super.isParsingComplete(wordParser, word, expression)) {

            return true;
        }

        if (areLogicalIdentifiersSupported()) {
            return false;
        }

        // This check for compound functions, such as AND, OR, <, <=, =, >=, >, BETWEEN
        return word.equalsIgnoreCase(AND)          ||
               word.equalsIgnoreCase(OR)           ||
               word.equalsIgnoreCase(BETWEEN)      ||
               word.equalsIgnoreCase(NOT_BETWEEN)  ||
               wordParser.startsWith(LOWER_THAN)   ||
               wordParser.startsWith(GREATER_THAN) ||
               wordParser.startsWith(EQUAL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // Parse the identifier
        this.identifier = wordParser.moveForward(getText());

        int position = wordParser.position();
        int count = wordParser.skipLeadingWhitespace();
        int whitespaceAfterLeftParenthesis = 0;

        // Parse '('
        hasLeftParenthesis = wordParser.startsWith(LEFT_PARENTHESIS);

        if (hasLeftParenthesis) {
            wordParser.moveForward(1);
            count = wordParser.skipLeadingWhitespace();
            whitespaceAfterLeftParenthesis = count;
        }

        // Parse the expression
        parseEncapsulatedExpression(wordParser, whitespaceAfterLeftParenthesis, tolerant);

        if (hasEncapsulatedExpression()) {
            // When having incomplete query of this form: ABS 4 + 5),
            // a whitespace is required
            hasSpaceAfterIdentifier = !hasLeftParenthesis && (count > 0);
            count = wordParser.skipLeadingWhitespace();
        }

        // Parse ')'
        if (shouldParseRightParenthesis(wordParser, tolerant)) {

            hasRightParenthesis = wordParser.startsWith(RIGHT_PARENTHESIS);

            if (hasRightParenthesis) {
                wordParser.moveForward(1);
            }
            else {
                // Neither '(' and ') were parsed, then it makes no sense
                // the parsed expression is part of this expression
                if (!hasLeftParenthesis && hasEncapsulatedExpression()) {
                    hasSpaceAfterIdentifier = false;
                    removeEncapsulatedExpression();
                    wordParser.setPosition(position);
                }
                else {
                    wordParser.moveBackward(count);
                }
            }
        }
        else if ((count > 0) && !hasEncapsulatedExpression()) {
            wordParser.moveBackward(count);
        }
    }

    /**
     * Parses the encapsulated expression by starting at the current position, which is part of the
     * given {@link WordParser}.
     *
     * @param wordParser The text to parse based on the current position of the cursor
     * @param whitespaceCount The number of whitespace characters that were parsed after '('
     * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
     * try to parse invalid or incomplete queries
     */
    protected abstract void parseEncapsulatedExpression(WordParser wordParser,
                                                        int whitespaceCount,
                                                        boolean tolerant);

    /**
     * Removes the encapsulated {@link Expression} that was parsed, it should not be part of this one.
     * This happens when the parsed information does not have both '(' and ')'.
     */
    protected abstract void removeEncapsulatedExpression();

    /**
     * Determines whether the right parenthesis should be parsed or not by this expression. There is
     * a possible case where this expression should have optional left and right parenthesis. If
     * there is no left parenthesis, then it would most likely mean the right parenthesis does not
     * belong to this expression.
     *
     * @param wordParser The text to parse based on the current position of the cursor
     * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
     * try to parse invalid or incomplete queries
     * @return <code>true</code> if the right parenthesis should be owned by this expression if it
     * is the next character to scan; <code>false</code> otherwise
     */
    protected boolean shouldParseRightParenthesis(WordParser wordParser, boolean tolerant) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void toParsedText(StringBuilder writer, boolean actual) {

        // Identifier
        writer.append(actual ? identifier : getText());

        // '('
        if (hasLeftParenthesis) {
            writer.append(LEFT_PARENTHESIS);
        }
        else if (hasSpaceAfterIdentifier) {
            writer.append(SPACE);
        }

        // Encapsulated expression
        toParsedTextEncapsulatedExpression(writer, actual);

        // ')'
        if (hasRightParenthesis) {
            writer.append(RIGHT_PARENTHESIS);
        }
    }

    /**
     * Generates a string representation of the encapsulated {@link Expression}.
     *
     * @param writer The buffer used to append the encapsulated {@link Expression}'s string
     * representation
     * @param actual Determines whether to include any characters that are considered
     * virtual, i.e. that was parsed when the query is incomplete and is needed for functionality
     * like content assist
     */
    protected abstract void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual);
}
