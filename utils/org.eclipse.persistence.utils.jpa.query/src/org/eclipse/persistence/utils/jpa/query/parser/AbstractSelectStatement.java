/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.Collection;
import java.util.List;

/**
 * A select statement must always have a <b>SELECT</b> and a <b>FROM</b> clause.
 *
 * @see SelectStatement
 * @see SimpleSelectStatement
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public abstract class AbstractSelectStatement extends AbstractExpression {

	/**
	 * The <b>FROM</b> clause of this select statement.
	 */
	private AbstractExpression fromClause;

	/**
	 * The <b>GROUP BY</b> clause of this select statement.
	 */
	private AbstractExpression groupByClause;

	/**
	 * Determines whether there is a whitespace after the identifier <b>FROM</b>.
	 */
	private boolean hasSpaceAfterFrom;

	/**
	 * Determines whether there is a whitespace after the identifier <b>GROUP BY</b>.
	 */
	private boolean hasSpaceAfterGroupBy;

	/**
	 * Determines whether there is a whitespace after the identifier <b>SELECT</b>.
	 */
	private boolean hasSpaceAfterSelect;

	/**
	 * Determines whether there is a whitespace after the identifier <b>WHERE</b>.
	 */
	private boolean hasSpaceAfterWhere;

	/**
	 * The <b>HAVING</b> clause of this select statement.
	 */
	private AbstractExpression havingClause;

	/**
	 * The <b>SELECT</b> clause of this select statement.
	 */
	private AbstractSelectClause selectClause;

	/**
	 * The <b>WHERE</b> clause of this select statement.
	 */
	private AbstractExpression whereClause;

	/**
	 * Creates a new <code>AbstractSelectStatement</code>.
	 *
	 * @param parent The parent of this expression
	 */
	AbstractSelectStatement(AbstractExpression parent) {
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
	void addChildrenTo(Collection<Expression> children) {
		children.add(getSelectClause());
		children.add(getFromClause());
		children.add(getWhereClause());
		children.add(getGroupByClause());
		children.add(getHavingClause());
	}

	/**
	 * Manually adds the <b>FROM</b> clause to this <b>SELECT</b> statement.
	 *
	 * @return The new {@link AbstractFromClause <b>FROM</b> clause}
	 */
	public AbstractFromClause addFromClause() {
		AbstractFromClause fromClause = buildFromClause();
		this.fromClause = fromClause;
		return fromClause;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {

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
	 * Manually adds an empty <b>WHERE</b> clause.
	 *
	 * @return The {@link WhereClause}
	 */
	final WhereClause addWhereClause() {
		WhereClause whereClause = new WhereClause(this);
		this.whereClause = whereClause;
		this.hasSpaceAfterFrom = true;
		return whereClause;
	}

	/**
	 * Creates the expression representing the from clause of this select statement.
	 *
	 * @return A new from clause, <code>null</code> can't be returned
	 */
	abstract AbstractFromClause buildFromClause();

	/**
	 * Creates the expression representing the select clause of this select statement.
	 *
	 * @return A new from clause, <code>null</code> can't be returned
	 */
	abstract AbstractSelectClause buildSelectClause();

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
	void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'SELECT'
		selectClause = buildSelectClause();
		selectClause.parse(wordParser, tolerant);

		hasSpaceAfterSelect = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'FROM'
		if (wordParser.startsWithIdentifier(FROM)) {
			fromClause = buildFromClause();
			fromClause.parse(wordParser, tolerant);
		}

		hasSpaceAfterFrom = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'WHERE'
		if (wordParser.startsWithIdentifier(WHERE)) {
			whereClause = new WhereClause(this);
			whereClause.parse(wordParser, tolerant);
		}

		hasSpaceAfterWhere = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'GROUP BY'
		if (wordParser.startsWithIdentifier(GROUP_BY)) {
			groupByClause = new GroupByClause(this);
			groupByClause.parse(wordParser, tolerant);
		}

		hasSpaceAfterGroupBy = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'HAVING'
		if (wordParser.startsWithIdentifier(HAVING)) {
			havingClause = new HavingClause(this);
			havingClause.parse(wordParser, tolerant);
		}

		if (tolerant &&
		    !wordParser.isTail() &&
		    !shouldManageSpaceAfterClause())
		{
			if (hasSpaceAfterFrom     &&
			    whereClause   == null &&
			    groupByClause == null &&
			    havingClause  == null)
			{
				hasSpaceAfterFrom = false;
				wordParser.moveBackward(1);
			}
			else if (hasSpaceAfterWhere    &&
			         groupByClause == null &&
			         havingClause  == null)
			{
				hasSpaceAfterWhere = false;
				wordParser.moveBackward(1);
			}
			else if (hasSpaceAfterGroupBy &&
			         havingClause  == null)
			{
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
	boolean shouldManageSpaceAfterClause() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer) {

		// SELECT ...
		if (selectClause != null) {
			selectClause.toParsedText(writer);
		}

		if (hasSpaceAfterSelect) {
			writer.append(SPACE);
		}

		// FROM ...
		if (hasFromClause()) {
			fromClause.toParsedText(writer);
		}

		if (hasSpaceAfterFrom) {
			writer.append(SPACE);
		}

		// WHERE ...
		if (hasWhereClause()) {
			whereClause.toParsedText(writer);
		}

		if (hasSpaceAfterWhere) {
			writer.append(SPACE);
		}

		// GROUP BY ...
		if (hasGroupByClause()) {
			groupByClause.toParsedText(writer);
		}

		if (hasSpaceAfterGroupBy) {
			writer.append(SPACE);
		}

		// HAVING ...
		if (hasHavingClause()) {
			havingClause.toParsedText(writer);
		}
	}
}