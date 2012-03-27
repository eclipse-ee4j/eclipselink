/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
 * <div nowrap><b>BNF:</b> <code>expression ::= NOT conditional_primary</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class NotExpression extends AbstractExpression {

	/**
	 * The expression being negated by this expression.
	 */
	private AbstractExpression expression;

	/**
	 * Determines whether a space was parsed after <b>NOT</b>.
	 */
	private boolean hasSpaceAfterNot;

	/**
	 * The actual <b></b> identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

	/**
	 * The BNF coming from the parent expression that was used to parse the query.
	 */
	private JPQLQueryBNF queryBNF;

	/**
	 * Creates a new <code>NotExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param queryBNF The BNF coming from the parent expression that was used to parse the query
	 */
	public NotExpression(AbstractExpression parent, JPQLQueryBNF queryBNF) {
		super(parent, NOT);
		this.queryBNF = queryBNF;
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

		children.add(buildStringExpression(NOT));

		if (hasSpaceAfterNot) {
			children.add(buildStringExpression(SPACE));
		}

		if (expression != null) {
			children.add(expression);
		}
	}

	/**
	 * Returns the actual <b>NOT</b> found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The <b>NOT</b> identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return identifier;
	}

	/**
	 * Returns the {@link Expression} representing the expression that is negated.
	 *
	 * @return The expression representing the expression that is negated
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
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF;
	}

	/**
	 * Determines whether the expression to negate was parsed.
	 *
	 * @return <code>true</code> if the expression to negate was parsed; <code>false</code> if it was
	 * not parsed
	 */
	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <b>NOT</b>.
	 *
	 * @return <code>true</code> if a whitespace was parsed after the identifier <b>NOT</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterNot() {
		return hasSpaceAfterNot;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {
		identifier = wordParser.moveForward(NOT);
		hasSpaceAfterNot = wordParser.skipLeadingWhitespace() > 0;
		expression = parse(wordParser, queryBNF.getId(), tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// NOT
		writer.append(actual ? identifier : getText());

		if (hasSpaceAfterNot) {
			writer.append(SPACE);
		}

		// Expression
		if (expression != null) {
			expression.toParsedText(writer, actual);
		}
	}
}