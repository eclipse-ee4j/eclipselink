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

/**
 * This {@link ExpressionVisitor} wraps another {@link ExpressionVisitor} and delegates all its
 * calls to it (the delegate).
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class ExpressionVisitorWrapper extends AnonymousExpressionVisitor {

	/**
	 * The {@link ExpressionVisitor} that will have the calls delegated from this one.
	 */
	private final ExpressionVisitor delegate;

	/**
	 * Creates a new <code>ExpressionVisitorWrapper</code>.
	 */
	@SuppressWarnings("unused")
	private ExpressionVisitorWrapper() {
		this(null);
	}

	/**
	 * Creates a new <code>ExpressionVisitorWrapper</code>.
	 *
	 * @param delegate The {@link ExpressionVisitor} that will have the calls delegated from this one
	 */
	protected ExpressionVisitorWrapper(ExpressionVisitor delegate) {
		super();

		if (delegate == null) {
			throw new NullPointerException("The delegate ExpressionVisitor cannot be null");
		}

		this.delegate = delegate;
	}

	/**
	 * Returns the delegate {@link ExpressionVisitor} that is receiving all the calls from this one.
	 *
	 * @return The delegate {@link ExpressionVisitor}
	 */
	protected ExpressionVisitor getDelegate() {
		return delegate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void visit(Expression expression) {
		expression.accept(delegate);
	}
}