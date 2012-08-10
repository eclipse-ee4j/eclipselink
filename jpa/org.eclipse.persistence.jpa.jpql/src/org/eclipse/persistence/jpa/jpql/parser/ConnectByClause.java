/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>CONNECT BY</b> clause allows selecting rows in a hierarchical order using the hierarchical
 * query clause. <b>CONNECT BY</b> specifies the relationship between parent rows and child rows of
 * the hierarchy. In a hierarchical query, one expression in condition must be qualified with the
 * <b>PRIOR</b> operator to refer to the parent row.
 * <p>
 * <b>PRIOR</b> is a unary operator and has the same precedence as the unary + and - arithmetic
 * operators. It evaluates the immediately following expression for the parent row of the current
 * row in a hierarchical query.
 * <p>
 * <b>PRIOR</b> is most commonly used when comparing column values with the equality operator (The
 * <b>PRIOR</b> keyword can be on either side of the operator).
 * <p>
 * Both the <b>CONNECT BY</b> condition and the <b>PRIOR</b> expression can take the form of an
 * uncorrelated subquery. However, the <b>PRIOR</b> expression cannot refer to a sequence. That is,
 * <b>CURRVAL</b> and <b>NEXTVAL</b> are not valid <b>PRIOR</b> expressions.
 * <p>
 * <div nowrap><b>BNF:</b> <code>connectby_clause ::= connectby_clause ::= <b>CONNECT BY</b> [NOCYCLE] { <b>PRIOR</b> parent_expr = child_expr | child_expr = <b>PRIOR</b> parent_expr }</code><p>
 *
 * @version 2.4
 * @since 2.3
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
	 * Determines whether a whitespace was parsed after the identifier <b>NOCYCLE</b>.
	 */
	private boolean hasSpaceAfterNocycle;

	/**
	 * The actual <b>NOCYCLE</b> identifier found in the string representation of the JPQL query.
	 */
	private String nocycleIdentifier;

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
	public void accept(ExpressionVisitor visitor) {
		acceptUnknownVisitor(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
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
		children.add(buildStringExpression(ORDER_BY));

		if (hasSpaceAfterConnectBy) {
			children.add(buildStringExpression(SPACE));
		}

		// 'NOCYCLE'
		if (nocycleIdentifier != null) {

			if (hasSpaceAfterNocycle) {
				children.add(buildStringExpression(SPACE));
			}
		}

		// Expression
		if (expression != null) {
			children.add(expression);
		}
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
	 * Returns the actual <b>NOCYCLE</b> identifier found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <b>NOCYCLE</b> identifier that was actually parsed, or an empty string if it was
	 * not parsed
	 */
	public String getActualNocycleIdentifier() {
		return (nocycleIdentifier != null) ? nocycleIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the {@link Expression} representing the TODO.
	 *
	 * @return The expression representing the TODO
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
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ConnectByClauseBNF.ID);
	}

	/**
	 * Determines whether the conditional expression was parsed.
	 *
	 * @return <code>true</code> if the conditional expression was parsed; <code>false</code> if it
	 * was not parsed
	 */
	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether the identifier <b>NOCYCLE</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>NOCYCLE</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasNocycle() {
		return nocycleIdentifier != null;
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
	 * Determines whether a whitespace was found after <b>NOCYCLE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>NOCYCLE</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterNocycle() {
		return hasSpaceAfterNocycle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// 'CONNECT BY'
		actualIdentifier = wordParser.moveForward(CONNECT_BY);

		hasSpaceAfterConnectBy = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'NOCYCLE'
		boolean nocycle = wordParser.startsWithIdentifier(NOCYCLE);

		if (nocycle) {
			nocycleIdentifier = wordParser.moveForward(NOCYCLE);
			hasSpaceAfterNocycle = wordParser.skipLeadingWhitespace() > 0;
		}

		// Conditional expression
		expression = parse(wordParser, ConditionalExpressionBNF.ID, tolerant);
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

		// 'NOCYCLE'
		if (nocycleIdentifier != null) {
			writer.append(actual ? nocycleIdentifier : NOCYCLE);

			if (hasSpaceAfterNocycle) {
				writer.append(SPACE);
			}
		}

		// Conditional expression
		if (expression != null) {
			expression.toParsedText(writer, actual);
		}
	}
}