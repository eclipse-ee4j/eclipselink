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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * In the <b>SELECT</b> clause a constructor may be used in the <b>SELECT</b> list to return one or
 * more Java instances. The specified class is not required to be an entity or to be mapped to the
 * database. The constructor name must be fully qualified.
 * <p>
 * <div nowrap><b>BNF:</b> <code>constructor_expression ::= NEW constructor_name(constructor_item {, constructor_item}*)</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class ConstructorExpression extends AbstractExpression {

	/**
	 * The fully qualified class name of this expression.
	 */
	private String className;

	/**
	 * The list of constructor items.
	 */
	private AbstractExpression constructorItems;

	/**
	 * Flag used to determine if the closing parenthesis is present in the query.
	 */
	private boolean hasLeftParenthesis;

	/**
	 * Flag used to determine if the opening parenthesis is present in the query.
	 */
	private boolean hasRightParenthesis;

	/**
	 * When the expression is not valid and the left parenthesis is missing, then a space might have
	 * been parsed.
	 */
	private boolean hasSpaceAfterConstructorName;

	/**
	 * Determines whether there is a whitespace after the identifier <b>NEW</b>.
	 */
	private boolean hasSpaceAfterNew;

	/**
	 * The actual <b>NEW</b> identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

	/**
	 * Creates a new <code>ConstructorExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public ConstructorExpression(AbstractExpression parent) {
		super(parent, NEW);
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
		getConstructorItems().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getConstructorItems());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<StringExpression> children) {

		// 'NEW'
		children.add(buildStringExpression(NEW));

		if (hasSpaceAfterNew) {
			children.add(buildStringExpression(SPACE));
		}

		// Class name
		if (className.length() > 0) {
			children.add(buildStringExpression(className));
		}

		// '('
		if (hasLeftParenthesis) {
			children.add(buildStringExpression(LEFT_PARENTHESIS));
		}
		else if (hasSpaceAfterConstructorName) {
			children.add(buildStringExpression(SPACE));
		}

		// Constructor items
		if (hasConstructorItems()) {
			children.add(constructorItems);
		}

		// ')'
		if (hasRightParenthesis) {
			children.add(buildStringExpression(RIGHT_PARENTHESIS));
		}
	}

	/**
	 * Creates a new {@link CollectionExpression} that will wrap the single constructor item.
	 *
	 * @return The single constructor item represented by a temporary collection
	 */
	public CollectionExpression buildCollectionExpression() {

		List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
		children.add((AbstractExpression) getConstructorItems());

		List<Boolean> commas = new ArrayList<Boolean>(1);
		commas.add(Boolean.FALSE);

		List<Boolean> spaces = new ArrayList<Boolean>(1);
		spaces.add(Boolean.FALSE);

		return new CollectionExpression(this, children, commas, spaces, true);
	}

	/**
	 * Returns the actual <b>NEW</b> identifier found in the string representation of the JPQL query,
	 * which has the actual case that was used.
	 *
	 * @return The <b>NEW</b> identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return identifier;
	}

	/**
	 * Returns the fully qualified class name that will be used to retrieve the constructor.
	 *
	 * @return The fully qualified class name or an empty string if it is not defined
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns the constructor items aggregated into a single expression and separated by commas or
	 * a single expression.
	 *
	 * @return The constructor item or items or an invalid or "<code>null</code>" expression
	 */
	public Expression getConstructorItems() {
		if (constructorItems == null) {
			constructorItems = buildNullExpression();
		}
		return constructorItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ConstructorExpressionBNF.ID);
	}

	/**
	 * Determines whether the constructor items were parsed.
	 *
	 * @return <code>true</code> if the query had at least one constructor item; <code>false</code>
	 * otherwise
	 */
	public boolean hasConstructorItems() {
		return constructorItems != null &&
		      !constructorItems.isNull();
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
	 * Determines whether the close parenthesis was parsed or not.
	 *
	 * @return <code>true</code> if the close parenthesis was present in the string version of the
	 * query; <code>false</code> otherwise
	 */
	public boolean hasRightParenthesis() {
		return hasRightParenthesis;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>NEW</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>NEW</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterNew() {
		return hasSpaceAfterNew;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return wordParser.character() == RIGHT_PARENTHESIS ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'NEW'
		identifier = wordParser.moveForward(NEW);

		hasSpaceAfterNew = wordParser.skipLeadingWhitespace() > 0;

		// Parse the class name
		String className = wordParser.word();

		if (tolerant && isIdentifier(className)) {
			this.className        = ExpressionTools.EMPTY_STRING;
			this.constructorItems = buildNullExpression();
			return;
		}

		this.className = className;
		wordParser.moveForward(className);

		int count = wordParser.skipLeadingWhitespace();

		// Parse '('
		hasLeftParenthesis = wordParser.startsWith(LEFT_PARENTHESIS);

		if (hasLeftParenthesis) {
			wordParser.moveForward(1);
			count = wordParser.skipLeadingWhitespace();
		}
		else {
			hasSpaceAfterConstructorName = (count > 0);
		}

		// Parse the constructor items
		constructorItems = parse(
			wordParser,
			getQueryBNF(ConstructorItemBNF.ID),
			tolerant
		);

		if (hasConstructorItems()) {
			count = wordParser.skipLeadingWhitespace();
		}

		// Parse ')'
		hasRightParenthesis = wordParser.character() == RIGHT_PARENTHESIS;

		if (hasRightParenthesis) {
			wordParser.moveForward(1);
		}
		else {
			wordParser.moveBackward(count);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// 'NEW'
		writer.append(actual ? identifier : getText());

		if (hasSpaceAfterNew) {
			writer.append(SPACE);
		}

		// Class name
		writer.append(className);

		// '('
		if (hasLeftParenthesis) {
			writer.append(LEFT_PARENTHESIS);
		}
		else if (hasSpaceAfterConstructorName) {
			writer.append(SPACE);
		}

		// Constructor items
		if (constructorItems != null) {
			constructorItems.toParsedText(writer, actual);
		}

		// ')'
		if (hasRightParenthesis) {
			writer.append(RIGHT_PARENTHESIS);
		}
	}
}