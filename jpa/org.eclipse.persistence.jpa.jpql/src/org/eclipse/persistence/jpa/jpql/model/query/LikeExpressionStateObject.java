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

import java.io.IOException;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>LIKE</b></code> condition is used to specify a search for a pattern.
 * <p>
 * The <code>string_expression</code> must have a string value. The <code>pattern_value</code> is a
 * string literal or a string-valued input parameter in which an underscore (_) stands for any
 * single character, a percent (%) character stands for any sequence of characters (including the
 * empty sequence), and all other characters stand for themselves. The optional <code>escape_character</code>
 * is a single-character string literal or a character-valued input parameter (i.e., char or
 * Character) and is used to escape the special meaning of the underscore and percent characters in
 * <code>pattern_value</code>.
 * <p>
 * <div nowrap><b>BNF:</b> <code>like_expression ::= string_expression [NOT] LIKE pattern_value [ESCAPE escape_character]</code><p>
 *
 * @see LikeExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class LikeExpressionStateObject extends AbstractStateObject {

	/**
	 *
	 */
	private StringLiteralStateObject escapeCharacter;

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is part of the expression or not.
	 */
	private boolean not;

	/**
	 *
	 */
	private StateObject patternValue;

	/**
	 *
	 */
	private StateObject stringStateObject;

	/**
	 * Notifies the escape character property has changed.
	 */
	public static final String ESCAPE_CHARACTER_PROPERTY = "escapeCharacter";

	/**
	 * Notifies the visibility of the <code><b>NOT</b></code> identifier has changed.
	 */
	public static String NOT_PROPERTY = "not";

	/**
	 * Notifies the pattern value property has changed.
	 */
	public static final String PATTERN_VALUE_PROPERTY = "patternValue";

	/**
	 * Notifies the string state object property has changed.
	 */
	public static final String STRING_STATE_OBJECT_PROPERTY = "stringStateObject";

	/**
	 * Creates a new <code>LikeExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public LikeExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>LikeExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stringStateObject The {@link StateObject} representing the string expression
	 * @param patternValue
	 * @param escapeCharacter
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public LikeExpressionStateObject(StateObject parent,
	                                 StateObject stringStateObject) {

		this(parent, stringStateObject, false, null, null);
	}

	/**
	 * Creates a new <code>LikeExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stringStateObject The {@link StateObject} representing the string expression
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param patternValue
	 * @param escapeCharacter
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public LikeExpressionStateObject(StateObject parent,
	                                 StateObject stringStateObject,
	                                 boolean not,
	                                 StateObject patternValue,
	                                 String escapeCharacter) {

		super(parent);
		this.stringStateObject = parent(stringStateObject);
		this.not               = not;
		this.patternValue      = parent(patternValue);
		this.escapeCharacter.setTextInternally(escapeCharacter);
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
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		if (stringStateObject != null) {
			children.add(stringStateObject);
		}
		if (patternValue != null) {
			children.add(patternValue);
		}
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public LikeExpressionStateObject addNot() {
		if (!not) {
			setNot(true);
		}
		return this;
	}

	public String getEscapeCharacter() {
		return escapeCharacter.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LikeExpression getExpression() {
		return (LikeExpression) super.getExpression();
	}

	public StateObject getPatternValue() {
		return patternValue;
	}

	public StateObject getStringStateObject() {
		return stringStateObject;
	}

	public boolean hasEscapeCharacter() {
		return escapeCharacter.hasText();
	}

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is used or not.
	 *
	 * @return <code>true</code> if the <code><b>NOT</b></code> identifier is part of the expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return not;
	}

	public boolean hasPatternValue() {
		return patternValue != null;
	}

	public boolean hasStringStateObject() {
		return stringStateObject != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		escapeCharacter = new StringLiteralStateObject(this);
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is not specified.
	 */
	public void removeNot() {
		if (not) {
			setNot(false);
		}
	}

	/**
	 *
	 */
	public void setEscapeCharacter(String escapeCharacter) {
		String oldEscapeCharacter = getEscapeCharacter();
		this.escapeCharacter.setText(escapeCharacter);
		firePropertyChanged(ESCAPE_CHARACTER_PROPERTY, oldEscapeCharacter, escapeCharacter);
	}

	/**
	 * Keeps a reference of the {@link LikeExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link LikeExpression parsed object} representing a <code><b>LIKE</b></code>
	 * expression
	 */
	public void setExpression(LikeExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets whether the <code><b>NOT</b></code> identifier should be part of the expression or not.
	 *
	 * @param not <code>true</code> if the <code><b>NOT</b></code> identifier should be part of the
	 * expression; <code>false</code> otherwise
	 */
	public void setNot(boolean not) {
		boolean oldNot = this.not;
		this.not = not;
		firePropertyChanged(NOT_PROPERTY, oldNot, not);
	}

	public void setPatternValue(StateObject patternValue) {
		StateObject oldPatternValue = this.patternValue;
		this.patternValue = parent(patternValue);
		firePropertyChanged(PATTERN_VALUE_PROPERTY, oldPatternValue, patternValue);
	}

	public void setStringStateObject(StateObject stringStateObject) {
		StateObject oldStringStateObject = this.stringStateObject;
		this.stringStateObject = parent(stringStateObject);
		firePropertyChanged(STRING_STATE_OBJECT_PROPERTY, oldStringStateObject, stringStateObject);
	}

	/**
	 * Changes the visibility state of the <code><b>NOT</b></code> identifier.
	 */
	public void toggleNot() {
		setNot(!not);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		if (stringStateObject != null) {
			stringStateObject.toString(writer);
			writer.append(SPACE);
		}

		writer.append(not ? NOT_LIKE : LIKE);

		if (patternValue != null) {
			writer.append(SPACE);
			patternValue.toString(writer);
		}

		if (escapeCharacter != null) {
			writer.append(SPACE);
			writer.append(ESCAPE);
			writer.append(SPACE);
			escapeCharacter.toTextInternal(writer);
		}
	}
}