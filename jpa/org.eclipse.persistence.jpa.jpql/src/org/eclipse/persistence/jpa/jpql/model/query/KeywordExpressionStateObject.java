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

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The expression representing some keywords: <code>TRUE</code>, <code>FALSE</code> or <code>NULL</code>.
 *
 * @see KeywordExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class KeywordExpressionStateObject extends SimpleStateObject {

	/**
	 * Creates a new <code>KeywordExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public KeywordExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>KeywordExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param text Either {@link org.eclipse.persistence.jpa.jpql.parser.Expression#TRUE TRUE},
	 * {@link org.eclipse.persistence.jpa.jpql.parser.Expression#FALSE FALSE} or
	 * {@link org.eclipse.persistence.jpa.jpql.parser.Expression#NULL NULL}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public KeywordExpressionStateObject(StateObject parent, String text) {
		super(parent, text);
		validateIdentifier(text);
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
	public KeywordExpression getExpression() {
		return (KeywordExpression) super.getExpression();
	}

	/**
	 * Keeps a reference of the {@link KeywordExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link KeywordExpression parsed object} representing one of the three
	 * possible keyword: <code><b>TRUE</b></code>, <code><b>FALSE</b></code> or <code><b>NULL</b></code>
	 */
	public void setExpression(KeywordExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String text) {
		validateIdentifier(text);
		super.setText(text);
	}

	protected void validateIdentifier(String text) {
		Assert.isValid(text, "Only TRUE, FALSE, and NULL are valid identifiers.", TRUE, FALSE, NULL);
	}
}