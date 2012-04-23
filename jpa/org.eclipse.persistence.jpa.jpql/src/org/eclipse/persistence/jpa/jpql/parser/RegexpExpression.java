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
 * The <b>REGEXP</b> condition is used to specify a search for a pattern. It is not part of JPA
 * 2.0/2.1, only EclipseLink. It is supported by many databases (Oracle, MySQL, PostgreSQL), I
 * think part of SQL 2008, replacing <b>SIMILAR TO</b>.
 * <p>
 * The <code>string_expression</code> must have a string value. The <code>pattern_value</code> is a
 * string literal or a string-valued input parameter that is a regular expression.
 * <p>
 * <div nowrap><b>BNF:</b> <code>regexp_expression ::= string_expression REGEXP pattern_value</code>
 * <p>
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class RegexpExpression extends AbstractExpression {

	/**
	 * Determines whether a whitespace was parsed after <b>REGEXP</b>.
	 */
	private boolean hasSpaceAfterIdentifier;

	/**
	 * The {@link Expression} representing the pattern value.
	 */
	private AbstractExpression patternValue;

	/**
	 * The actual <b>REGEXP</b> identifier found in the string representation of the JPQL query.
	 */
	private String regexpIdentifier;

	/**
	 * The {@link Expression} representing the string expression.
	 */
	private AbstractExpression stringExpression;

	/**
	 * Creates a new <code>LikeExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param stringExpression The first part of this expression, which is the string expression
	 */
	public RegexpExpression(AbstractExpression parent, AbstractExpression stringExpression) {
		super(parent, REGEXP);

		if (stringExpression != null) {
			this.stringExpression = stringExpression;
			this.stringExpression.setParent(this);
		}
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
		getStringExpression().accept(visitor);
		getPatternValue().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getStringExpression());
		children.add(getPatternValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// String expression
		if (stringExpression != null) {
			children.add(stringExpression);
		}

		children.add(buildStringExpression(SPACE));

		// 'REGEXP'
		children.add(buildStringExpression(REGEXP));

		if (hasSpaceAfterIdentifier) {
			children.add(buildStringExpression(SPACE));
		}

		// Pattern value
		if (patternValue != null) {
			children.add(patternValue);
		}
	}

	/**
	 * Returns the actual <b>REGEXP</b> found in the string representation of the JPQL query, which
	 * has the actual case that was used.
	 *
	 * @return The <b>REGEXP</b> identifier that was actually parsed
	 */
	public String getActualRegexpIdentifier() {
		return regexpIdentifier;
	}

	/**
	 * Returns the {@link Expression} that represents the pattern value.
	 *
	 * @return The expression that was parsed representing the pattern value
	 */
	public Expression getPatternValue() {
		if (patternValue == null) {
			patternValue = buildNullExpression();
		}
		return patternValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(RegexpExpressionBNF.ID);
	}

	/**
	 * Returns the {@link Expression} that represents the string expression.
	 *
	 * @return The expression that was parsed representing the string expression
	 */
	public Expression getStringExpression() {
		if (stringExpression == null) {
			stringExpression = buildNullExpression();
		}
		return stringExpression;
	}

	/**
	 * Determines whether the pattern value was parsed.
	 *
	 * @return <code>true</code> if the pattern value was parsed; <code>false</code> otherwise
	 */
	public boolean hasPatternValue() {
		return patternValue != null &&
		      !patternValue.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <b>REGEXP</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>REGEXP</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterIdentifier() {
		return hasSpaceAfterIdentifier;
	}

	/**
	 * Determines whether a whitespace was parsed after the string expression.
	 *
	 * @return <code>true</code> if there was a whitespace after the string expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterStringExpression() {
		return hasStringExpression();
	}

	/**
	 * Determines whether the string expression was parsed.
	 *
	 * @return <code>true</code> if the string expression was parsed; <code>false</code> otherwise
	 */
	public boolean hasStringExpression() {
		return stringExpression != null &&
		      !stringExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

		char character = word.charAt(0);

		if (getQueryBNF(PatternValueBNF.ID).handleAggregate() &&
		    (character == '+' || character == '-' || character == '*' || character == '/')) {

			return false;
		}

		return super.isParsingComplete(wordParser, word, expression) ||
		       word.equalsIgnoreCase(AND) ||
		       word.equalsIgnoreCase(OR)  ||
		       expression != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'REGEXP'
		regexpIdentifier = wordParser.moveForward(REGEXP);

		hasSpaceAfterIdentifier = wordParser.skipLeadingWhitespace() > 0;

		// Parse the pattern value
		patternValue = parse(wordParser, PatternValueBNF.ID, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// String expression
		if (stringExpression != null) {
			stringExpression.toParsedText(writer, actual);
			writer.append(SPACE);
		}

		// 'REGEXP'
		writer.append(actual ? regexpIdentifier : REGEXP);

		if (hasSpaceAfterIdentifier) {
			writer.append(SPACE);
		}

		// Pattern value
		if (patternValue != null) {
			patternValue.toParsedText(writer, actual);
		}
	}
}