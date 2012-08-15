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
 * This expression tests whether the designated value is a member of the collection specified by the
 * collection-valued path expression. If the collection-valued path expression designates an empty
 * collection, the value of the <b>MEMBER OF</b> expression is <b>FALSE</b> and the value of the
 * <b>NOT MEMBER OF</b> expression is <b>TRUE</b>. Otherwise, if the value of the collection-valued
 * path expression or single-valued association-field path expression in the collection member
 * expression is <b>NULL</b> or unknown, the value of the collection member expression is unknown.
 * <p>
 * <div nowrap><b>BNF:</b> <code>collection_member_expression ::= entity_or_value_expression [NOT] MEMBER [OF] collection_valued_path_expression</code><p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class CollectionMemberExpression extends AbstractExpression {

	/**
	 * The {@link Expression} representing the collection-valued path expression.
	 */
	private AbstractExpression collectionValuedPathExpression;

	/**
	 * The {@link Expression} representing the entity expression.
	 */
	private AbstractExpression entityExpression;

	/**
	 * Determines whether a whitespace was parsed after <b>MEMBER</b>.
	 */
	private boolean hasSpaceAfterMember;

	/**
	 * Determines whether a whitespace was parsed after <b>OF</b>.
	 */
	private boolean hasSpaceAfterOf;

	/**
	 * The actual <b>MEMBER</b> identifier found in the string representation of the JPQL query.
	 */
	private String memberIdentifier;

	/**
	 * The actual <b>NOT</b> identifier found in the string representation of the JPQL query.
	 */
	private String notIdentifier;

	/**
	 * The actual <b>OF</b> identifier found in the string representation of the JPQL query.
	 */
	private String ofIdentifier;

	/**
	 * Creates a new <code>CollectionMemberExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The entity expression that was parsed before parsing this one
	 */
	public CollectionMemberExpression(AbstractExpression parent, AbstractExpression expression) {
		super(parent);

		if (expression != null) {
			this.entityExpression = expression;
			this.entityExpression.setParent(this);
		}
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
		getEntityExpression().accept(visitor);
		getCollectionValuedPathExpression().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getEntityExpression());
		children.add(getCollectionValuedPathExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// Entity expression
		if (entityExpression != null) {
			children.add(entityExpression);
		}

		// 'NOT'
		if (notIdentifier != null) {
			if (hasEntityExpression()) {
				children.add(buildStringExpression(SPACE));
			}

			children.add(buildStringExpression(NOT));
		}

		if ((notIdentifier != null) || hasEntityExpression()) {
			children.add(buildStringExpression(SPACE));
		}

		// 'MEMBER'
		children.add(buildStringExpression(MEMBER));

		if (hasSpaceAfterMember) {
			children.add(buildStringExpression(SPACE));
		}

		// 'OF'
		if (ofIdentifier != null) {
			children.add(buildStringExpression(OF));
		}

		if (hasSpaceAfterOf) {
			children.add(buildStringExpression(SPACE));
		}

		// Collection-valued path expression
		if (collectionValuedPathExpression != null) {
			children.add(collectionValuedPathExpression);
		}
	}

	/**
	 * Returns the actual <b>MEMBER</b> identifier found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <b>MEMBER</b> identifier that was actually parsed
	 */
	public String getActualMemberIdentifier() {
		return memberIdentifier;
	}

	/**
	 * Returns the actual <b>NOT</b> identifier found in the string representation of the JPQL query,
	 * which has the actual case that was used.
	 *
	 * @return The <b>NOT</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualNotIdentifier() {
		return (notIdentifier != null) ? notIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the actual <b>OF</b> identifier found in the string representation of the JPQL query,
	 * which has the actual case that was used.
	 *
	 * @return The <b>OF</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualOfIdentifier() {
		return (ofIdentifier != null) ? ofIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the {@link Expression} representing the collection-valued path expression.
	 *
	 * @return The expression that was parsed representing the collection valued path expression
	 */
	public Expression getCollectionValuedPathExpression() {
		if (collectionValuedPathExpression == null) {
			collectionValuedPathExpression = buildNullExpression();
		}
		return collectionValuedPathExpression;
	}

	/**
	 * Returns the {@link Expression} representing the entity expression.
	 *
	 * @return The expression that was parsed representing the entity expression
	 */
	public Expression getEntityExpression() {
		if (entityExpression == null) {
			entityExpression = buildNullExpression();
		}
		return entityExpression;
	}

	/**
	 * Returns the identifier for this expression that may include <b>NOT</b> and <b>OF</b> if it was parsed.
	 *
	 * @return Either <b>MEMBER</b>, <b>NOT MEMBER</b>, <b>NOT MEMBER OF</b> or <b>MEMBER OF</b>
	 */
	public String getIdentifier() {

		if ((notIdentifier != null) && (ofIdentifier != null)) {
			return NOT_MEMBER_OF;
		}

		if (notIdentifier != null) {
			return NOT_MEMBER;
		}

		if (ofIdentifier != null) {
			return MEMBER_OF;
		}

		return MEMBER;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(CollectionMemberExpressionBNF.ID);
	}

	/**
	 * Determines whether the collection-valued path expression was parsed.
	 *
	 * @return <code>true</code> if the collection-valued path expression was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasCollectionValuedPathExpression() {
		return collectionValuedPathExpression != null &&
		      !collectionValuedPathExpression.isNull();
	}

	/**
	 * Determines whether the entity expression was parsed.
	 *
	 * @return <code>true</code> if the entity expression was parsed; <code>false</code> otherwise
	 */
	public boolean hasEntityExpression() {
		return entityExpression != null &&
		      !entityExpression.isNull();
	}

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return notIdentifier != null;
	}

	/**
	 * Determines whether the identifier <b>OF</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>OF</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasOf() {
		return ofIdentifier != null;
	}

	/**
	 * Determines whether a whitespace was found after <b>MEMBER</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>MEMBER</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterMember() {
		return hasSpaceAfterMember;
	}

	/**
	 * Determines whether a whitespace was found after <b>OF</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>OF</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterOf() {
		return hasSpaceAfterOf;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'NOT'
		if (wordParser.startsWithIgnoreCase('N')) {
			notIdentifier = wordParser.moveForward(NOT);
			wordParser.skipLeadingWhitespace();
		}

		// Parse 'MEMBER'
		memberIdentifier = wordParser.moveForward(MEMBER);

		hasSpaceAfterMember = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'OF'
		if (wordParser.startsWithIdentifier(OF)) {
			ofIdentifier = wordParser.moveForward(OF);
			hasSpaceAfterOf = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the collection-valued path expression
		collectionValuedPathExpression = parse(
			wordParser,
			CollectionValuedPathExpressionBNF.ID,
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Entity expression
		if (entityExpression != null) {
			entityExpression.toParsedText(writer, actual);
		}

		// 'NOT'
		if (notIdentifier != null) {
			if (hasEntityExpression()) {
				writer.append(SPACE);
			}

			writer.append(actual ? notIdentifier : NOT);
		}

		if ((notIdentifier != null) || hasEntityExpression()) {
			writer.append(SPACE);
		}

		// 'MEMBER'
		writer.append(actual ? memberIdentifier : MEMBER);

		if (hasSpaceAfterMember) {
			writer.append(SPACE);
		}

		// 'OF'
		if (ofIdentifier != null) {
			writer.append(actual ? ofIdentifier : OF);
		}

		if (hasSpaceAfterOf) {
			writer.append(SPACE);
		}

		// Collection-valued path expression
		if (collectionValuedPathExpression != null) {
			collectionValuedPathExpression.toParsedText(writer, actual);
		}
	}
}