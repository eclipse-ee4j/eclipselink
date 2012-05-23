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
 * The state field path expression must have a string, numeric, or enum value. The literal and/or
 * input parameter values must be like the same abstract schema type of the state field path
 * expression in type.
 * <p>
 * The results of the subquery must be like the same abstract schema type of the state field path
 * expression in type.
 * <p>
 * There must be at least one element in the comma separated list that defines the set of values for
 * the <b>IN</b> expression. If the value of a state field path expression in an <b>IN</b> or
 * <b>NOT IN</b> expression is <b>NULL</b> or unknown, the value of the expression is unknown.
 * <p>
 * JPA 1.0:
 * <div nowrap><b>BNF:</b> <code>in_expression ::= state_field_path_expression [NOT] IN(in_item {, in_item}* | subquery)</code><p>
 * JPA 2.0
 * <div nowrap><b>BNF:</b> <code>in_expression ::= {state_field_path_expression | type_discriminator} [NOT] IN { ( in_item {, in_item}* ) | (subquery) | collection_valued_input_parameter }</code><p>
 * <p>
 * <div nowrap>Example: </code><b>SELECT</b> c <b>FROM</b> Customer c <b>WHERE</b> c.home.city <b>IN</b> :city</p>
 * <p>
 * <div nowrap>Example: </code><b>SELECT</b> p <b>FROM</b> Project p <b>WHERE</b> <b>TYPE</b>(p) <b>IN</b>(LargeProject, SmallProject)</p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class InExpression extends AbstractExpression {

	/**
	 * The expression before the 'IN' identifier used for identification.
	 */
	private AbstractExpression expression;

	/**
	 * Flag used to determine if the closing parenthesis is present in the query.
	 */
	private boolean hasLeftParenthesis;

	/**
	 * Determines whether this expression is negated or not.
	 */
	private boolean hasNot;

	/**
	 * Flag used to determine if the opening parenthesis is present in the query.
	 */
	private boolean hasRightParenthesis;

	/**
	 * Flag used to determine if a space was parsed after <code>IN</code> if the left parenthesis was
	 * not parsed.
	 */
	private boolean hasSpaceAfterIn;

	/**
	 * The actual <b>IN</b> identifier found in the string representation of the JPQL query.
	 */
	private String inIdentifier;

	/**
	 * The expression within parenthesis, which can be one or many expressions.
	 */
	private AbstractExpression inItems;

	/**
	 * The actual <b>NOT</b> identifier found in the string representation of the JPQL query.
	 */
	private String notIdentifier;

	/**
	 * Determines whether what was parsed after the <code>IN</code> identifier is a single input
	 * parameter.
	 */
	private Boolean singleInputParameter;

	/**
	 * Creates a new <code>InExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The state field path expression that was parsed prior of parsing this
	 * expression
	 */
	public InExpression(AbstractExpression parent, AbstractExpression expression) {
		super(parent, IN);

		if (expression != null) {
			this.expression = expression;
			this.expression.setParent(this);
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
		getExpression().accept(visitor);
		getInItems().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getExpression());
		children.add(getInItems());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// State field path expression or type discriminator
		if (hasExpression()) {
			children.add(expression);
			children.add(buildStringExpression(SPACE));
		}

		// 'NOT'
		if (hasNot) {
			children.add(buildStringExpression(NOT));
		}

		// 'IN'
		children.add(buildStringExpression(IN));

		// '('
		if (hasLeftParenthesis) {
			children.add(buildStringExpression(LEFT_PARENTHESIS));
		}
		else if (hasSpaceAfterIn) {
			children.add(buildStringExpression(SPACE));
		}

		// In items
		if (hasInItems()) {
			children.add(inItems);
		}

		// ')'
		if (hasRightParenthesis) {
			children.add(buildStringExpression(RIGHT_PARENTHESIS));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF findQueryBNF(AbstractExpression expression) {

		if (this.expression == expression) {
			return getQueryBNF(InExpressionExpressionBNF.ID);
		}

		if (inItems.isAncestor(expression)) {
			return getQueryBNF(InExpressionItemBNF.ID);
		}

		return super.findQueryBNF(expression);
	}

	/**
	 * Returns the actual <b>IN</b> found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The <b>IN</b> identifier that was actually parsed
	 */
	public String getActualInIdentifier() {
		return inIdentifier;
	}

	/**
	 * Returns the actual <b>NOT</b> found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The <b>NOT</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualNotIdentifier() {
		return (notIdentifier != null) ? notIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the {@link Expression} that represents the state field path expression or type
	 * discriminator.
	 *
	 * @return The expression that was parsed representing the state field path expression or the
	 * type discriminator
	 */
	public Expression getExpression() {
		if (expression == null) {
			expression = buildNullExpression();
		}
		return expression;
	}

	/**
	 * Returns the unique identifier of the query BNF that describes the expression being tested by
	 * the <code>IN</code> expression.
	 *
	 * @return {@link InExpressionExpressionBNF#ID}
	 */
	public String getExpressionExpressionBNF() {
		return InExpressionExpressionBNF.ID;
	}

	/**
	 * Returns the unique identifier of the query BNF that describes the items being tested against.
	 *
	 * @return {@link InExpressionItemBNF#ID}
	 */
	public String getExpressionItemBNF() {
		return InExpressionItemBNF.ID;
	}

	/**
	 * Returns the identifier for this expression.
	 *
	 * @return Either <b>IS IN</b> or <b>IN</b>
	 */
	public String getIdentifier() {
		return hasNot ? NOT_IN : IN;
	}

	/**
	 * Returns the {@link Expression} that represents the list if items.
	 *
	 * @return The expression that was parsed representing the list of items
	 */
	public Expression getInItems() {
		if (inItems == null) {
			inItems = buildNullExpression();
		}
		return inItems;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(InExpressionBNF.ID);
	}

	/**
	 * Determines whether the state field path expression or type discriminator was parsed.
	 *
	 * @return <code>true</code> if the state field path expression or type discriminator was parsed;
	 * <code>false</code> if it was not parsed
	 */
	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether the list of items was parsed.
	 *
	 * @return <code>true</code> if at least one item was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasInItems() {
		return inItems != null &&
		      !inItems.isNull();
	}

	/**
	 * Determines whether the open parenthesis was parsed or not.
	 *
	 * @return <code>true</code> if the open parenthesis was present in the string version of the
	 * query; <code>false</code> otherwise
	 */
	public boolean hasLeftParenthesis() {
		return hasLeftParenthesis;
	}

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return hasNot;
	}

	/**
	 * Determines whether the close parenthesis was parsed or not.
	 *
	 * @return <code>true</code> if the close parenthesis was present in the string version of the
	 * query; <code>false</code> otherwise
	 */
	public boolean hasRightParenthesis() {
		return hasRightParenthesis;
	}

	/**
	 * Determines whether there was a whitespace after the <code>IN</code> identifier if the left
	 * parenthesis was not parsed.
	 *
	 * @return <code>true</code> if a whitespace was parsed after <code>IN</code> in the string
	 * version of the query; <code>false</code> otherwise
	 * @since 2.4
	 */
	public boolean hasSpaceAfterIn() {
		return hasSpaceAfterIn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(AND) ||
		       word.equalsIgnoreCase(OR)  ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * Determines whether what was parsed after the <code>IN</code> identifier is a single input
	 * parameter:
	 * <div nowrap><b>BNF:</b> <code>in_expression ::= {state_field_path_expression | type_discriminator} [NOT] IN collection_valued_input_parameter</code><p>
	 *
	 * @return <code>true</code> if what is following the <code>IN</code> identifier is a single
	 * input parameter (without the left or right parenthesis); <code>false</code> otherwise
	 * @since 2.4
	 */
	public boolean isSingleInputParameter() {

		if (singleInputParameter == null) {

			if (hasLeftParenthesis || hasRightParenthesis)  {
				singleInputParameter = Boolean.FALSE;
			}
			else {
				WordParser wordParser = new WordParser(getInItems().toActualText());
				String word = wordParser.word();
				wordParser.moveForward(word);

				singleInputParameter = (word.length() > 0) &&
				                       ExpressionTools.isParameter(word.charAt(0)) &&
				                       wordParser.isTail();
			}
		}

		return singleInputParameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'NOT'
		hasNot = wordParser.startsWithIgnoreCase('N');

		if (hasNot) {
			notIdentifier = wordParser.moveForward(NOT);
			wordParser.skipLeadingWhitespace();
		}

		// Parse 'IN'
		inIdentifier = wordParser.moveForward(IN);

		int count = wordParser.skipLeadingWhitespace();
		hasSpaceAfterIn = (count > 0);

		// Parse '('
		hasLeftParenthesis = wordParser.startsWith(LEFT_PARENTHESIS);

		if (hasLeftParenthesis) {
			wordParser.moveForward(1);
			count = wordParser.skipLeadingWhitespace();
		}

		// Parse the in items or sub-query
		inItems = parse(wordParser, InExpressionItemBNF.ID, tolerant);

		if (hasInItems()) {
			count = wordParser.skipLeadingWhitespace();
		}

		// Parse ')'
		hasRightParenthesis = wordParser.startsWith(RIGHT_PARENTHESIS);

		if (hasRightParenthesis) {
			wordParser.moveForward(1);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// State field path expression or type discriminator
		if (hasExpression()) {
			expression.toParsedText(writer, actual);
			writer.append(SPACE);
		}

		// 'NOT'
		if (hasNot) {
			writer.append(actual ? notIdentifier : NOT);
			writer.append(SPACE);
		}

		// 'IN'
		writer.append(actual ? inIdentifier : IN);

		// '('
		if (hasLeftParenthesis) {
			writer.append(LEFT_PARENTHESIS);
		}
		else if (hasSpaceAfterIn) {
			writer.append(SPACE);
		}

		// IN items
		if (hasInItems()) {
			inItems.toParsedText(writer, actual);
		}

		// ')'
		if (hasRightParenthesis) {
			writer.append(RIGHT_PARENTHESIS);
		}
	}
}