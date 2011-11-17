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
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>UPDATE</b> clause of a query consists of a conditional expression used to select objects
 * or values that satisfy the expression. The <b>UPDATE</b> clause restricts the result of a select
 * statement or the scope of an update operation.
 * <p>
 * <div nowrap><b>BNF:</b> <code>update_statement ::= update_clause [where_clause]</code><p>
 *
 * @see JPQLExpression
 * @see UpdateClause
 * @see WhereClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class UpdateStatement extends AbstractExpression {

	/**
	 * Determines whether a whitespace was parsed after the <b>UPDATE</b> clause.
	 */
	private boolean hasSpaceAfterUpdateClause;

	/**
	 * The expression representing the <b>UPDATE</b> clause.
	 */
	private UpdateClause updateClause;

	/**
	 * The expression representing the <b>WHERE</b> clause.
	 */
	private AbstractExpression whereClause;

	/**
	 * Creates a new <code>UpdateStatement</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public UpdateStatement(AbstractExpression parent) {
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
	public void acceptChildren(ExpressionVisitor visitor) {
		getUpdateClause().accept(visitor);
		getWhereClause().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getUpdateClause());
		children.add(getWhereClause());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<StringExpression> children) {

		// Update clause
		children.add(updateClause);

		if (hasSpaceAfterUpdateClause) {
			children.add(buildStringExpression(SPACE));
		}

		// Where clause
		if (whereClause != null) {
			children.add(whereClause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(UpdateStatementBNF.ID);
	}

	/**
	 * Returns the {@link UpdateClause} representing the <b>UPDATE</b> clause.
	 *
	 * @return The section of the update statement representing the <b>UPDATE</b> clause
	 */
	public UpdateClause getUpdateClause() {
		return updateClause;
	}

	/**
	 * Returns the {@link Expression} representing the <b>WHERE</b> clause.
	 *
	 * @return The section of the update statement representing the <b>WHERE</b> clause
	 */
	public Expression getWhereClause() {
		if (whereClause == null) {
			whereClause = buildNullExpression();
		}
		return whereClause;
	}

	/**
	 * Determines whether a whitespace was parsed after the <b>UPDATE</b> clause.
	 *
	 * @return <code>true</code> if a whitespace was parsed after the <b>UPDATE</b> clause;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterUpdateClause() {
		return hasSpaceAfterUpdateClause;
	}

	/**
	 * Determines whether the <b>WHERE</b> clause is defined or not.
	 *
	 * @return <code>true</code> if this statement has a <b>WHERE</b> clause; <code>false</code> if
	 * it was not parsed
	 */
	public boolean hasWhereClause() {
		return whereClause != null &&
		      !whereClause.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'UPDATE'
		updateClause = new UpdateClause(this);
		updateClause.parse(wordParser, tolerant);

		hasSpaceAfterUpdateClause = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'WHERE'
		if (wordParser.startsWithIdentifier(WHERE)) {
			whereClause = new WhereClause(this);
			whereClause.parse(wordParser, tolerant);
		}

		// Now fully qualify attribute names with a virtual identification variable
		accept(FullyQualifyPathExpressionVisitor.instance());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Update clause
		updateClause.toParsedText(writer, actual);

		if (hasSpaceAfterUpdateClause) {
			writer.append(SPACE);
		}

		// Where clause
		if (whereClause != null) {
			whereClause.toParsedText(writer, actual);
		}
	}
}