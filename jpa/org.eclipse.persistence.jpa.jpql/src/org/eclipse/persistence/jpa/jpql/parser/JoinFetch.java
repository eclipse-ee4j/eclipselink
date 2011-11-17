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
import java.util.StringTokenizer;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * A <b>JOIN FETCH</b> enables the fetching of an association as a side effect of the execution of
 * a query. A <b>JOIN FETCH</b> is specified over an entity and its related entities.
 * <p>
 * <div nowrap><b>BNF:</b> <code>fetch_join ::= join_spec FETCH join_association_path_expression</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JoinFetch extends AbstractExpression {

	/**
	 * Determines whether a whitespace was parsed after <b>FETCH</b>.
	 */
	private boolean hasSpaceAfterFetch;

	/**
	 * The actual <b></b> identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

	/**
	 * The expression of this fetch join.
	 */
	private AbstractExpression joinAssociationPath;

	/**
	 * Creates a new <code>JoinFetch</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The full <b>JOIN</b> identifier
	 */
	public JoinFetch(AbstractExpression parent, String identifier) {
		super(parent, identifier);
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
		getJoinAssociationPath().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getJoinAssociationPath());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<StringExpression> children) {

		String joinType = getIdentifier().toString();
		String space = " ";

		// Break the identifier into multiple identifiers
		if (joinType.indexOf(space) != -1) {
			StringTokenizer tokenizer = new StringTokenizer(joinType, space, true);

			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				children.add(buildStringExpression(token));
			}
		}
		else {
			children.add(buildStringExpression(joinType));
		}

		if (hasSpaceAfterFetch) {
			children.add(buildStringExpression(SPACE));
		}

		// Join association path
		if (joinAssociationPath != null) {
			children.add(joinAssociationPath);
		}
	}

	/**
	 * Returns the actual identifier found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The identifier identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return identifier;
	}

	/**
	 * Returns the identifier this expression represents.
	 *
	 * @return Either <b>INNER JOIN FETCH</b>, <b>JOIN FETCH</b>, <b>LEFT JOIN_FETCH</b> or
	 * <b>LEFT OUTER JOIN_FETCH</b>. Although it's possible to have an incomplete identifier if the
	 * query is not complete
	 */
	public String getIdentifier() {
		return getText();
	}

	/**
	 * Returns the {@link Expression} that represents the join association path expression if it was
	 * parsed.
	 *
	 * @return The expression that was parsed representing the join association path expression
	 */
	public Expression getJoinAssociationPath() {
		if (joinAssociationPath == null) {
			joinAssociationPath = buildNullExpression();
		}
		return joinAssociationPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(JoinFetchBNF.ID);
	}

	/**
	 * Determines whether the join association path expression was parsed.
	 *
	 * @return <code>true</code> if the join association path expression was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasJoinAssociationPath() {
		return joinAssociationPath != null &&
		      !joinAssociationPath.isNull();
	}

	/**
	 * Determines whether a whitespace was found after <b>FETCH</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>FETCH</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterFetch() {
		return hasSpaceAfterFetch;
	}

	/**
	 * Determines whether this {@link JoinFetch} is a left join fetch, i.e. {@link Expression#LEFT_JOIN_FETCH}
	 * or {@link Expression#LEFT_OUTER_JOIN_FETCH}.
	 *
	 * @return <code>true</code> if this {@link JoinFetch} expression is a {@link Expression#LEFT_JOIN_FETCH}
	 * or {@link Expression#LEFT_OUTER_JOIN_FETCH}; <code>false</code> otherwise
	 */
	public boolean isLeftJoinFetch() {
		String identifier = getIdentifier();
		return identifier == LEFT_JOIN_FETCH ||
		       identifier == LEFT_OUTER_JOIN_FETCH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		identifier = wordParser.moveForward(getText());

		hasSpaceAfterFetch = wordParser.skipLeadingWhitespace() > 0;

		// Parse the join association
		joinAssociationPath = parse(
			wordParser,
			getQueryBNF(JoinAssociationPathExpressionBNF.ID),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// JOIN
		writer.append(actual ? identifier : getIdentifier());

		if (hasSpaceAfterFetch) {
			writer.append(SPACE);
		}

		// Join association path
		if (joinAssociationPath != null) {
			joinAssociationPath.toParsedText(writer, actual);
		}
	}
}