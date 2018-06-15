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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.AsOfClause;
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConnectByClause;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.HierarchicalQueryClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderSiblingsByClause;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.StartWithClause;
import org.eclipse.persistence.jpa.jpql.parser.TableExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;

/**
 * This visitor traverses an {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}
 * and retrieves the "literal" value. The literal to retrieve depends on the {@link LiteralType type}.
 * The literal is basically a string value like an identification variable name, an input parameter,
 * a path expression, an abstract schema name, etc.
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
public class EclipseLinkLiteralVisitor extends LiteralVisitor
                                       implements EclipseLinkExpressionVisitor {

    /**
     * Creates a new <code>EclipseLinkLiteralVisitor</code>.
     */
    public EclipseLinkLiteralVisitor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AsOfClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CastExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConnectByClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DatabaseType expression) {
        if (type == LiteralType.STRING_LITERAL) {
            literal = expression.getActualIdentifier();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ExtractExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(HierarchicalQueryClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderSiblingsByClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(RegexpExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StartWithClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TableExpression expression) {
        if (type == LiteralType.STRING_LITERAL) {
            expression.getExpression().accept(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TableVariableDeclaration expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UnionClause expression) {
    }
}
