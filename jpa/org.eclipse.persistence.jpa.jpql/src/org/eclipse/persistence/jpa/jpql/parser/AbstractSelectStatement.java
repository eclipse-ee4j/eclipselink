/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * A select statement must always have a <code><b>SELECT</b></code> and a <code><b>FROM</b></code>
 * clause. The other clauses are optional.
 * <p>
 * <div nowrap><b>BNF:</b> <code>select_statement ::= select_clause from_clause [where_clause] [groupby_clause] [having_clause] [orderby_clause]</code>
 * <p>
 *
 * @see SelectStatement
 * @see SimpleSelectStatement
 *
 * @version 2.4
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
	private AbstractSelectClause selectClause;

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
	 * Sets the query statement to be a select clause.
	 *
	 * @return This expression's select clause
	 */
	public final SelectClause addSelectClause() {
		selectClause = buildSelectClause();
		return (SelectClause) selectClause;
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
	public AbstractSelectClause getSelectClause() {
		if (selectClause == null) {
			selectClause = buildSelectClause();
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
	 * Determines whether the <b>FROM</b> clause is defined.
	 *
	 * @return <code>true</code> if the query that got parsed had the <b>FROM</b> clause
	 */
	public final boolean hasFromClause() {
		return fromClause != null &&
		      !fromClause.isNull();
	}

	/**
	 * Determines whether the <b>GROUP BY</b> clause is defined.
	 *
	 * @return <code>true</code> if the query that got parsed had the <b>GROUP BY</b> clause
	 */
	public final boolean hasGroupByClause() {
		return groupByClause != null &&
		      !groupByClause.isNull();
	}

	/**
	 * Determines whether the <b>HAVING</b> clause is defined.
	 *
	 * @return <code>true</code> if the query that got parsed had the <b>HAVING</b> clause
	 */
	public final boolean hasHavingClause() {
		return havingClause != null &&
		      !havingClause.isNull();
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
		selectClause = buildSelectClause();
		selectClause.parse(wordParser, tolerant);

		hasSpaceAfterSelect = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'FROM' clause
		if (wordParser.startsWithIdentifier(FROM)) {
			fromClause = buildFromClause();
			fromClause.parse(wordParser, tolerant);
		}

		hasSpaceAfterFrom = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'WHERE' clause
		if (wordParser.startsWithIdentifier(WHERE)) {
			whereClause = new WhereClause(this);
			whereClause.parse(wordParser, tolerant);
		}

		hasSpaceAfterWhere = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'GROUP BY' clause
		if (wordParser.startsWithIdentifier(GROUP_BY)) {
			groupByClause = new GroupByClause(this);
			groupByClause.parse(wordParser, tolerant);
		}

		hasSpaceAfterGroupBy = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'HAVING' clause
		if (wordParser.startsWithIdentifier(HAVING)) {
			havingClause = new HavingClause(this);
			havingClause.parse(wordParser, tolerant);
		}

		if (!wordParser.isTail() && !shouldManageSpaceAfterClause()) {

			if (hasSpaceAfterFrom     &&
			    whereClause   == null &&
			    groupByClause == null &&
			    havingClause  == null) {

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
	 * Determines whether
	 *
	 * @return
	 */
	protected boolean shouldManageSpaceAfterClause() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// SELECT ...
		if (selectClause != null) {
			selectClause.toParsedText(writer, actual);
		}

		if (hasSpaceAfterSelect) {
			writer.append(SPACE);
		}

		// FROM ...
		if (hasFromClause()) {
			fromClause.toParsedText(writer, actual);
		}

		if (hasSpaceAfterFrom) {
			writer.append(SPACE);
		}

		// WHERE ...
		if (hasWhereClause()) {
			whereClause.toParsedText(writer, actual);
		}

		if (hasSpaceAfterWhere) {
			writer.append(SPACE);
		}

		// GROUP BY ...
		if (hasGroupByClause()) {
			groupByClause.toParsedText(writer, actual);
		}

		if (hasSpaceAfterGroupBy) {
			writer.append(SPACE);
		}

		// HAVING ...
		if (hasHavingClause()) {
			havingClause.toParsedText(writer, actual);
		}
	}
}