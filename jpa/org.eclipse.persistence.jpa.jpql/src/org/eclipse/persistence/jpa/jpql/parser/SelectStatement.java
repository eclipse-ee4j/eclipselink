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

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * A <code><b>SELECT</b></code> query is an operation that retrieves data from one or more tables or
 * views.
 * <p>
 * JPA:
 * <div><b>BNF:</b> <code>select_statement ::= select_clause
 *                                                    from_clause
 *                                                    [where_clause]
 *                                                    [groupby_clause]
 *                                                    [having_clause]
 *                                                    [orderby_clause]</code></div>
 * <p>
 * EclipseLink 2.4:
 * <div><b>BNF:</b> <code>select_statement ::= select_clause
 *                                                    from_clause
 *                                                    [where_clause]
 *                                                    [groupby_clause]
 *                                                    [having_clause]
 *                                                    [orderby_clause]
 *                                                    {union_clause}*</code></div>
 * <p>
 * HQL query (EclipseLink 2.5):
 * <div><b>BNF:</b> <code>select_statement ::= [select_clause]
 *                                                    from_clause
 *                                                    [where_clause]
 *                                                    [groupby_clause]
 *                                                    [having_clause]
 *                                                    [orderby_clause]
 *                                                    {union_clause}*</code></div>
 * <p>
 *
 * @see FromClause
 * @see GroupByClause
 * @see HavingClause
 * @see HierarchicalQueryClause
 * @see OrderByClause
 * @see SelectClause
 * @see UnionClause
 * @see WhereClause
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class SelectStatement extends AbstractSelectStatement {

    /**
     * Determines whether there is a whitespace after the select statement parsed by the superclass
     * and the <b>ORDER BY</b> identifier.
     */
    private boolean hasSpaceBeforeOrderBy;

    /**
     * Determines whether there is a whitespace after the select statement parsed by the superclass
     * and the <b>UNION</b> identifier.
     */
    private boolean hasSpaceBeforeUnion;

    /**
     * The <b>ORDER BY</b> expression.
     */
    private AbstractExpression orderByClause;

    /**
     * The <b>UNION</b> expression.
     */
    private AbstractExpression unionClauses;

    /**
     * Creates a new <code>SelectStatement</code>.
     *
     * @param parent The parent of this expression
     */
    public SelectStatement(AbstractExpression parent) {
        super(parent);
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
    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        super.acceptChildren(visitor);
        getOrderByClause().accept(visitor);
        getUnionClauses().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        super.addChildrenTo(children);
        children.add(getOrderByClause());
        children.add(getUnionClauses());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {

        super.addOrderedChildrenTo(children);

        if (hasSpaceBeforeOrderBy) {
            children.add(buildStringExpression(SPACE));
        }

        // 'ORDER BY' clause
        if (orderByClause != null) {
            children.add(orderByClause);
        }

        // 'UNION' clauses
        if (unionClauses != null) {
            children.add(unionClauses);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FromClause buildFromClause() {
        return new FromClause(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SelectClause buildSelectClause() {
        return new SelectClause(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((orderByClause != null) && orderByClause.isAncestor(expression)) {
            return orderByClause.getQueryBNF();
        }

        if ((unionClauses != null) && unionClauses.isAncestor(expression)) {
            return getQueryBNF(UnionClauseBNF.ID);
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the {@link Expression} representing the <b>ORDER BY</b> clause.
     *
     * @return The expression representing the <b>ORDER BY</b> clause
     */
    public Expression getOrderByClause() {
        if (orderByClause == null) {
            orderByClause = buildNullExpression();
        }
        return orderByClause;
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(SelectStatementBNF.ID);
    }

    /**
     * Returns the {@link Expression} representing the <b>UNION</b> clauses.
     *
     * @return The {@link Expression} representing the <b>UNION</b> clauses
     */
    public Expression getUnionClauses() {
        if (unionClauses == null) {
            unionClauses = buildNullExpression();
        }
        return unionClauses;
    }

    /**
     * Determines whether the <b>ORDER BY</b> clause is defined or not.
     *
     * @return <code>true</code> if the query that got parsed had the <b>ORDER BY</b> clause
     */
    public boolean hasOrderByClause() {
        return orderByClause != null &&
              !orderByClause.isNull();
    }

    /**
     * Determines whether a whitespace was parsed before the <b>ORDER BY</b> clause. In some cases,
     * the space could be owned by a child of the previous clause.
     *
     * @return <code>true</code> if there was a whitespace before the <b>ORDER BY</b>; <code>false</code>
     * otherwise
     */
    public boolean hasSpaceBeforeOrderBy() {
        return hasSpaceBeforeOrderBy;
    }

    /**
     * Determines whether a whitespace was parsed before the <b>UNION</b> clause. In some cases,
     * the space could be owned by a child of the previous clause.
     *
     * @return <code>true</code> if there was a whitespace before the <b>UNION</b>;
     * <code>false</code> otherwise
     */
    public boolean hasSpaceBeforeUnion() {
        return hasSpaceBeforeUnion;
    }

    /**
     * Determines whether at least one <b>UNION</b> clause was defined.
     *
     * @return <code>true</code> if the query that got parsed had the <b>UNION</b> clauses
     */
    public boolean hasUnionClauses() {
        return unionClauses != null &&
              !unionClauses.isNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        if (word.equalsIgnoreCase(UNION)     ||
            word.equalsIgnoreCase(INTERSECT) ||
            word.equalsIgnoreCase(EXCEPT)) {

            return false;
        }

        return super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        super.parse(wordParser, tolerant);

        hasSpaceBeforeOrderBy = wordParser.skipLeadingWhitespace() > 0;

        // Parse 'ORDER BY'
        if (wordParser.startsWithIdentifier(ORDER_BY)) {
            orderByClause = new OrderByClause(this);
            orderByClause.parse(wordParser, tolerant);
        }

        // Parse the union clauses and make sure the grammar supports it
        if (getQueryBNF(UnionClauseBNF.ID) != null) {
            hasSpaceBeforeUnion = wordParser.skipLeadingWhitespace() > 0;
            unionClauses = parse(wordParser, UnionClauseBNF.ID, tolerant);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        super.toParsedText(writer, actual);

        if (hasSpaceBeforeOrderBy) {
            writer.append(SPACE);
        }

        // 'ORDER BY' clause
        if (hasOrderByClause()) {
            orderByClause.toParsedText(writer, actual);
        }

        if (hasSpaceBeforeUnion) {
            writer.append(SPACE);
        }

        // 'UNION' clauses
        if (hasUnionClauses()) {
            unionClauses.toParsedText(writer, actual);
        }
    }
}
