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
 * A query is an operation that retrieves data from one or more tables or views. In this reference,
 * a top-level <code><b>SELECT</b></code> statement is called a query, and a query nested within
 * another SQL statement is called a subquery.
 *
 * @see SelectStatement
 * @see SimpleSelectStatement
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractSelectStatement extends AbstractExpression {

    /**
     * The <code><b>FROM</b></code> clause of this select statement.
     */
    private AbstractExpression fromClause;

    /**
     * The <code><b>GROUP BY</b></code> clause of this select statement.
     */
    private AbstractExpression groupByClause;

    /**
     * Determines whether there is a whitespace after the identifier <code><b>FROM</b></code>.
     */
    private boolean hasSpaceAfterFrom;

    /**
     * Determines whether there is a whitespace after the identifier <code><b>GROUP BY</b></code>.
     */
    private boolean hasSpaceAfterGroupBy;

    /**
     * Determines whether there is a whitespace after the identifier <code><b>SELECT</b></code>.
     */
    private boolean hasSpaceAfterSelect;

    /**
     * Determines whether there is a whitespace after the identifier <code><b>WHERE</b></code>.
     */
    private boolean hasSpaceAfterWhere;

    /**
     * The <code><b>HAVING</b></code> clause of this select statement.
     */
    private AbstractExpression havingClause;

    /**
     * The <code><b>SELECT</b></code> clause of this select statement.
     */
    private AbstractExpression selectClause;

    /**
     * The <code><b>WHERE</b></code> clause of this select statement.
     */
    private AbstractExpression whereClause;

    /**
     * Creates a new <code>AbstractSelectStatement</code>.
     *
     * @param parent The parent of this expression
     */
    protected AbstractSelectStatement(AbstractExpression parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        getSelectClause().accept(visitor);
        getFromClause().accept(visitor);
        getWhereClause().accept(visitor);
        getGroupByClause().accept(visitor);
        getHavingClause().accept(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addChildrenTo(Collection<Expression> children) {
        children.add(getSelectClause());
        children.add(getFromClause());
        children.add(getWhereClause());
        children.add(getGroupByClause());
        children.add(getHavingClause());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addOrderedChildrenTo(List<Expression> children) {

        // SELECT clause
        if (selectClause != null) {
            children.add(selectClause);
        }

        // Space between SELECT and FROM clauses
        if (hasSpaceAfterSelect) {
            children.add(buildStringExpression(SPACE));
        }

        // FROM clause
        if (fromClause != null) {
            children.add(fromClause);
        }

        // Space between the FROM clause and an optional clause
        if (hasSpaceAfterFrom) {
            children.add(buildStringExpression(SPACE));
        }

        // WHERE clause
        if (whereClause != null) {
            children.add(whereClause);
        }

        // Space between WHERE clause and another optional clause
        if (hasSpaceAfterWhere) {
            children.add(buildStringExpression(SPACE));
        }

        // GROUP BY clause
        if (groupByClause != null) {
            children.add(groupByClause);
        }

        // Space between GROUP BY clause and another optional clause
        if (hasSpaceAfterGroupBy) {
            children.add(buildStringExpression(SPACE));
        }

        // HAVING clause
        if (havingClause != null) {
            children.add(havingClause);
        }
    }

    /**
     * Creates the expression representing the from clause of this select statement.
     *
     * @return A new from clause, <code>null</code> can't be returned
     */
    protected abstract AbstractFromClause buildFromClause();

    /**
     * Creates the expression representing the select clause of this select statement.
     *
     * @return A new from clause, <code>null</code> can't be returned
     */
    protected abstract AbstractSelectClause buildSelectClause();

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {

        if ((selectClause != null) && selectClause.isAncestor(expression)) {
            return selectClause.getQueryBNF();
        }

        if ((fromClause != null) && fromClause.isAncestor(expression)) {
            return fromClause.getQueryBNF();
        }

        if ((whereClause != null) && whereClause.isAncestor(expression)) {
            return whereClause.getQueryBNF();
        }

        if ((groupByClause != null) && groupByClause.isAncestor(expression)) {
            return groupByClause.getQueryBNF();
        }

        if ((havingClause != null) && havingClause.isAncestor(expression)) {
            return havingClause.getQueryBNF();
        }

        return super.findQueryBNF(expression);
    }

    /**
     * Returns the {@link Expression} representing the <b>FROM</b> clause.
     *
     * @return The expression representing the <b>FROM</b> clause
     */
    public final Expression getFromClause() {
        if (fromClause == null) {
            fromClause = buildNullExpression();
        }
        return fromClause;
    }

    /**
     * Returns the {@link Expression} representing the <b>GROUP BY</b> clause.
     *
     * @return The expression representing the <b>GROUP BY</b> clause
     */
    public final Expression getGroupByClause() {
        if (groupByClause == null) {
            groupByClause = buildNullExpression();
        }
        return groupByClause;
    }

    /**
     * Returns the {@link Expression} representing the <b>HAVING</b> clause.
     *
     * @return The expression representing the <b>HAVING</b> clause
     */
    public final Expression getHavingClause() {
        if (havingClause == null) {
            havingClause = buildNullExpression();
        }
        return havingClause;
    }

    /**
     * Returns the {@link AbstractSelectClause} representing the <b>SELECT</b> clause.
     *
     * @return The expression representing the <b>SELECT</b> clause
     */
    public final Expression getSelectClause() {
        if (selectClause == null) {
            selectClause = buildNullExpression();
        }
        return selectClause;
    }

    /**
     * Returns the {@link Expression} representing the <b>WHERE</b> clause.
     *
     * @return The expression representing the <b>WHERE</b> clause
     */
    public final Expression getWhereClause() {
        if (whereClause == null) {
            whereClause = buildNullExpression();
        }
        return whereClause;
    }

    /**
     * Determines whether the <b>FROM</b> clause was parsed or not.
     *
     * @return <code>true</code> if the query that got parsed had the <b>FROM</b> clause
     */
    public final boolean hasFromClause() {
        return fromClause != null &&
              !fromClause.isNull();
    }

    /**
     * Determines whether the <b>GROUP BY</b> clause was parsed or not.
     *
     * @return <code>true</code> if the query that got parsed had the <b>GROUP BY</b> clause
     */
    public final boolean hasGroupByClause() {
        return groupByClause != null &&
              !groupByClause.isNull();
    }

    /**
     * Determines whether the <b>HAVING</b> clause was parsed or not.
     *
     * @return <code>true</code> if the query that got parsed had the <b>HAVING</b> clause
     */
    public final boolean hasHavingClause() {
        return havingClause != null &&
              !havingClause.isNull();
    }

    /**
     * Determines whether the <b>SELECT</b> clause was parsed or not.
     *
     * @return <code>true</code> if the query that got parsed had the <b>HAVING</b> clause
     * @since 2.5
     */
    public final boolean hasSelectClause() {
        return selectClause != null &&
              !selectClause.isNull();
    }

    /**
     * Determines whether a whitespace was found after the <b>FROM</b> clause. In some cases, the
     * space is owned by a child of the <b>FROM</b> clause.
     *
     * @return <code>true</code> if there was a whitespace after the <b>FROM</b> clause and owned by
     * this expression; <code>false</code> otherwise
     */
    public final boolean hasSpaceAfterFrom() {
        return hasSpaceAfterFrom;
    }

    /**
     * Determines whether a whitespace was found after the <b>GROUP BY</b> clause. In some cases, the
     * space is owned by a child of the <b>GROUP BY</b> clause.
     *
     * @return <code>true</code> if there was a whitespace after the <b>GROUP BY</b> clause and owned
     * by this expression; <code>false</code> otherwise
     */
    public final boolean hasSpaceAfterGroupBy() {
        return hasSpaceAfterGroupBy;
    }

    /**
     * Determines whether a whitespace was found after the <b>SELECT</b> clause. In some cases, the
     * space is owned by a child of the <b>SELECT</b> clause.
     *
     * @return <code>true</code> if there was a whitespace after the <b>SELECT</b> clause and owned
     * by this expression; <code>false</code> otherwise
     */
    public final boolean hasSpaceAfterSelect() {
        return hasSpaceAfterSelect;
    }

    /**
     * Determines whether a whitespace was found after the <b>WHERE</b> clause. In some cases, the
     * space is owned by a child of the <b>WHERE</b> clause.
     *
     * @return <code>true</code> if there was a whitespace after the <b>WHERE</b> clause and owned by
     * this expression; <code>false</code> otherwise
     */
    public final boolean hasSpaceAfterWhere() {
        return hasSpaceAfterWhere;
    }

    /**
     * Determines whether the <b>WHERE</b> clause is defined.
     *
     * @return <code>true</code> if the query that got parsed had the <b>WHERE</b> clause
     */
    public final boolean hasWhereClause() {
        return whereClause != null &&
              !whereClause.isNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {

        // Parse 'SELECT' clause
        if (wordParser.startsWithIdentifier(SELECT)) {
            selectClause = buildSelectClause();
            selectClause.parse(wordParser, tolerant);

            hasSpaceAfterSelect = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse 'FROM' clause
        if (wordParser.startsWithIdentifier(FROM)) {
            fromClause = buildFromClause();
            fromClause.parse(wordParser, tolerant);

            hasSpaceAfterFrom = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse 'WHERE' clause
        if (wordParser.startsWithIdentifier(WHERE)) {
            whereClause = new WhereClause(this);
            whereClause.parse(wordParser, tolerant);

            hasSpaceAfterWhere = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse 'GROUP BY' clause
        if (wordParser.startsWithIdentifier(GROUP_BY)) {
            groupByClause = new GroupByClause(this);
            groupByClause.parse(wordParser, tolerant);

            hasSpaceAfterGroupBy = wordParser.skipLeadingWhitespace() > 0;
        }

        // Parse 'HAVING' clause
        if (wordParser.startsWithIdentifier(HAVING)) {
            havingClause = new HavingClause(this);
            havingClause.parse(wordParser, tolerant);
        }

        if (!wordParser.isTail() && !shouldManageSpaceAfterClause()) {

            if (hasSpaceAfterFrom     &&
                whereClause == null   &&
                groupByClause == null &&
                havingClause == null) {

                hasSpaceAfterFrom = false;
                wordParser.moveBackward(1);
            }
            else if (hasSpaceAfterWhere    &&
                     groupByClause == null &&
                     havingClause  == null) {

                hasSpaceAfterWhere = false;
                wordParser.moveBackward(1);
            }
            else if (hasSpaceAfterGroupBy &&
                     havingClause  == null) {

                hasSpaceAfterGroupBy = false;
                wordParser.moveBackward(1);
            }
        }
    }

    /**
     * Determines whether the whitespace following the <code><b>HAVING</b></code> clause should be
     * managed by this expression or not.
     *
     * @return <code>true</code> by default to keep the whitespace part of this expression;
     * <code>false</code> to let the parent handle it
     */
    protected boolean shouldManageSpaceAfterClause() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {

        // SELECT clause
        if (selectClause != null) {
            selectClause.toParsedText(writer, actual);
        }

        if (hasSpaceAfterSelect) {
            writer.append(SPACE);
        }

        // FROM clause
        if (fromClause != null) {
            fromClause.toParsedText(writer, actual);
        }

        if (hasSpaceAfterFrom) {
            writer.append(SPACE);
        }

        // WHERE clause
        if (whereClause != null) {
            whereClause.toParsedText(writer, actual);
        }

        if (hasSpaceAfterWhere) {
            writer.append(SPACE);
        }

        // GROUP BY clause
        if (groupByClause != null) {
            groupByClause.toParsedText(writer, actual);
        }

        if (hasSpaceAfterGroupBy) {
            writer.append(SPACE);
        }

        // HAVING clause
        if (havingClause != null) {
            havingClause.toParsedText(writer, actual);
        }
    }
}
