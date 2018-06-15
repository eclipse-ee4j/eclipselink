/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
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
     * @exception NullPointerException The delegate {@link ExpressionVisitor} cannot be null
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
