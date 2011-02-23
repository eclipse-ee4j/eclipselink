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
 * TODO:
 * <p>
 * New to
 * <div nowrap><b>BNF:</b> <code>join_treat ::= TREAT(collection_valued_path_expression AS entity_type_literal)</code>
 * <p>
 * Example: <code>SELECT e FROM Employee e JOIN TREAT(e.projects AS LargeProject) lp WHERE lp.budget = value</code>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public final class TreatExpression extends AbstractEncapsulatedExpression {

	/**
	 *
	 */
	private AbstractExpression entityType;

	/**
	 *
	 */
	private AbstractExpression expression;

	/**
	 * Determines whether the identifier <b>AS</b> was parsed.
	 */
	private boolean hasAs;

	/**
	 * Determines whether a whitespace was parsed after <b>AS</b>.
	 */
	private boolean hasSpaceAfterAs;

	/**
	 *
	 */
	private boolean hasSpaceAfterExpression;

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
		getExpression().accept(visitor);
		getEntityType().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children) {
		children.add(getExpression());
		children.add(getEntityType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedEncapsulatedExpressionTo(List<StringExpression> children) {

		// Collection-valued path expression
		if (expression != null) {
			children.add(expression);
		}

		if (hasSpaceAfterExpression) {
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

	public Expression getEntityType() {
		if (entityType == null) {
			entityType = buildNullExpression();
		}
		return entityType;
	}

	public Expression getExpression() {
		if (expression == null) {
			expression = buildNullExpression();
		}
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
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
	 * {@inheritDoc}
	 */
	@Override
	boolean hasEncapsulatedExpression() {
		return hasExpression() || hasAs || hasEntityType();
	}

	public boolean hasEntityType() {
		return entityType != null &&
		      !entityType.isNull();
	}

	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
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

	public boolean hasSpaceAfterExpression() {
		return hasSpaceAfterExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parseEncapsulatedExpression(WordParser wordParser, boolean tolerant) {

		// Collection-valued path expression
		if (tolerant) {
			expression = parse(wordParser, queryBNF(CollectionValuedPathExpressionBNF.ID), tolerant);
		}
		else {
			expression = new CollectionValuedPathExpression(this, wordParser.word());
			expression.parse(wordParser, tolerant);
		}

		hasSpaceAfterExpression = wordParser.skipLeadingWhitespace() > 0;

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
	void toParsedTextEncapsulatedExpression(StringBuilder writer) {

		// Collection-valued path expression
		if (expression != null) {
			writer.append(expression);
		}

		if (hasSpaceAfterExpression) {
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