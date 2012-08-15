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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * An <code><b>AS OF</b></code> clause is part of a flashback query, which provides ways to view
 * past states of database objects, or to return database objects to a previous state, without using
 * traditional point-in-time recovery.
 * <p>
 * Specify <code><b>AS OF</b></code> to retrieve the single version of the rows returned by the
 * query at a particular change number (SCN) or timestamp. If you specify SCN, then the expression
 * must evaluate to a number. If you specify <code><b>TIMESTAMP</b></code>, then the expression must
 * evaluate to a timestamp value. Oracle Database returns rows as they existed at the specified
 * system change number or time.
 *
 * <div nowrap><b>BNF:</b> <code>asof_clause ::= AS OF { SCN | TIMESTAMP } expression</code></div><p>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class AsOfClause extends AbstractExpression {

	/**
	 * The {@link Expression} representing the timestamp or change number.
	 */
	private AbstractExpression expression;

	/**
	 * Determines whether a whitespace was found after <code><b>AS OF</b><code>.
	 */
	private boolean hasSpaceAfterIdentifier;

	/**
	 * Determines whether a whitespace was found after either <code><b>SCN</b><code> or
	 * <code><b>TIMESTAMP</b></code>.
	 */
	private boolean hasSpaceAfterCategory;

	/**
	 * The actual identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

	/**
	 * The actual <code><b>SCN</b></code> identifier found in the string representation of the
	 * JPQL query.
	 */
	private String scnIdentifier;

	/**
	 * The actual <code><b>TIMESTAMP</b></code> identifier found in the string representation of the
	 * JPQL query.
	 */
	private String timestampIdentifier;

	/**
	 * Creates a new <code>AsOfClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public AsOfClause(AbstractExpression parent) {
		super(parent, AS_OF);
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

		// 'AS OF'
		children.add(buildStringExpression(AS_OF));

		if (hasSpaceAfterIdentifier) {
			children.add(buildStringExpression(SPACE));
		}

		// 'SCN'
		if (scnIdentifier != null) {
			children.add(buildStringExpression(SCN));
		}
		// 'TIMESTAMP'
		else if (timestampIdentifier != null) {
			children.add(buildStringExpression(TIMESTAMP));
		}

		if (hasSpaceAfterCategory) {
			children.add(buildStringExpression(SPACE));
		}

		// Expression
		if (expression != null) {
			children.add(expression);
		}
	}

	/**
	 * Returns the actual <b>AS OF</b> identifier found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <b>AS OF</b> identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return identifier;
	}

	/**
	 * Returns the actual <b>SCN</b> identifier found in the string representation of the JPQL query,
	 * which has the actual case that was used.
	 *
	 * @return The <b>SCN</b> identifier that was actually parsed, or an empty string if it was not parsed
	 */
	public String getActualScnIdentifier() {
		return (scnIdentifier != null) ? scnIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the actual <b>TIMESTAMP</b> identifier found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <b>TIMESTAMP</b> identifier that was actually parsed, or an empty string if it was
	 * not parsed
	 */
	public String getActualTimestampIdentifier() {
		return (timestampIdentifier != null) ? timestampIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the {@link Expression} representing the timestamp or change number.
	 *
	 * @return The {@link Expression} that was parsed representing the timestamp or change number
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
		return getQueryBNF(AsOfClauseBNF.ID);
	}

	/**
	 * Determines whether the {@link Expression} representing the timestamp or change number was
	 * parsed or not.
	 *
	 * @return <code>true</code> if the timestamp or change number was parsed; <code>false</code> otherwise
	 */
	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether the identifier <b>SCN</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>SCN</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasScn() {
		return scnIdentifier != null;
	}

	/**
	 * Determines whether a whitespace was found after <b>AS OF</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AS OF</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterIdentifier() {
		return hasSpaceAfterIdentifier;
	}

	/**
	 * Determines whether a whitespace was found after either <b>SCN</b> or <code>TIMESTAMP</code>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>SCN</b> or <code>TIMESTAMP</code>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterCategory() {
		return hasSpaceAfterCategory;
	}

	/**
	 * Determines whether the identifier <b>TIMESTAMP</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>TIMESTAMP</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasTimestamp() {
		return timestampIdentifier != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// 'AS OF'
		identifier = wordParser.moveForward(AS_OF);
		hasSpaceAfterIdentifier = wordParser.skipLeadingWhitespace() > 0;

		// 'SCN'
		if (wordParser.startsWithIdentifier(SCN)) {
			scnIdentifier = wordParser.moveForward(SCN);
			hasSpaceAfterCategory = wordParser.skipLeadingWhitespace() > 0;
		}
		// 'TIMESTAMP'
		else if (wordParser.startsWithIdentifier(TIMESTAMP)) {
			timestampIdentifier = wordParser.moveForward(TIMESTAMP);
			hasSpaceAfterCategory = wordParser.skipLeadingWhitespace() > 0;
		}

		// Expression
		                               // TODO
		expression = parse(wordParser, ScalarExpressionBNF.ID, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// 'AS OF'
		writer.append(actual ? identifier : AS_OF);

		if (hasSpaceAfterIdentifier) {
			writer.append(SPACE);
		}

		// 'SCN'
		if (scnIdentifier != null) {
			writer.append(actual ? scnIdentifier : SCN);
		}

		// 'TIMESTAMP'
		if (timestampIdentifier != null) {
			writer.append(actual ? timestampIdentifier : TIMESTAMP);
		}

		if (hasSpaceAfterCategory) {
			writer.append(SPACE);
		}

		// Expression
		if (expression != null) {
			expression.toParsedText(writer, actual);
		}
	}
}