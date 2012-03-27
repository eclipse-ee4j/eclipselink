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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * An orderby_item must be one of the following:
 * <ol>
 * <li> A {@link StateFieldPathExpression state_field_path_expression} that evaluates to an orderable
 * state field of an entity or embeddable class abstract schema type designated in the SELECT clause
 * by one of the following:
 *   <ul>
 *   <li>A general_identification_variable
 *   <li>A single_valued_object_path_expression
 *   </ul>
 * <li>A {@link StateFieldPathExpression state_field_path_expression} that evaluates to the same
 * state field of the same entity or embeddable abstract schema type as a {@link StateFieldPathExpression
 * state_field_path_expression} in the <b>SELECT</b> clause
 * <li>A {@link ResultVariable result_variable} that refers to an orderable item in the <b>SELECT</b>
 * clause for which the same {@link ResultVariable result_variable} has been specified. This may be
 * the result of an aggregate_expression, a <code>scalar_expression</code>, or a {@link
 * StateFieldPathExpression state_field_path_expression} in the <b>SELECT</b> clause.
 * </ol>
 * <p>
 * The keyword <b>ASC</b> specifies that ascending ordering be used for the associated orderby_item;
 * the keyword <b>DESC</b> specifies that descending ordering be used. Ascending ordering is the
 * default.
 * <p>
 * JPA 1.0: <div nowrap><b>BNF:</b> <code>orderby_item ::= state_field_path_expression [ ASC | DESC ]</code>
 * <p>
 * JPA 2.0 <div nowrap><b>BNF:</b> <code>orderby_item ::= state_field_path_expression | result_variable [ ASC | DESC ]</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class OrderByItem extends AbstractExpression {

	/**
	 * The {@link Expression} representing the order by expression.
	 */
	private AbstractExpression expression;

	/**
	 * Determines whether a whitespace was parsed after the order by expression.
	 */
	private boolean hasSpaceAfterExpression;

	/**
	 * The keyword <b>ASC</b> specifies that ascending ordering be used; the keyword <b>DESC</b>
	 * specifies that descending ordering be used. Ascending ordering is the default.
	 */
	private Ordering ordering;

	/**
	 * The actual ordering identifier found in the string representation of the JPQL query.
	 */
	private String orderingIdentifier;

	/**
	 * Creates a new <code>OrderByItem</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public OrderByItem(AbstractExpression parent) {
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

		// Order By expression
		if (expression != null) {
			children.add(expression);
		}

		if (hasSpaceAfterExpression) {
			children.add(buildStringExpression(SPACE));
		}

		// Ordering type
		if (ordering != Ordering.DEFAULT) {
			children.add(buildStringExpression(ordering.toString()));
		}
	}

	/**
	 * Returns the actual ordering identifier found in the string representation of the JPQL query,
	 * which has the actual case that was used.
	 *
	 * @return The ordering identifier that was actually parsed, if one was present, otherwise an
	 * empty string is returned
	 */
	public String getActualOrdering() {
		return (orderingIdentifier != null) ? orderingIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the {@link Expression} that represents the order by expression.
	 *
	 * @return The expression that was parsed representing the order by expression
	 */
	public Expression getExpression() {
		if (expression == null) {
			expression = buildNullExpression();
		}
		return expression;
	}

	/**
	 * Returns the enum constant representing the ordering type.
	 *
	 * @return The constant representing the ordering, in the case the ordering was not parsed, then
	 * {@link Ordering#DEFAULT} is returned
	 */
	public Ordering getOrdering() {
		return ordering;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(OrderByItemBNF.ID);
	}

	/**
	 * Determines whether the order by expression was parsed.
	 *
	 * @return <code>true</code> if the order by expression was parsed; <code>false</code> otherwise
	 */
	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after the order by expression.
	 *
	 * @return <code>true</code> if there was a whitespace after the order by expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterExpression() {
		return hasSpaceAfterExpression;
	}

	/**
	 * Determines whether the ordering was specified as being ascendant.
	 *
	 * @return <code>true</code> if <b>ASC</b> was parsed; <code>false</code> otherwise
	 */
	public boolean isAscending() {
		return ordering == Ordering.ASC;
	}

	/**
	 * Determines whether the ordering was not specified.
	 *
	 * @return <code>true</code> if no ordering was parsed; <code>false</code> otherwise
	 */
	public boolean isDefault() {
		return ordering == Ordering.DEFAULT;
	}

	/**
	 * Determines whether the ordering was specified as being descendant.
	 *
	 * @return <code>true</code> if <b>DESC</b> was parsed; <code>false</code> otherwise
	 */
	public boolean isDescending() {
		return ordering == Ordering.DESC;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(ASC)  ||
		       word.equalsIgnoreCase(DESC) ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse the state field path expression
		expression = parse(wordParser, InternalOrderByItemBNF.ID, tolerant);

		hasSpaceAfterExpression = wordParser.skipLeadingWhitespace() > 0;

		if (!wordParser.isTail()) {
			String word = wordParser.word();

			// Parse 'ASC'
			if (word.equalsIgnoreCase(ASC)) {
				ordering = Ordering.ASC;
				orderingIdentifier = wordParser.moveForward(ASC.length());
			}
			// Parse 'DESC'
			else if (word.equalsIgnoreCase(DESC)) {
				ordering = Ordering.DESC;
				orderingIdentifier = wordParser.moveForward(DESC.length());
			}
			else {
				ordering = Ordering.DEFAULT;
			}
		}
		else {
			ordering = Ordering.DEFAULT;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Order By expression
		if (expression != null) {
			expression.toParsedText(writer, actual);
		}

		if (hasSpaceAfterExpression) {
			writer.append(SPACE);
		}

		// Ordering type
		if (ordering != Ordering.DEFAULT) {
			writer.append(actual ? orderingIdentifier : ordering.name());
		}
	}

	/**
	 * This enumeration lists all the possible choices for ordering an item.
	 */
	public enum Ordering {

		/**
		 * The constant for 'ASC', which tells to order the items in ascending ordering.
		 */
		ASC,

		/**
		 * The constant used when the ordering is not specify, the default is ascending ordering.
		 */
		DEFAULT,

		/**
		 * The constant for 'DESC', which tells to order the items in descending ordering.
		 */
		DESC
	}
}