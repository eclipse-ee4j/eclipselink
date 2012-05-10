/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObjectVisitor;

/**
 * An abstract implementation of a {@link IJPQLQueryFormatter}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class BaseJPQLQueryFormatter implements StateObjectVisitor,
                                                        IJPQLQueryFormatter {

	/**
	 * Determines how the JPQL identifiers are written out.
	 */
	protected final IdentifierStyle style;

	/**
	 * The holder of the string representation of the JPQL query.
	 */
	protected final StringBuilder writer;

	/**
	 * The constant for a comma: ','.
	 */
	protected static final String COMMA = ",";

	/**
	 * The constant for a comma followed by a space: ', '.
	 */
	protected static final String COMMA_SPACE = ", ";

	/**
	 * The constant for the left parenthesis: '('.
	 */
	protected static final String LEFT_PARENTHESIS = "(";

	/**
	 * The constant for the right parenthesis: ')'.
	 */
	protected static final String RIGHT_PARENTHESIS = ")";

	/**
	 * The constant for a space: '&nbsp;&nbsp;'.
	 */
	protected static final String SPACE = " ";

	/**
	 * Creates a new <code>BaseJPQLQueryFormatter</code>.
	 *
	 * @param style Determines how the JPQL identifiers are written out, which is used if the
	 * {@link StateObject} was modified after its creation
	 * @exception NullPointerException The {@link IdentifierStyle} cannot be <code>null</code>
	 */
	protected BaseJPQLQueryFormatter(IdentifierStyle style) {

		super();

		Assert.isNotNull(style, "The IdentifierStyle cannot be null");

		this.style  = style;
		this.writer = new StringBuilder();
	}

	/**
	 * Formats the given JPQL identifier, if it needs to be decorated with more information. Which
	 * depends on how the string is created.
	 *
	 * @param identifier JPQL identifier to format
	 * @return By default the given identifier is returned
	 */
	protected String formatIdentifier(String identifier) {
		return style.formatIdentifier(identifier);
	}

	/**
	 * Returns the style to use when formatting the JPQL identifiers.
	 *
	 * @return One of the possible ways to format the JPQL identifiers
	 */
	public IdentifierStyle getIdentifierStyle() {
		return style;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return writer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString(StateObject stateObject) {
		writer.delete(0, writer.length());
		stateObject.accept(this);
		return writer.toString();
	}

	/**
	 * Visits the given {@link StateObject} and prevents its decorator to be called, which will
	 * prevent any possible recursion when the decorator is outputting the information.
	 *
	 * @param stateObject The decorated {@link stateObject} to traverse without going through the
	 * decorator
	 */
	protected void toText(StateObject stateObject) {

		if (stateObject.isDecorated()) {

			StateObject decorator = stateObject.getDecorator();
			stateObject.decorate(null);

			try {
				decorator.accept(this);
			}
			finally {
				stateObject.decorate(decorator);
			}
		}
		else {
			stateObject.accept(this);
		}
	}
}