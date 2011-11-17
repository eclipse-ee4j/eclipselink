/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;

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
 * @see StringLiteral
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class StringLiteralStateObject extends SimpleStateObject {

	/**
	 * Creates a new <code>StringLiteralStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public StringLiteralStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>StringLiteralStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param literal The string literal
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public StringLiteralStateObject(StateObject parent, String literal) {
		super(parent, literal);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringLiteral getExpression() {
		return (StringLiteral) super.getExpression();
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
	 * @return <code>true</code> if the literal is ended by a single quote; <code>false</code> otherwise
	 */
	public boolean hasCloseQuote() {
		String text = getText();
		int length = text.length();
		return (length > 1) && ExpressionTools.isQuote(text.charAt(length - 1));
	}

	/**
	 * Keeps a reference of the {@link StringLiteral parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link StringLiteral parsed object} representing a string literal
	 */
	public void setExpression(StringLiteral expression) {
		super.setExpression(expression);
	}
}