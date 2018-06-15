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
 * This {@link ExpressionVisitor} traverses the entire hierarchy of the JPQL parsed tree by going
 * down into each of the children of any given {@link Expression}. It is up to the subclass to
 * complete the behavior.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractTraverseChildrenVisitor extends AnonymousExpressionVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void visit(Expression expression) {
        expression.acceptChildren(this);
    }
}
