/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
 * The <b>UNION</b> clause allows the results of two queries to be combined.
 * <p>
 * <div nowrap><b>BNF:</b> <code>union_clause ::= <b>{ UNION | INTERSECT | EXCEPT }</b> [ALL] subquery</code>
 * <p>
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class UnionClause extends AbstractExpression {

	/**
	 * The actual identifier found in the string representation of the JPQL query.
	 */
	private String actualIdentifier;

	/**
	 * The actual <b>All</b> identifier found in the string representation of the JPQL query.
	 */
	private String allIdentifier;

	/**
	 * Determines if <code><b>ALL</b></code> keyword is used.
	 */
	private boolean hasAll;

	/**
	 * Determines whether a whitespace was parsed after <b>ALL</b>.
	 */
	private boolean hasSpaceAfterAll;

	/**
	 * Determines whether a whitespace was parsed after <b>UNION</b>.
	 */
	private boolean hasSpaceAfterIdentifier;

	/**
	 * The {@link Expression} representing the unioned query.
	 */
	private AbstractExpression query;

	/**
	 * Creates a new <code>UnionClause</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier Either <code><b>UNION</b></code>, <code><b>INTERSECT</b></code> or
	 * <code><b>EXCEPT</b></code>
	 */
	public UnionClause(AbstractExpression parent, String identifier) {
		super(parent, identifier);
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
		getQuery().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getQuery());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// 'UNION' or 'INTERSECT' or 'EXCEPT'
		children.add(buildStringExpression(getText()));

		if (hasSpaceAfterIdentifier) {
			children.add(buildStringExpression(SPACE));
		}

		// 'ALL'
		if (hasAll) {
			children.add(buildStringExpression(ALL));

			if (hasSpaceAfterAll) {
				children.add(buildStringExpression(SPACE));
			}
		}

		// Subquery
		if (query != null) {
			children.add(query);
		}
	}

	/**
	 * Returns the actual <code><b>ALL</b></code>< found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <code><b>ALL</b></code> identifier that was actually parsed
	 */
	public String getActualAll() {
		return allIdentifier;
	}

	/**
	 * Returns the actual identifier found in the string representation of the JPQL query, which
	 * has the actual case that was used.
	 *
	 * @return The identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return actualIdentifier;
	}

	/**
	 * Returns the union identifier.
	 *
	 * @return Either "UNION", "INTERSECT", "EXCEPT"
	 */
	public String getIdentifier() {
		return getText();
	}

	/**
	 * Returns the {@link Expression} representing the unioned query.
	 *
	 * @return The {@link expression} representing the subquery
	 */
	public Expression getQuery() {
		if (query == null) {
		    query = buildNullExpression();
		}
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(UnionClauseBNF.ID);
	}

	/**
	 * Determines whether <code><b>ALL</b></code> was parsed.
	 *
	 * @return <code>true</code> if <code><b>ALL</b></code> was parsed; <code>false</code> otherwise
	 */
	public boolean hasAll() {
		return hasAll;
	}

	/**
	 * Determines whether the query  was parsed.
	 *
	 * @return <code>true</code> the query was parsed; <code>false</code> otherwise
	 */
	public boolean hasQuery() {
		return query != null &&
		      !query.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <code><b>ALL</b></code>.
	 *
	 * @return <code>true</code> if a whitespace was parsed after <code><b>ALL</b></code>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterAll() {
		return hasSpaceAfterAll;
	}

	/**
	 * Determines whether a whitespace was parsed after the identifier.
	 *
	 * @return <code>true</code> if a whitespace was parsed after the identifier; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterIdentifier() {
		return hasSpaceAfterIdentifier;
	}

	/**
	 * Determines whether this {@link UnionClause} uses the <code><b>EXCEPT</b></code> identifier.
	 *
	 * @return <code>true</code> if the identifier is <code><b>EXCEPT</b></code>; <code>false</code>
	 * otherwise
	 */
	public boolean isExcept() {
		return getText() == EXCEPT;
	}

	/**
	 * Determines whether this {@link UnionClause} uses the <code><b>INTERSECT</b></code> identifier.
	 *
	 * @return <code>true</code> if the identifier is <code><b>INTERSECT</b></code>; <code>false</code>
	 * otherwise
	 */
	public boolean isIntersect() {
		return getText() == INTERSECT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(UNION)     ||
		       word.equalsIgnoreCase(INTERSECT) ||
		       word.equalsIgnoreCase(EXCEPT)    ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * Determines whether this {@link UnionClause} uses the <code><b>UNION</b></code> identifier.
	 *
	 * @return <code>true</code> if the identifier is <code><b>UNION</b></code>; <code>false</code>
	 * otherwise
	 */
	public boolean isUnion() {
		return getText() == UNION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse the identifier
		String identifier = parseIdentifier();
		actualIdentifier = wordParser.moveForward(identifier);
		setText(identifier);

		hasSpaceAfterIdentifier = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'ALL'
		hasAll = wordParser.startsWithIdentifier(ALL);

		if (hasAll) {
			allIdentifier = wordParser.moveForward(ALL);
			hasSpaceAfterAll = wordParser.skipLeadingWhitespace() > 0;
		}

		// Query
		if (tolerant) {
			query = parse(wordParser, SubqueryBNF.ID, tolerant);
		}
		else {
			query = new SimpleSelectStatement(this);
			query.parse(wordParser, tolerant);
		}
	}

	private String parseIdentifier() {
		switch (getText().charAt(0)) {
			case 'U': case 'u': return UNION;
			case 'I': case 'i': return INTERSECT;
			case 'E': case 'e': return EXCEPT;
			default:            return null; // Never happens
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// 'UNION', 'INTERSECT' or 'EXCEPT'
		writer.append(actual ? actualIdentifier : getText());

		if (hasSpaceAfterIdentifier) {
			writer.append(SPACE);
		}

		// 'ALL'
		if (hasAll) {
			writer.append(actual ? allIdentifier : ALL);

			if (hasSpaceAfterAll) {
				writer.append(SPACE);
			}
		}

		// Query
		if (query != null) {
			query.toParsedText(writer, actual);
		}
	}
}