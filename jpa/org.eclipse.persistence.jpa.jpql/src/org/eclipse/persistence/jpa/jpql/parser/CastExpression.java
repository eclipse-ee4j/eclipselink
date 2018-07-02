/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * The <b>CAST</b> function cast value to a different type. The database type is the second parameter,
 * and can be any valid database type including size and scale.
 *
 * <div><b>BNF:</b> <code>expression ::= CAST(scalar_expression [AS] database_type)</code></div>
 *
 * @see DatabaseType
 *
 * @version 2.5
 * @since 2.4
 * @author James Sutherland
 */
public final class CastExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * The actual <b>AS</b> identifier found in the string representation of the JPQL query.
     */
    private String asIdentifier;

    /**
     * The {@link Expression} representing the database type to cast to.
     */
    private AbstractExpression databaseType;

    /**
     * Determines whether a space was parsed after the identifier <b>AS</b>.
     */
    private boolean hasSpaceAfterAs;

    /**
     * Determines whether a space was parsed after the expression.
     */
    private boolean hasSpaceAfterExpression;

    /**
     * Flag used to determine how to stop parsing based on what is being parsed.
     */
    private boolean parsingDatabaseType;

    /**
     * This flag is used to prevent the parsing from using any {@link ExpressionFactory} before
     * going through the fallback procedure. This is necessary because only a {@link DatabaseType}
     * should be created, even if the database type is a JPQL identifier (eg: <code>TIMESTAMP</code>).
     */
    private boolean shouldParseWithFactoryFirst;

    /**
     * Creates a new <code>CastExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public CastExpression(AbstractExpression parent) {
        super(parent, CAST);
        shouldParseWithFactoryFirst = true;
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ExpressionVisitor visitor) {
        acceptUnknownVisitor(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedEncapsulatedExpressionTo(List<Expression> children) {

        // Value
        super.addOrderedEncapsulatedExpressionTo(children);

        if (hasSpaceAfterExpression) {
            children.add(buildStringExpression(SPACE));
        }

        // 'AS'
        if (asIdentifier != null) {
            children.add(buildStringExpression(AS));
        }

        if (hasSpaceAfterAs) {
            children.add(buildStringExpression(SPACE));
        }

        if (hasDatabaseType()) {
            children.add(databaseType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncapsulatedExpressionQueryBNFId() {
        return ScalarExpressionBNF.ID;
    }

    /**
     * Returns the database type to cast to.
     *
     * @return The {@link Expression} representing the database type
     */
    public Expression getDatabaseType() {
        return databaseType;
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(CastExpressionBNF.ID);
    }

    /**
     * Determines whether the identifier <b>AS</b> was part of the query.
     *
     * @return <code>true</code> if the identifier <b>AS</b> was parsed; <code>false</code> otherwise
     */
    public boolean hasAs() {
        return asIdentifier != null;
    }

    /**
     * Determines whether the database type was parsed or not.
     *
     * @return <code>true</code> if the database type was parsed; <code>false</code> otherwise
     */
    public boolean hasDatabaseType() {
        return databaseType != null &&
              !databaseType.isNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEncapsulatedExpression() {
        return super.hasEncapsulatedExpression() || (asIdentifier != null) || hasDatabaseType();
    }

    /**
     * Determines whether something was parsed after the left parenthesis and before the
     * <code><b>AS</b></code> identifier.
     *
     * @return <code>true</code> the expression to be cast was parsed; <code>false</code> otherwise
     * @since 2.5
     */
    public boolean hasScalarExpression() {
        return super.hasEncapsulatedExpression();
    }

    /**
     * Determines whether a whitespace parsed after <b>AS</b>.
     *
     * @return <code>true</code> if there was a whitespace parsed after <b>AS</b>;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceAfterAs() {
        return hasSpaceAfterAs;
    }

    /**
     * Determines whether a whitespace was parsed after the expression.
     *
     * @return <code>true</code> if there was a whitespace parsed after the expression;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceAfterExpression() {
        return hasSpaceAfterExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        if (parsingDatabaseType) {
            return super.isParsingComplete(wordParser, word, expression);
        }

        ExpressionFactory factory = getQueryBNF(getEncapsulatedExpressionQueryBNFId()).getExpressionFactory(word);

        // The first check is used to stop parsing the scalar expression,
        // example: CAST(e.firstName NUMERIC(2, 3)) and "NUMERIC" is the current word,
        // it cannot be part of the scalar expression but will be the database type
        // TODO: Add support for tolerance and the scalar expression is invalid, like
        //       having 'x AND y', it probably should be parsed in its entirety
        return (factory == null && expression != null) ||
               word.equalsIgnoreCase(AS) ||
               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parseEncapsulatedExpression(WordParser wordParser,
                                               int whitespaceCount,
                                               boolean tolerant) {

        // Parse the value
        super.parseEncapsulatedExpression(wordParser, whitespaceCount, tolerant);

        hasSpaceAfterExpression = wordParser.skipLeadingWhitespace() > 0;

        // Parse 'AS'
        if (wordParser.startsWithIdentifier(AS)) {
            asIdentifier = wordParser.moveForward(AS);
            hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse the database type
        if (!wordParser.isTail()) {
            parsingDatabaseType = true;
            if (tolerant) {
                databaseType = parse(wordParser, DatabaseTypeQueryBNF.ID, tolerant);
            }
            else {
                databaseType = new DatabaseType(this, wordParser.word());
                databaseType.parse(wordParser, tolerant);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void removeEncapsulatedExpression() {
        super.removeEncapsulatedExpression();
        asIdentifier = null;
        databaseType = null;
        hasSpaceAfterAs = false;
        hasSpaceAfterExpression = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldParseWithFactoryFirst() {
        return shouldParseWithFactoryFirst;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

        // Value
        super.toParsedTextEncapsulatedExpression(writer, actual);

        if (hasSpaceAfterExpression) {
            writer.append(SPACE);
        }

        // 'AS'
        if (asIdentifier != null) {
            writer.append(actual ? asIdentifier : AS);
        }

        if (hasSpaceAfterAs) {
            writer.append(SPACE);
        }

        // Database type
        if (databaseType != null) {
            databaseType.toParsedText(writer, actual);
        }
    }
}
