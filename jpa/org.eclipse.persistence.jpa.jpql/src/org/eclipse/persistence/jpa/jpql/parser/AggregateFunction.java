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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * In the <b>SELECT</b> clause the result of a query may be the result of an aggregate function
 * applied to a path expression. The following aggregate functions can be used in the <b>SELECT</b>
 * clause of a query: <b>AVG</b>, <b>COUNT</b>, <b>MAX</b>, <b>MIN</b>, <b>SUM</b>.
 * <p>
 * A <code>single_valued_association_field</code> is designated by the name of an association-field
 * in a one-to-one or many-to-one relationship. The type of a <code>single_valued_association_field</code>
 * and thus a <code>single_valued_association_path_expression</code> is the abstract schema type of
 * the related entity.
 * <p>
 * The argument to an aggregate function may be preceded by the keyword <b>DISTINCT</b> to specify
 * that duplicate values are to be eliminated before the aggregate function is applied. Null values
 * are eliminated before the aggregate function is applied, regardless of whether the keyword
 * <b>DISTINCT</b>
 * is specified.
 *
 * <div nowrap><b>BNF:</b> <code>aggregate_expression ::= { AVG | MAX | MIN | SUM } ([DISTINCT] state_field_path_expression) |
 *                          COUNT ([DISTINCT] identification_variable |
 *                                            state_field_path_expression |
 *                                            single_valued_object_path_expression)</code><p>
 *
 * @see AvgFunction
 * @see CountFunction
 * @see MaxFunction
 * @see MinFunction
 * @see SumFunction
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AggregateFunction extends AbstractSingleEncapsulatedExpression {

	/**
	 * Determines whether the identifier <b>DISTINCT</b> was parsed.
	 */
	private boolean hasDistinct;

	/**
	 * The actual <b>DISTINCT</b> identifier found in the string representation of the JPQL query.
	 */
	private String distinctIdentifier;

	/**
	 * Returns the actual <b>DISTINCT</b> identifier found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <b>DISTINCT</b> identifier that was actually parsed, or an empty string if it was
	 * not parsed
	 */
	public String getActualDistinctIdentifier() {
		return (distinctIdentifier != null) ?distinctIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Determines whether whitespace was found after the identifier <b>DISTINCT</b>.
	 */
	private boolean hasSpaceAfterDistinct;

	/**
	 * Creates a new <code>AggregateFunction</code>.
	 *
	 * @param parent The parent of this expression
	 */
	protected AggregateFunction(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedEncapsulatedExpressionTo(List<Expression> children) {

		if (hasDistinct) {
			children.add(buildStringExpression(DISTINCT));
		}

		if (hasSpaceAfterDistinct) {
			children.add(buildStringExpression(SPACE));
		}

		super.addOrderedEncapsulatedExpressionTo(children);
	}

	/**
	 * Creates the {@link AbstractExpression} to represent the given word.
	 *
	 * @param word The word that was parsed
	 * @return The encapsulated {@link AbstractExpression}
	 */
	protected AbstractExpression buildEncapsulatedExpression(WordParser wordParser, String word) {
		return new StateFieldPathExpression(this, word);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return InternalAggregateFunctionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(AggregateExpressionBNF.ID);
	}

	/**
	 * Determines whether the <b>DISTINCT</b> identifier was specified in the query.
	 *
	 * @return <code>true</code> if the query has <b>DISTINCT</b>; <code>false</code> otherwise
	 */
	public final boolean hasDistinct() {
		return hasDistinct;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>DISTINCT</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>DISTINCT</b>; <code>false</code>
	 * otherwise
	 */
	public final boolean hasSpaceAfterDistinct() {
		return hasSpaceAfterDistinct;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void parseEncapsulatedExpression(WordParser wordParser, int whitespaceCount, boolean tolerant) {

		// Parse 'DISTINCT'
		hasDistinct = wordParser.startsWithIdentifier(DISTINCT);

		if (hasDistinct) {
			distinctIdentifier = wordParser.moveForward(DISTINCT);
			hasSpaceAfterDistinct = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the rest
		super.parseEncapsulatedExpression(wordParser, whitespaceCount, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

		if (hasDistinct) {
			writer.append(actual ? distinctIdentifier : DISTINCT);
		}

		if (hasSpaceAfterDistinct) {
			writer.append(SPACE);
		}

		super.toParsedTextEncapsulatedExpression(writer, actual);
	}
}