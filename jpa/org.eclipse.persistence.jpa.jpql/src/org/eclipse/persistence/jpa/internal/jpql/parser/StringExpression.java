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

import java.util.ListIterator;

/**
 * A <code>StringExpression</code> is used when retrieving the deepest {@link Expression} in the
 * JPQL parsed tree. An {@link Expression} should wrap its entire content with {@link
 * StringExpression StringExpressions}, i.e. whitespace, commas, dots, etc. {@link Expression} parts
 * are already an instance of {@link StringExpression StringExpressions} so they don't need to be
 * encapsulated.
 * <p>
 * <b>Note:</b> This is not an interface because some methods should not become public on {@link
 * AbstractExpression}, they are used internally.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class StringExpression {

	/**
	 * Creates a list representing this expression and its children. In order to add every piece of
	 * the expression, {@link #addChildrenTo(Collection)} is called.
	 *
	 * @return The {@link StringExpression StringExpressions} representing this {@link Expression}
	 */
	public abstract ListIterator<StringExpression> orderedChildren();

	/**
	 * Retrieves the <code>StringExpression</code> located at the given position using the actual
	 * query, which may have extra whitespace.
	 *
	 * @param position The array has one element and is the position of the <code>StringExpression</code>
	 * to retrieve
	 * @return The <code>StringExpression</code> located at the given position
	 */
	abstract void populatePosition(QueryPosition queryPosition, int position);

	/**
	 * Generates a string representation of this {@link StringExpression}, which needs to include any
	 * characters that are considered virtual, i.e. that was parsed when the query is incomplete and
	 * is needed for functionality like content assist.
	 *
	 * @return The string representation of this {@link StringExpression}
	 */
	public abstract String toActualText();

	/**
	 * Returns a string representation of this {@link StringExpression} and its children. The
	 * expression should contain whitespace even if the beautified version would not have any. For
	 * instance, "SELECT e " should be returned where {@link Expression#toText()} would return
	 * "SELECT e".
	 *
	 * @return The string representation of this {@link StringExpression}
	 */
	public abstract String toParsedText();
}