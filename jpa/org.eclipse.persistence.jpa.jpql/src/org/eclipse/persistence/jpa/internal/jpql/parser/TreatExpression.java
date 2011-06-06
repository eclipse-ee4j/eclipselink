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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * Returns an expression that allows to treat its base as if it were a subclass of the class
 * returned by the base.
 * <p>
 * New to
 * <div nowrap><b>BNF:</b> <code>join_treat ::= TREAT(collection_valued_path_expression AS entity_type_literal)</code>
 * <p>
 * Example: <code>SELECT e FROM Employee e JOIN TREAT(e.projects AS LargeProject) lp WHERE lp.budget = value</code>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class TreatExpression extends AbstractEncapsulatedExpression {

	/**
	 * The {@link Expression} that represents the collection-valued path expression.
	 */
	private AbstractExpression collectionValuedPathExpression;

	/**
	 * The entity type used to downcast the type of the elements in the collection.
	 */
	private AbstractExpression entityType;

	/**
	 * Determines whether the identifier <b>AS</b> was parsed.
	 */
	private boolean hasAs;

	/**
	 * Determines whether a whitespace was parsed after <b>AS</b>.
	 */
	private boolean hasSpaceAfterAs;

	/**
	 * Determines whether a whitespace was parsed after the collection-valued path expression.
	 */
	private boolean hasSpaceAfterCollectionValuedPathExpression;

	/**
	 * Creates a new <code>TreatExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	TreatExpression(AbstractExpression parent) {
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
		getCollectionValuedPathExpression().accept(visitor);
		getEntityType().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children) {
		children.add(getCollectionValuedPathExpression());
		children.add(getEntityType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedEncapsulatedExpressionTo(List<StringExpression> children) {

		// Collection-valued path expression
		if (collectionValuedPathExpression != null) {
			children.add(collectionValuedPathExpression);
		}

		if (hasSpaceAfterCollectionValuedPathExpression) {
			children.add(buildStringExpression(SPACE));
		}

		// AS
		if (hasAs) {
			children.add(buildStringExpression(AS));
		}

		if (hasSpaceAfterAs) {
			children.add(buildStringExpression(SPACE));
		}

		// Entity type
		if (entityType != null) {
			children.add(entityType);
		}
	}

	/**
	 * Returns the {@link Expression} that represents the collection-valued path expression.
	 *
	 * @return The expression that represents the collection-valued path expression
	 */
	public Expression getCollectionValuedPathExpression() {
		if (collectionValuedPathExpression == null) {
			collectionValuedPathExpression = buildNullExpression();
		}
		return collectionValuedPathExpression;
	}

	/**
	 * Returns the {@link Expression} that represents the entity type that will be used to downcast
	 * the type of the elements in the collection.
	 *
	 * @return The expression representing the entity type
	 */
	public Expression getEntityType() {
		if (entityType == null) {
			entityType = buildNullExpression();
		}
		return entityType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(TreatExpressionBNF.ID);
	}

	/**
	 * Determines whether the identifier <b>AS</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>AS</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasAs() {
		return hasAs;
	}

	/**
	 * Determines whether the collection-valued path expression of the query was parsed.
	 *
	 * @return <code>true</code> if the collection-valued path expression was parsed;
	 * <code>false</code> if nothing was parsed
	 */
	public boolean hasCollectionValuedPathExpression() {
		return collectionValuedPathExpression != null &&
		      !collectionValuedPathExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasEncapsulatedExpression() {
		return hasCollectionValuedPathExpression() || hasAs || hasEntityType();
	}

	/**
	 * Determines whether the entity type was parsed.
	 *
	 * @return <code>true</code> if the entity type was parsed; <code>false</code> if nothing was
	 * parsed
	 */
	public boolean hasEntityType() {
		return entityType != null &&
		      !entityType.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <b>AS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AS</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterAs() {
		return hasSpaceAfterAs;
	}

	/**
	 * Determines whether a whitespace was parsed after the collection-valued path expression.
	 *
	 * @return <code>true</code> if a whitespace was parsed after the collection-valued path
	 * expression; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterCollectionValuedPathExpression() {
		return hasSpaceAfterCollectionValuedPathExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant) {

		// Collection-valued path expression
		if (tolerant) {
			collectionValuedPathExpression = parse(
				wordParser,
				queryBNF(CollectionValuedPathExpressionBNF.ID),
				tolerant
			);
		}
		else {
			collectionValuedPathExpression = new CollectionValuedPathExpression(this, wordParser.word());
			collectionValuedPathExpression.parse(wordParser, tolerant);
		}

		hasSpaceAfterCollectionValuedPathExpression = wordParser.skipLeadingWhitespace() > 0;

		// AS
		hasAs = wordParser.startsWithIdentifier(AS);

		if (hasAs) {
			wordParser.moveForward(AS);
			hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
		}

		// Entity type
		if (tolerant) {
			entityType = parse(wordParser, queryBNF(EntityTypeLiteralBNF.ID), tolerant);
		}
		else {
			entityType = new EntityTypeLiteral(this, wordParser.word());
			entityType.parse(wordParser, tolerant);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser) {
		return TREAT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean includeVirtual) {

		// Collection-valued path expression
		if (collectionValuedPathExpression != null) {
			writer.append(collectionValuedPathExpression);
		}

		if (hasSpaceAfterCollectionValuedPathExpression) {
			writer.append(SPACE);
		}

		// AS
		if (hasAs) {
			writer.append(AS);
		}

		if (hasSpaceAfterAs) {
			writer.append(SPACE);
		}

		// Entity type
		if (entityType != null) {
			writer.append(entityType);
		}
	}
}