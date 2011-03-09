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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import java.util.List;

/**
 * This {@link Expression} represents a date or time. It supports the following identifiers:
 * <p>
 * <b>CURRENT_DATE</b>: This function returns the value of current date on the database server.
 * <p>
 * <b>CURRENT_TIME</b>: This function returns the value of current time on the database server.
 * <p>
 * <b>CURRENT_TIMESTAMP</b>: This function returns the value of current timestamp on the database
 * server.
 * <p>
 * <div nowrap><b>BNF:</b> <code>functions_returning_datetime ::= CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP</code>
 * <p>
 * The JDBC escape syntax may be used for the specification of date, time, and timestamp literals.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= {d 'yyyy-mm-dd'} | {t 'hh:mm:ss'} | {ts 'yyyy-mm-dd hh:mm:ss.f...'}</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class DateTime extends AbstractExpression {

	/**
	 * Creates a new <code>DateTime</code>.
	 *
	 * @param parent The parent of this expression
	 */
	DateTime(AbstractExpression parent) {
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {
		children.add(buildStringExpression(getText()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(FunctionsReturningDatetimeBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return super.getText();
	}

	/**
	 * Determines whether this {@link DateTime} represents the JPQL identifier
	 * {@link Expression#CURRENT_DATE CURRENT_DATE}.
	 *
	 * @return <code>true</code> if this {@link Expression} represents
	 * {@link Expression#CURRENT_DATE CURRENT_DATE}; <code>false</code> otherwise
	 */
	public boolean isCurrentDate() {
		return getText() == CURRENT_DATE;
	}

	/**
	 * Determines whether this {@link DateTime} represents the JPQL identifier
	 * {@link Expression#CURRENT_TIME CURRENT_TIME}.
	 *
	 * @return <code>true</code> if this {@link Expression} represents
	 * {@link Expression#CURRENT_TIME CURRENT_TIME}; <code>false</code> otherwise
	 */
	public boolean isCurrentTime() {
		return getText() == CURRENT_TIME;
	}

	/**
	 * Determines whether this {@link DateTime} represents the JPQL identifier
	 * {@link Expression#CURRENT_TIMESTAMP CURRENT_TIMESTAMP}.
	 *
	 * @return <code>true</code> if this {@link Expression} represents
	 * {@link Expression#CURRENT_TIMESTAMP CURRENT_TIMESTAMP}; <code>false</code>
	 * otherwise
	 */
	public boolean isCurrentTimestamp() {
		return getText() == CURRENT_TIMESTAMP;
	}

	/**
	 * Determines whether this {@link DateTime} represents the JDBC escape syntax for date, time,
	 * timestamp formats.
	 *
	 * @return <code>true</code> if this {@link Expression} represents a JDBC escape syntax;
	 * <code>false</code> otherwise
	 */
	public boolean isJDBCDate() {
		return getText().charAt(0) == LEFT_CURLY_BRACKET;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		// JDBC escape format for date/time/timestamp
		if (wordParser.startsWith(LEFT_CURLY_BRACKET)) {
			parseJDBCEscapeFormat(wordParser);
		}
		// JPQL identifiers
		else {
			String identifier = parseIdentifier(wordParser);
			setText(identifier);
			wordParser.moveForward(identifier);
		}
	}

	private String parseIdentifier(WordParser wordParser) {

		int position = wordParser.position();

		if (wordParser.character(position + 8) == 'D') {
			return CURRENT_DATE;
		}

		if (wordParser.character(position + 12) == 'S') {
			return CURRENT_TIMESTAMP;
		}

		return CURRENT_TIME;
	}

	private void parseJDBCEscapeFormat(WordParser wordParser) {

		int startIndex = wordParser.position();
		int stopIndex  = startIndex + 1;

		for (int index = startIndex + 1, length = wordParser.length(); index < length; index++) {
			char character = wordParser.character(index);

			if (character == RIGHT_CURLY_BRACKET) {
				stopIndex = index + 1;
				break;
			}

			stopIndex++;
		}

		setText(wordParser.substring(startIndex, stopIndex));
		wordParser.moveForward(stopIndex - startIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toParsedText() {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer) {
		writer.append(getText());
	}
}