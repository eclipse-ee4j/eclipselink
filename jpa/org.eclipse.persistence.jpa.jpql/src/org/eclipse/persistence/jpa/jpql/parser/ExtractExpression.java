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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>EXTRACT</b> function extracts a date part from a date/time value. The part can be
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
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class ExtractExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * The actual <b>FROM</b> identifier found in the string representation of the JPQL query.
	 */
	private String fromIdentifier;

	/**
	 * Determines whether the identifier <b>FROM</b> was part of the query.
	 */
	private boolean hasFrom;

	/**
	 * Determines whether a space was parsed after the identifier <b>FROM</b>.
	 */
	private boolean hasSpaceAfterFrom;

	/**
	 * Determines whether a space was parsed after the date part.
	 */
	private boolean hasSpaceAfterPart;

	/**
	 * The part to extract from the date/time.
	 */
	private String part;

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

		if (hasPart()) {
			children.add(buildStringExpression(part));
		}

		// 'FROM'
		if (hasFrom) {
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
	public String getPart() {
		return part;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ExtractExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasEncapsulatedExpression() {
		return hasPart() || hasFrom || hasExpression();
	}

	/**
	 * Determines whether the identifier <b>FROM</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>FROM</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasFrom() {
		return hasFrom;
	}

	/**
	 * Determines whether the date part literal was parsed or not.
	 *
	 * @return <code>true</code> if the date part literal was parsed; <code>false</code> otherwise
	 */
	public boolean hasPart() {
		return ExpressionTools.stringIsNotEmpty(part);
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
	 * Determines whether a whitespace was found after the date part literal.
	 *
	 * @return <code>true</code> if there was a whitespace after the database part literal;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterPart() {
		return hasSpaceAfterPart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseEncapsulatedExpression(WordParser wordParser,
	                                           int whitespaceCount,
	                                           boolean tolerant) {

		// Parse the database type
		part = wordParser.word();

		if (isParsingComplete(wordParser, part, null)) {
			part = ExpressionTools.EMPTY_STRING;
			hasSpaceAfterPart = whitespaceCount > 0;
		}
		else {
			wordParser.moveForward(part);
			hasSpaceAfterPart = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse 'FROM'
		hasFrom = wordParser.startsWithIdentifier(FROM);

		if (hasFrom) {
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
	protected void toParsedTextEncapsulatedExpression(StringBuilder writer, boolean actual) {

		// Database part
		writer.append(part);

		if (hasSpaceAfterPart) {
			writer.append(SPACE);
		}

		// FROM
		if (hasFrom) {
			writer.append(actual ? fromIdentifier : FROM);
		}

		if (hasSpaceAfterFrom) {
			writer.append(SPACE);
		}

		// Value
		super.toParsedTextEncapsulatedExpression(writer, actual);
	}
}