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
 * A string literal is enclosed in single quotes. For example: 'literal'. A string literal that
 * includes a single quote is represented by two single quotes. For example: 'literal''s'. String
 * literals in queries, like Java String literals, use unicode character encoding. Approximate
 * literals support the use Java floating point literal syntax as well as SQL approximate numeric
 * literal syntax. Enum literals support the use of Java enum literal syntax. The enum class name
 * must be specified. Appropriate suffixes may be used to indicate the specific type of a numeric
 * literal in accordance with the Java Language Specification. The boolean literals are <code>TRUE</code>
 * and <code>FALSE</code>. Although predefined reserved literals appear in upper case, they are case
 * insensitive.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class StringLiteral extends AbstractExpression {

	/**
	 * Creates a new <code>StringLiteral</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public StringLiteral(AbstractExpression parent) {
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
	protected void addOrderedChildrenTo(List<Expression> children) {
		children.add(buildStringExpression(getText()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(StringLiteralBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return super.getText();
	}

	/**
	 * Returns the string literal without the single quotes.
	 *
	 * @return The unquoted text
	 */
	public String getUnquotedText() {
		return ExpressionTools.unquote(getText());
	}

	/**
	 * Determines whether the closing quote was present or not.
	 *
	 * @return <code>true</code> if the literal is ended by a single quote; <code>false</code>
	 * otherwise
	 */
	public boolean hasCloseQuote() {
		String text = getText();
		int length = text.length();
		return (length > 1) && ExpressionTools.isQuote(text.charAt(length - 1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {
		String literal = ExpressionTools.parseLiteral(wordParser);
		wordParser.moveForward(literal);
		setText(literal);
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
	protected void toParsedText(StringBuilder writer, boolean actual) {
		writer.append(getText());
	}
}