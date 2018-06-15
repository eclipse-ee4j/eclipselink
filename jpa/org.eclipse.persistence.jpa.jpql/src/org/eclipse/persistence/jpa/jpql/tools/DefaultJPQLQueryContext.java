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

import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.tools.resolver.DefaultResolverBuilder;

/**
 * This context is used to store information related to the JPQL query.
 *
 * <pre><code> {@link org.eclipse.persistence.jpa.jpql.tools.spi.IQuery IQuery} externalQuery = ...;
 *
 * JPQLQueryContext context = new JPQLQueryContext(DefaultJPQLGrammar.instance());
 * context.setQuery(query);</code></pre>
 *
 * If the JPQL query is already parsed, then the context can use it and it needs to be set before
 * setting the {@link org.eclipse.persistence.jpa.jpql.tools.spi.IQuery IQuery}:
 * <pre><code> {@link org.eclipse.persistence.jpa.jpql.parser.JPQLExpression JPQLExpression} jpqlExpression = ...;
 *
 * JPQLQueryContext context = new JPQLQueryContext(DefaultJPQLGrammar.instance());
 * context.setJPQLExpression(jpqlExpression);
 * context.setQuery(query);</code></pre>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultJPQLQueryContext extends JPQLQueryContext {

    /**
     * Creates a new <code>DefaultJPQLQueryContext</code>.
     */
    public DefaultJPQLQueryContext(JPQLGrammar jpqlGrammar) {
        super(jpqlGrammar);
    }

    /**
     * Creates a new sub-<code>DefaultJPQLQueryContext</code>.
     *
     * @param parent The parent context
     * @param currentQuery The parsed tree representation of the subquery
     */
    protected DefaultJPQLQueryContext(JPQLQueryContext parent, Expression currentQuery) {
        super(parent, currentQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildJPQLQueryContext(JPQLQueryContext currentContext,
                                                     Expression currentQuery) {

        return new DefaultJPQLQueryContext(currentContext, currentQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DefaultLiteralVisitor buildLiteralVisitor() {
        return new DefaultLiteralVisitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DefaultParameterTypeVisitor buildParameterTypeVisitor() {
        return new DefaultParameterTypeVisitor(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DefaultResolverBuilder buildResolverBuilder() {
        return new DefaultResolverBuilder(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultJPQLQueryContext getParent() {
        return (DefaultJPQLQueryContext) super.getParent();
    }
}
