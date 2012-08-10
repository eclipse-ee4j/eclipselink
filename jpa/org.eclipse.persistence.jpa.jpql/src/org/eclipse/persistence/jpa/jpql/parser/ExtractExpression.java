/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 * The <b>EXTRACT</b> function extracts a date part from a date/time value. The date part can be
 * <code>YEAR</code>, <code>MONTH</code>, <code>DAY</code>, <code>HOUR</code>, <code>MINUTE</code>,
 * <code>SECOND</code>. Some databases may support other parts.
 * <p>
 * <div nowrap><b>BNF:</b> <code>extract_expression ::= EXTRACT(date_part_literal [FROM] scalar_expression)</code>
 * <p>
 * date_part_literal ::= { MICROSECOND | SECOND | MINUTE | HOUR | DAY | WEEK | MONTH | QUARTER |
 *                         YEAR | SECOND_MICROSECOND | MINUTE_MICROSECOND | MINUTE_SECOND |
 *                         HOUR_MICROSECOND | HOUR_SECOND | HOUR_MINUTE | DAY_MICROSECOND |
 *                         DAY_SECOND | DAY_MINUTE | DAY_HOUR | YEAR_MONTH, etc }
 * <p>
 *
 * @version 2.5
 * @since 2.4
 * @author James Sutherland
 */
public final class ExtractExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * The date part to extract from the date/time.
	 */
	private String datePart;

	/**
	 * The actual <b>FROM</b> identifier found in the string representation of the JPQL query.
	 */
	private String fromIdentifier;

	/**
	 * Determines whether a space was parsed after the date part.
	 */
	private boolean hasSpaceAfterDatePart;

	/**
	 * Determines whether a space was parsed after the identifier <b>FROM</b>.
	 */
	private boolean hasSpaceAfterFrom;

	/**
	 * Creates a new <code>ExtractExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public ExtractExpression(AbstractExpression parent) {
		super(parent);
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
	@Override
	protected void addOrderedEncapsulatedExpressionTo(List<Expression> children) {

		// Date part
		if (hasDatePart()) {
			children.add(buildStringExpression(datePart));
		}

		if (hasSpaceAfterDatePart) {
			children.add(buildStringExpression(SPACE));
		}

		// 'FROM'
		if (fromIdentifier != null) {
			children.add(buildStringExpression(FROM));
		}

		if (hasSpaceAfterFrom) {
			children.add(buildStringExpression(SPACE));
		}

		// Value
		super.addOrderedEncapsulatedExpressionTo(children);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return ScalarExpressionBNF.ID;
	}

	/**
	 * Returns the actual <b>FROM</b> identifier found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <b>FROM</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualFromIdentifier() {
		return (fromIdentifier != null) ? fromIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the date part that was parsed, it is used to extract a single part of a date/time,
	 * such as year, month, day, your, etc.
	 *
	 * @return The part of the date/time to retrieve
	 */
	public String getDatePart() {
		return datePart;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ExtractExpressionBNF.ID);
	}

	/**
	 * Determines whether the date part literal was parsed or not.
	 *
	 * @return <code>true</code> if the date part literal was parsed; <code>false</code> otherwise
	 */
	public boolean hasDatePart() {
		return ExpressionTools.stringIsNotEmpty(datePart);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasEncapsulatedExpression() {
		return hasDatePart() || (fromIdentifier != null) || hasExpression();
	}

	/**
	 * Determines whether the identifier <b>FROM</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>FROM</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasFrom() {
		return fromIdentifier != null;
	}

	/**
	 * Determines whether a whitespace was found after the date part literal.
	 *
	 * @return <code>true</code> if there was a whitespace after the date part literal;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterDatePart() {
		return hasSpaceAfterDatePart;
	}

	/**
	 * Determines whether a whitespace was found after <b>FROM</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>FROM</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterFrom() {
		return hasSpaceAfterFrom;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(FROM) ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseEncapsulatedExpression(WordParser wordParser,
	                                           int whitespaceCount,
	                                           boolean tolerant) {

		if (wordParser.isTail()) {
			datePart = ExpressionTools.EMPTY_STRING;
			return;
		}

		// Parse the date part
		datePart = wordParser.word();

		// Invalid date part, parse it as the scalar expression
		if (tolerant &&
		    (isParsingComplete(wordParser, datePart, null) ||
		     datePart.indexOf(DOT) > -1 ||
		     datePart.charAt(0) == SINGLE_QUOTE ||
		     datePart.charAt(0) == DOUBLE_QUOTE ||
		     Character.isDigit(datePart.charAt(0)))) {

			datePart = ExpressionTools.EMPTY_STRING;
			hasSpaceAfterDatePart = whitespaceCount > 0;
		}
		else {
			wordParser.moveForward(datePart);
			hasSpaceAfterDatePart = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse 'FROM'
		if (wordParser.startsWithIdentifier(FROM)) {
			fromIdentifier = wordParser.moveForward(FROM);
			hasSpaceAfterFrom = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the value
		super.parseEncapsulatedExpression(wordParser, whitespaceCount, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		return EXTRACT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeEncapsulatedExpression() {
		super.removeEncapsulatedExpression();
		datePart              = ExpressionTools.EMPTY_STRING;
		fromIdentifier        = null;
		hasSpaceAfterFrom     = false;
		hasSpaceAfterDatePart = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

		// Date part
		writer.append(datePart);

		if (hasSpaceAfterDatePart) {
			writer.append(SPACE);
		}

		// FROM
		if (fromIdentifier != null) {
			writer.append(actual ? fromIdentifier : FROM);
		}

		if (hasSpaceAfterFrom) {
			writer.append(SPACE);
		}

		// Value
		super.toParsedTextEncapsulatedExpression(writer, actual);
	}
}