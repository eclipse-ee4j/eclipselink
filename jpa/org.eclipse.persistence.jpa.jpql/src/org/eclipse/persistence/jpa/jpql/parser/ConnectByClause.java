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
 * The <b>CONNECT BY</b> clause allows selecting rows in a hierarchical order using the hierarchical
 * query clause. <b>CONNECT BY</b> specifies the relationship between parent rows and child rows of
 * the hierarchy.
 *
 * <div><b>BNF:</b> <code>connectby_clause ::= <b>CONNECT BY</b> { single_valued_object_path_expression | collection_valued_path_expression }</code><p></div>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class ConnectByClause extends AbstractExpression {

    /**
     * The actual identifier found in the string representation of the JPQL query.
     */
    private String actualIdentifier;

    /**
     * The conditional expression.
     */
    private AbstractExpression expression;

    /**
     * Determines whether a whitespace was parsed after <b>CONNECT BY</b>.
     */
    private boolean hasSpaceAfterConnectBy;

    /**
     * Creates a new <code>ConnectByClause</code>.
     *
     * @param parent The parent of this expression
     */
    public ConnectByClause(AbstractExpression parent) {
        super(parent, CONNECT_BY);
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

        // 'CONNECT BY'
        children.add(buildStringExpression(CONNECT_BY));

        if (hasSpaceAfterConnectBy) {
            children.add(buildStringExpression(SPACE));
        }

        // Relationship expression
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
            return getQueryBNF(CollectionValuedPathExpressionBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the actual <b>CONNECT BY</b> identifier found in the string representation of the JPQL
     * query, which has the actual case that was used.
     *
     * @return The <b>CONNECT BY</b> identifier that was actually parsed, or an empty string if it
     * was not parsed
     */
    public String getActualIdentifier() {
        return actualIdentifier;
    }

    /**
     * Returns the {@link Expression} representing the relationship expression.
     *
     * @return The expression representing the relationship expression
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
        return getQueryBNF(ConnectByClauseBNF.ID);
    }

    /**
     * Determines whether the relationship expression was parsed.
     *
     * @return <code>true</code> if the relationship expression was parsed; <code>false</code> if it
     * was not parsed
     */
    public boolean hasExpression() {
        return expression != null &&
              !expression.isNull();
    }

    /**
     * Determines whether a whitespace was found after <b>CONNECT BY</b>.
     *
     * @return <code>true</code> if there was a whitespace after <b>CONNECT BY</b>;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceAfterConnectBy() {
        return hasSpaceAfterConnectBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // 'CONNECT BY'
        actualIdentifier = wordParser.moveForward(CONNECT_BY);

        hasSpaceAfterConnectBy = wordParser.skipLeadingWhitespace() > 0;

        // Relationship expression
        expression = parse(wordParser, InternalConnectByClauseBNF.ID, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        // 'CONNECT BY'
        writer.append(actual ? getActualIdentifier() : getText());

        if (hasSpaceAfterConnectBy) {
            writer.append(SPACE);
        }

        // Relationship expression
        if (expression != null) {
            expression.toParsedText(writer, actual);
        }
    }
}
