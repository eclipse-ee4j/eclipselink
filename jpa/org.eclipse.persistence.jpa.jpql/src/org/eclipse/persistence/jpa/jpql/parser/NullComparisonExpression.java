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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * A null comparison tests whether or not the single-valued path expression or input parameter is a
 * <b>NULL</b> value.
 *
 * <div><b>BNF:</b> <code>null_comparison_expression ::= {single_valued_path_expression | input_parameter} IS [NOT] NULL</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class NullComparisonExpression extends AbstractExpression {

    /**
     * The expression tested for being <code>null</code> or not.
     */
    private AbstractExpression expression;

    /**
     * The actual <b>IS</b> identifier found in the string representation of the JPQL query.
     */
    private String isIdentifier;

    /**
     * The actual <b>IS</b> identifier found in the string representation of the JPQL query.
     */
    private String notIdentifier;

    /**
     * The actual <b>NULL</b> identifier found in the string representation of the JPQL query.
     */
    private String nullIdentifier;

    /**
     * Creates a new <code>NullComparisonExpression</code>.
     *
     * @param parent The parent of this expression
     * @param expression The expression before the identifier
     */
    public NullComparisonExpression(AbstractExpression parent,
                                    String identifier,
                                    AbstractExpression expression) {

        super(parent, identifier);
        this.expression = expression;

        if (expression != null) {
            expression.setParent(this);
        }
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

        // Expression
        if (hasExpression()) {
            children.add(expression);
            children.add(buildStringExpression(SPACE));
        }

        // Identifier
        children.add(buildStringExpression(getIdentifier()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if (this.expression == expression) {
            return expression.getQueryBNF();
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the actual <b>IS</b> found in the string representation of the JPQL query, which has
     * the actual case that was used.
     *
     * @return The <b>IS</b> identifier that was actually parsed, or an empty string if it was not
     * parsed
     */
    public String getActualIsIdentifier() {
        return (isIdentifier != null) ? isIdentifier : ExpressionTools.EMPTY_STRING;
    }

    /**
     * Returns the actual <b>Not</b> found in the string representation of the JPQL query, which has
     * the actual case that was used.
     *
     * @return The <b>NOT</b> identifier that was actually parsed, or an empty string if it was not parsed
     */
    public String getActualNotIdentifier() {
        return (notIdentifier != null) ? notIdentifier : ExpressionTools.EMPTY_STRING;
    }

    /**
     * Returns the actual <b>NULL</b> found in the string representation of the JPQL query, which has
     * the actual case that was used.
     *
     * @return The <b>NULL</b> identifier that was actually parsed
     */
    public String getActualNullIdentifier() {
        return nullIdentifier;
    }

    /**
     * Returns the expression being tested for being <code>null</code>.
     *
     * @return Either the parsed expression or the <code>null</code>-expression
     */
    public Expression getExpression() {
        if (expression == null) {
            expression = buildNullExpression();
        }
        return expression;
    }

    /**
     * Returns the identifier for this expression that may include <b>NOT</b> if it was parsed.
     *
     * @return Either <b>IS NULL</b> or <b>IS NOT NULL</b>
     */
    public String getIdentifier() {
        return (notIdentifier != null) ? IS_NOT_NULL : IS_NULL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(NullComparisonExpressionBNF.ID);
    }

    /**
     * Determines whether the expression preceding the identifier was parsed.
     *
     * @return <code>true</code> the expression preceding the identifier was parsed;
     * <code>false</code> otherwise
     */
    public boolean hasExpression() {
        return expression != null &&
              !expression.isNull();
    }

    /**
     * Determines whether <b>NOT</b> is used in the query.
     *
     * @return <code>true</code> if <b>NOT</b> is present in the query; <code>false</code> otherwise
     */
    public boolean hasNot() {
        return notIdentifier != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // 'IS'
        isIdentifier = wordParser.moveForward(IS);

        wordParser.skipLeadingWhitespace();

        // 'NOT'
        if (wordParser.startsWithIdentifier(NOT)) {
            notIdentifier = wordParser.moveForward(NOT);
            wordParser.skipLeadingWhitespace();
            setText(IS_NOT_NULL);
        }
        else {
            setText(IS_NULL);
        }

        // 'NULL'
        nullIdentifier = wordParser.moveForward(NULL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        if (hasExpression()) {
            expression.toParsedText(writer, actual);
            writer.append(SPACE);
        }

        if (actual) {

            writer.append(isIdentifier);
            writer.append(SPACE);

            if (notIdentifier != null) {
                writer.append(notIdentifier);
                writer.append(SPACE);
            }

            writer.append(nullIdentifier);
        }
        else {
            writer.append(getIdentifier());
        }
    }
}
