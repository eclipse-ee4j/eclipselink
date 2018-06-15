/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link EclipseLinkExpressionVisitor} traverses up the hierarchy. It is up to the subclass to
 * complete the behavior.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractEclipseLinkTraverseParentVisitor extends AbstractTraverseParentVisitor
                                                               implements EclipseLinkExpressionVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AsOfClause expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CastExpression expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConnectByClause expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DatabaseType expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ExtractExpression expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(HierarchicalQueryClause expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderSiblingsByClause expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(RegexpExpression expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StartWithClause expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TableExpression expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TableVariableDeclaration expression) {
        visit((Expression) expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UnionClause expression) {
        visit((Expression) expression);
    }
}
