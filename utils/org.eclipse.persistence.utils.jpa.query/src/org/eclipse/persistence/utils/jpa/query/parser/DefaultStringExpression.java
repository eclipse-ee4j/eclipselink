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
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.Collections;
import java.util.ListIterator;

/**
 * The default implementation of a {@link StringExpression} that wraps a string.
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
final class DefaultStringExpression extends StringExpression {

	/**
	 * The {@link Expression} from where the text value comes.
	 */
	private final AbstractExpression owningExpression;

	/**
	 * The wrapped string value.
	 */
	private final String value;

	/**
	 * Creates a new <code>DefaultStringExpression</code>.
	 *
	 * @param owningExpression The {@link Expression} from where the text value
	 * comes
	 * @param value The string value to wrap with this {@link StringExpression}
	 */
	DefaultStringExpression(AbstractExpression owningExpression, String value) {
		super();

		this.value            = value;
		this.owningExpression = owningExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<StringExpression> orderedChildren() {
		return Collections.<StringExpression>emptyList().listIterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void populatePosition(QueryPosition queryPosition, int position) {
		queryPosition.setExpression(owningExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toParsedText() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return toParsedText();
	}
}