/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.EclipseLinkLiteralVisitor;
import org.eclipse.persistence.jpa.jpql.LiteralVisitor;
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
 * The default implementation of {@link BasicStateObjectBuilder}, which provides support based on
 * the JPQL grammar defined in the Java Persistence functional specification and for the additional
 * support provided by EclipseLink.
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
public class EclipseLinkStateObjectBuilder extends BasicStateObjectBuilder
                                           implements EclipseLinkExpressionVisitor {

    /**
     * Creates a new <code>EclipseLinkStateObjectBuilder</code>.
     */
    public EclipseLinkStateObjectBuilder() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LiteralVisitor buildLiteralVisitor() {
        return new EclipseLinkLiteralVisitor();
    }

    /**
     * {@inheritDoc}
     */
    public void visit(AsOfClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    public void visit(CastExpression expression) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void visit(ConnectByClause expression) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void visit(DatabaseType expression) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void visit(ExtractExpression expression) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void visit(HierarchicalQueryClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    public void visit(OrderSiblingsByClause expression) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void visit(RegexpExpression expression) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void visit(StartWithClause expression) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void visit(TableExpression expression) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void visit(TableVariableDeclaration expression) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void visit(UnionClause expression) {
        // TODO
    }
}
