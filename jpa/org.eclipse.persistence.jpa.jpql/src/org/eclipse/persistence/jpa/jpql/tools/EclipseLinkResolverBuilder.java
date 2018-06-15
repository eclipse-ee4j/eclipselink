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
package org.eclipse.persistence.jpa.jpql.tools;

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
import org.eclipse.persistence.jpa.jpql.tools.resolver.ResolverBuilder;

/**
 * An implementation of a {@link ResolverBuilder} that adds support for EclipseLink extension.
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
public class EclipseLinkResolverBuilder extends ResolverBuilder
                                        implements EclipseLinkExpressionVisitor {

    /**
     * Creates a new <code>EclipseLinkResolverBuilder</code>.
     *
     * @param queryContext The context used to query information about the JPQL query
     */
    public EclipseLinkResolverBuilder(EclipseLinkJPQLQueryContext queryContext) {
        super(queryContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AsOfClause expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CastExpression expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConnectByClause expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DatabaseType expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ExtractExpression expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(HierarchicalQueryClause expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderSiblingsByClause expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(RegexpExpression expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StartWithClause expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TableExpression expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TableVariableDeclaration expression) {
        resolver = buildClassResolver(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UnionClause expression) {
        resolver = buildClassResolver(Object.class);
    }
}
