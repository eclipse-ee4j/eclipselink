/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 * If a table contains hierarchical data, then rows can be selected in a hierarchical order using
 * the hierarchical query clause.
 * <p>
 * <code><b>START WITH</b></code> specifies the root row(s) of the hierarchy.
 * <p>
 * <code><b>CONNECT BY</b></code> specifies the relationship between parent rows and child rows of
 * the hierarchy.
 * <p>
 * <div nowrap><b>BNF:</b> <code>hierarchical_query_clause ::= [start_with_clause] connectby_clause</code><p>
 *
 * @see StartWithClause
 * @see ConnectByClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class HierarchicalQueryClause extends AbstractExpression {

	/**
	 * The {@link Expression} that represents the <code><b>CONNECT BY</b></code> clause.
	 */
	private AbstractExpression connectByClause;

	/**
	 * Determines whether a whitespace was parsed after the <code><b>START WITH</b></code> clause.
	 */
	private boolean hasSpaceAfterStartWithClause;

	/**
	 * The {@link Expression} that represents the <code><b>START WITH</b></code> clause.
	 */
	private AbstractExpression startWithClause;

	/**
	 * Creates a new <code>HierarchicalQueryClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public HierarchicalQueryClause(AbstractExpression parent) {
		super(parent);
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
	public void acceptChildren(ExpressionVisitor visitor) {
		getStartWithClause().accept(visitor);
		getConnectByClause().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		super.addChildrenTo(children);
		children.add(getStartWithClause());
		children.add(getConnectByClause());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// START WITH clause
		if (startWithClause != null) {
			children.add(startWithClause);
		}

		if (hasSpaceAfterStartWithClause) {
			children.add(buildStringExpression(SPACE));
		}

		// CONNECT BY clause
		if (connectByClause != null) {
			children.add(connectByClause);
		}
	}

	/**
	 * Returns the {@link Expression} representing the <b>CONNECT BY</b> clause.
	 *
	 * @return The expression representing the <b>CONNECT BY</b> clause
	 */
	public Expression getConnectByClause() {
		if (connectByClause == null) {
			connectByClause = buildNullExpression();
		}
		return connectByClause;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(HierarchicalQueryClauseBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the <b>START WITH</b> clause.
	 *
	 * @return The expression representing the <b>START WITH</b> clause
	 */
	public Expression getStartWithClause() {
		if (startWithClause == null) {
			startWithClause = buildNullExpression();
		}
		return startWithClause;
	}

	/**
	 * Determines whether the <b>CONNECT BY</b> clause is defined.
	 *
	 * @return <code>true</code> if the query that got parsed had the <b>CONNECT BY</b> clause
	 */
	public boolean hasConnectByClause() {
		return connectByClause != null &&
		      !connectByClause.isNull();
	}

	/**
	 * Determines whether a whitespace was found after the <code><b>START WITH</b></code> clause. In
	 * some cases, the space is owned by a child of the hierarchical query clause.
	 *
	 * @return <code>true</code> if there was a whitespace after the <code><b>START WITH</b></code>
	 * clause and owned by this expression; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterStartWithClause() {
		return hasSpaceAfterStartWithClause;
	}

	/**
	 * Determines whether the <b>START WITH</b> clause is defined.
	 *
	 * @return <code>true</code> if the query that got parsed had the <b>START WITH</b> clause
	 */
	public boolean hasStartWithClause() {
		return startWithClause != null &&
		      !startWithClause.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// START WITH clause
		if (wordParser.startsWithIdentifier(START_WITH)) {
			startWithClause = new StartWithClause(this);
			startWithClause.parse(wordParser, tolerant);

			hasSpaceAfterStartWithClause = wordParser.skipLeadingWhitespace() > 0;
		}

		// CONNECT BY clause
		if (wordParser.startsWithIdentifier(CONNECT_BY)) {
			connectByClause = new ConnectByClause(this);
			connectByClause.parse(wordParser, tolerant);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// START WITH clause
		if (startWithClause != null) {
			startWithClause.toParsedText(writer, actual);

			if (hasSpaceAfterStartWithClause) {
				writer.append(SPACE);
			}
		}

		// CONNECT BY clause
		if (connectByClause != null) {
			connectByClause.toParsedText(writer, actual);
		}
	}
}