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
 * A result variable may be used to name a select item in the query result.
 * <p>
 * <div nowrap><b>BNF:</b> <code>select_item ::= select_expression [[AS] result_variable]</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class ResultVariable extends AbstractExpression {

	/**
	 * The actual <b>AS</b> identifier found in the string representation of the JPQL query.
	 */
	private String asIdentifier;

	/**
	 * Determines whether the identifier <b>AS</b> was parsed or not.
	 */
	private boolean hasAs;

	/**
	 * Determines whether a whitespace was parsed after the identifier <b>AS</b>.
	 */
	private boolean hasSpaceAfterAs;

	/**
	 * The {@link Expression} used
	 */
	private AbstractExpression resultVariable;

	/**
	 * The {@link Expression} representing the select expression.
	 */
	private AbstractExpression selectExpression;

	/**
	 * Creates a new <code>ResultVariable</code>.
	 *
	 * @param parent The parent of this expression
	 * @param selectExpression The expression that represents the select expression, which will have
	 * a variable assigned to it
	 */
	public ResultVariable(AbstractExpression parent, AbstractExpression selectExpression) {
		super(parent);

		if (selectExpression != null) {
			this.selectExpression = selectExpression;
			this.selectExpression.setParent(this);
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
		getSelectExpression().accept(visitor);
		getResultVariable().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getSelectExpression());
		children.add(getResultVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// Select expression
		if (selectExpression != null) {
			children.add(selectExpression);
			children.add(buildStringExpression(SPACE));
		}

		// 'AS'
		if (hasAs) {
			children.add(buildStringExpression(AS));
		}

		if (hasSpaceAfterAs) {
			children.add(buildStringExpression(SPACE));
		}

		// Result variable
		if (resultVariable != null) {
			children.add(resultVariable);
		}
	}

	/**
	 * Returns the actual <b>AS</b> found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The <b>AS</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualAsIdentifier() {
		return (asIdentifier != null) ? asIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ResultVariableBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the result variable.
	 *
	 * @return The expression for the result variable
	 */
	public Expression getResultVariable() {
		if (resultVariable == null) {
			resultVariable = buildNullExpression();
		}
		return resultVariable;
	}

	/**
	 * Returns the {@link Expression} representing the select expression.
	 *
	 * @return The expression for the select expression
	 */
	public Expression getSelectExpression() {
		if (selectExpression == null) {
			selectExpression = buildNullExpression();
		}
		return selectExpression;
	}

	/**
	 * Determines whether the identifier <b>AS</b> was parsed or not.
	 *
	 * @return <code>true</code> if the identifier <b>AS</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasAs() {
		return hasAs;
	}

	/**
	 * Determines whether the result variable was parsed.
	 *
	 * @return <code>true</code> if the result variable was parsed; <code>false</code> otherwise
	 */
	public boolean hasResultVariable() {
		return resultVariable != null &&
		      !resultVariable.isNull();
	}

	/**
	 * Determines whether a select expression was defined for this result variable.
	 *
	 * @return <code>true</code> if the select expression was parsed; <code>false</code> if the
	 * result variable was parsed without one
	 */
	public boolean hasSelectExpression() {
		return selectExpression != null &&
		      !selectExpression.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after the identifier <b>AS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AS</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterAs() {
		return hasSpaceAfterAs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'AS'
		if (wordParser.startsWithIdentifier(AS)) {
			hasAs = true;
			asIdentifier = wordParser.moveForward(2);
			hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the result variable
		if (tolerant) {
			resultVariable = parse(wordParser, IdentificationVariableBNF.ID, tolerant);
		}
		else {
			resultVariable = new IdentificationVariable(this, wordParser.word());
			resultVariable.parse(wordParser, tolerant);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Select expression
		if (selectExpression != null) {
			selectExpression.toParsedText(writer, actual);
			writer.append(SPACE);
		}

		// 'AS'
		if (hasAs) {
			writer.append(actual ? asIdentifier : AS);
		}

		if (hasSpaceAfterAs) {
			writer.append(SPACE);
		}

		// Result variable
		if (resultVariable != null) {
			resultVariable.toParsedText(writer, actual);
		}
	}
}