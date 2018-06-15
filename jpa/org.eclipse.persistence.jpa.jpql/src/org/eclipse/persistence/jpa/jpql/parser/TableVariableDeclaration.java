/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * Defines a table expression. This allow a non-mapped table to be used in a query. This is not part
 * of the JPA functional specification but is EclipseLink specific support.
 *
 * <div><b>BNF:</b> <code>table_variable_declaration ::= table_expression [AS] identification_variable</code></div>
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public final class TableVariableDeclaration extends AbstractExpression {

    /**
     * The actual <b>AS</b> identifier found in the string representation of the JPQL query.
     */
    private String asIdentifier;

    /**
     * Determines whether a whitespace was parsed after <b>AS</b>.
     */
    private boolean hasSpaceAfterAs;

    /**
     * Determines whether a whitespace was parsed after the "root" object.
     */
    private boolean hasSpaceAfterTableExpression;

    /**
     * The {@link Expression} representing the identification variable.
     */
    private AbstractExpression identificationVariable;

    /**
     * The {@link Expression} representing the database table.
     */
    private TableExpression tableExpression;

    /**
     * Creates a new <code>TableVariableDeclaration</code>.
     *
     * @param parent The parent of this expression
     */
    public TableVariableDeclaration(AbstractExpression parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        acceptUnknownVisitor(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        getTableExpression().accept(visitor);
        getIdentificationVariable().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getTableExpression());
        children.add(getIdentificationVariable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {

        // Table expression
        children.add(tableExpression);

        // Space
        if (hasSpaceAfterTableExpression) {
            children.add(buildStringExpression(SPACE));
        }

        // 'AS'
        if (asIdentifier != null) {
            children.add(buildStringExpression(AS));
        }

        if (hasSpaceAfterAs) {
            children.add(buildStringExpression(SPACE));
        }

        // Identification variable
        if (identificationVariable != null) {
            children.add(identificationVariable);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((tableExpression != null) && tableExpression.isAncestor(expression)) {
            return getQueryBNF(TableExpressionBNF.ID);
        }

        if ((identificationVariable != null) && identificationVariable.isAncestor(expression)) {
            return getQueryBNF(IdentificationVariableBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the actual <b>AS</b> found in the string representation of the JPQL query, which has
     * the actual case that was used.
     *
     * @return The <b>AS</b> identifier that was actually parsed, or an empty string if it was not parsed
     */
    public String getActualAsIdentifier() {
        return (asIdentifier != null) ? asIdentifier : ExpressionTools.EMPTY_STRING;
    }

    /**
     * Returns the {@link Expression} that represents the identification variable.
     *
     * @return The expression that was parsed representing the identification variable
     */
    public Expression getIdentificationVariable() {
        if (identificationVariable == null) {
            identificationVariable = buildNullExpression();
        }
        return identificationVariable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(TableVariableDeclarationBNF.ID);
    }

    /**
     * Returns the {@link TableExpression} that specify the database table.
     *
     * @return The expression that was parsed specifying the database table
     */
    public TableExpression getTableExpression() {
        return tableExpression;
    }

    /**
     * Determines whether the identifier <b>AS</b> was parsed.
     *
     * @return <code>true</code> if the identifier <b>AS</b> was parsed; <code>false</code> otherwise
     */
    public boolean hasAs() {
        return asIdentifier != null;
    }

    /**
     * Determines whether the identification variable was parsed.
     *
     * @return <code>true</code> if the identification variable was parsed; <code>false</code> otherwise
     */
    public boolean hasIdentificationVariable() {
        return identificationVariable != null  &&
              !identificationVariable.isNull() &&
              !identificationVariable.isVirtual();
    }

    /**
     * Determines whether a whitespace was parsed after <b>AS</b>.
     *
     * @return <code>true</code> if there was a whitespace after <b>AS</b>; <code>false</code> otherwise
     */
    public boolean hasSpaceAfterAs() {
        return hasSpaceAfterAs;
    }

    /**
     * Determines whether a whitespace was parsed after the table expression.
     *
     * @return <code>true</code> if there was a whitespace after the table expression;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceAfterTableExpression() {
        return hasSpaceAfterTableExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
        return word.equalsIgnoreCase(AS) ||
               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // Parse the table expression
        tableExpression = new TableExpression(this);
        tableExpression.parse(wordParser, tolerant);

        hasSpaceAfterTableExpression = wordParser.skipLeadingWhitespace() > 0;

        // Parse 'AS'
        if (wordParser.startsWithIdentifier(AS)) {
            asIdentifier = wordParser.moveForward(AS);
            hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse the identification variable
        if (tolerant) {
            identificationVariable = parse(wordParser, IdentificationVariableBNF.ID, tolerant);
        }
        else {
            identificationVariable = new IdentificationVariable(this, wordParser.word());
            identificationVariable.parse(wordParser, tolerant);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        // Table expression
        tableExpression.toParsedText(writer, actual);

        if (hasSpaceAfterTableExpression) {
            writer.append(SPACE);
        }

        // 'AS'
        if (asIdentifier != null) {
            writer.append(actual ? asIdentifier : AS);
        }

        if (hasSpaceAfterAs) {
            writer.append(SPACE);
        }

        // Identification variable
        if (identificationVariable != null) {
            identificationVariable.toParsedText(writer, actual);
        }
    }
}
