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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This is the delete clause of the delete statement.
 * <p>
 * A <b>DELETE</b> statement provides bulk operations over sets of entities of a single entity class
 * (together with its subclasses, if any). Only one entity abstract schema type may be specified in
 * the <b>UPDATE</b> clause.
 *
 * <div><b>BNF:</b> <code>delete_clause ::= DELETE FROM abstract_schema_name [[AS] identification_variable]</code></div>
 * <p>
 * Example: <code>DELETE FROM Employee e</code>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class DeleteClause extends AbstractExpression {

    /**
     * The actual <b>DELETE</b> identifier found in the string representation of the JPQL query.
     */
    private String deleteIdentifier;

    /**
     * The actual <b>FROM</b> identifier found in the string representation of the JPQL query.
     */
    private String fromIdentifier;

    /**
     * Determines whether a whitespace was parsed after <b>DELETE</b>.
     */
    private boolean hasSpaceAfterDelete;

    /**
     * Determines whether a whitespace was parsed after <b>FROM</b>.
     */
    private boolean hasSpaceAfterFrom;

    /**
     * The {@link Expression} representing the range variable declaration.
     */
    private AbstractExpression rangeVariableDeclaration;

    /**
     * Creates a new <code>DeleteClause</code>.
     *
     * @param parent The parent of this expression
     */
    public DeleteClause(AbstractExpression parent) {
        super(parent, DELETE);
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
        getRangeVariableDeclaration().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getRangeVariableDeclaration());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {

        // 'DELETE'
        children.add(buildStringExpression(DELETE));

        if (hasSpaceAfterDelete) {
            children.add(buildStringExpression(SPACE));
        }

        // 'FROM'
        if (fromIdentifier != null) {
            children.add(buildStringExpression(FROM));
        }

        if (hasSpaceAfterFrom) {
            children.add(buildStringExpression(SPACE));
        }

        // Range declaration variable
        if (rangeVariableDeclaration != null) {
            children.add(rangeVariableDeclaration);
        }
    }

    /**
     * Creates a new {@link CollectionExpression} that will wrap the single range variable declaration.
     *
     * @return The single range variable declaration represented by a temporary collection
     */
    public CollectionExpression buildCollectionExpression() {

        List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
        children.add((AbstractExpression) getRangeVariableDeclaration());

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

        if ((rangeVariableDeclaration != null) && rangeVariableDeclaration.isAncestor(expression)) {
            return getQueryBNF(DeleteClauseRangeVariableDeclarationBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the actual <b>DELETE</b> found in the string representation of the JPQL query, which
     * has the actual case that was used.
     *
     * @return The <b>DELETE</b> identifier that was actually parsed, or an empty string if it was
     * not parsed
     */
    public String getActualDeleteIdentifier() {
        return (deleteIdentifier != null) ? deleteIdentifier : ExpressionTools.EMPTY_STRING;
    }

    /**
     * Returns the actual <b>FROM</b> identifier found in the string representation of the JPQL
     * query, which has the actual case that was used.
     *
     * @return The <b>FROM</b> identifier that was actually parsed, or an empty string if it was
     * not parsed
     */
    public String getActualFromIdentifier() {
        return (fromIdentifier != null) ? fromIdentifier : ExpressionTools.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(DeleteClauseBNF.ID);
    }

    /**
     * Returns the {@link Expression} representing the range variable declaration.
     *
     * @return The expression representing the range variable declaration
     */
    public Expression getRangeVariableDeclaration() {
        if (rangeVariableDeclaration == null) {
            rangeVariableDeclaration = buildNullExpression();
        }
        return rangeVariableDeclaration;
    }

    /**
     * Determines whether the identifier <b>FROM</b> was parsed.
     *
     * @return <code>true</code> if <b>FROM</b> was parsed; <code>false</code> otherwise
     */
    public boolean hasFrom() {
        return fromIdentifier != null;
    }

    /**
     * Determines whether the range variable declaration was parsed.
     *
     * @return <code>true</code> if the range variable declaration was parsed; <code>false</code> otherwise
     */
    public boolean hasRangeVariableDeclaration() {
        return rangeVariableDeclaration != null &&
              !rangeVariableDeclaration.isNull();
    }

    /**
     * Determines whether a whitespace was found after the identifier <b>DELETE</b>.
     *
     * @return <code>true</code> if there was a whitespace after the identifier <b>DELETE</b>;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceAfterDelete() {
        return hasSpaceAfterDelete;
    }

    /**
     * Determines whether a whitespace was found after the identifier <b>FROM</b>.
     *
     * @return <code>true</code> if there was a whitespace after the identifier <b>FROM</b>;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceAfterFrom() {
        return hasSpaceAfterFrom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // Parse 'DELETE'
        deleteIdentifier = wordParser.moveForward(DELETE);

        hasSpaceAfterDelete = wordParser.skipLeadingWhitespace() > 0;

        // Parse 'FROM'
        if (!tolerant || wordParser.startsWithIdentifier(FROM)) {
            fromIdentifier = wordParser.moveForward(FROM);
            hasSpaceAfterFrom = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse the range variable declaration
        if (tolerant) {
            rangeVariableDeclaration = parse(
                wordParser,
                DeleteClauseRangeVariableDeclarationBNF.ID,
                tolerant
            );
        }
        else {
            rangeVariableDeclaration = new RangeVariableDeclaration(this);
            rangeVariableDeclaration.parse(wordParser, tolerant);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        // 'DELETE'
        writer.append(actual ? deleteIdentifier : DELETE);

        if (hasSpaceAfterDelete) {
            writer.append(SPACE);
        }

        // 'FROM'
        if (fromIdentifier != null) {
            writer.append(actual ? fromIdentifier : FROM);
        }

        if (hasSpaceAfterFrom) {
            writer.append(SPACE);
        }

        // Range variable declaration
        if (rangeVariableDeclaration != null) {
            rangeVariableDeclaration.toParsedText(writer, actual);
        }
    }
}
