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
package org.eclipse.persistence.jpa.jpql.tools;

import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.DefaultJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;

/**
 * This visitor traverses the JPQL parsed tree and gathers the possible proposals at a given position.
 * <p>
 * Example:
 * <pre><code> // Have the external form of an IQuery
 * {@link org.eclipse.persistence.jpa.jpql.tools.spi.IQuery IQuery} query = ...
 *
 * // Create a JPQLQueryContext
 * {@link JPQLQueryContext} context = new JPQLQueryContext();
 * context.{@link JPQLQueryContext#setQuery(org.eclipse.persistence.jpa.jpql.tools.spi.IQuery) setQuery(query)};
 *
 * // Create a map of the positions within the parsed tree
 * {@link org.eclipse.persistence.jpa.jpql.parser.QueryPosition QueryPosition} queryPosition = context.getJPQLExpression().buildPosition(query.getExpression(), position);
 *
 * // Either a real extension that adds additional support or
 * ContentAssistExtension extension = {@link ContentAssistExtension#NULL_HELPER ContentAssistExtension.NULL_HELPER};
 *
 * // Create the visitor and visit the parsed tree
 * DefaultContentAssistVisitor visitor = new DefaultContentAssistVisitor(context);
 * visitor.{@link #buildProposals(int, ContentAssistExtension) buildProposals(queryPosition.getPosition(), extension)};
 * queryPosition.getExpression().accept(visitor);
 *
 * // Retrieve the proposals
 * {@link ContentAssistProposals} proposals = visitor.getProposals();
 *
 * // Only required if the visitor is cached
 * visitor.dispose();
 *
 * // Only required if the context is cached
 * context.dispose();
 * </code></pre>
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5.1
 * @since 2.3
 * @author Pascal Filion
 */
public class DefaultContentAssistVisitor extends AbstractContentAssistVisitor {

    /**
     * Creates a new <code>DefaultContentAssistVisitor</code>.
     *
     * @param queryContext The context used to query information about the query
     * @exception NullPointerException The {@link JPQLQueryContext} cannot be <code>null</code>
     */
    public DefaultContentAssistVisitor(JPQLQueryContext queryContext) {
        super(queryContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AcceptableTypeVisitor buildAcceptableTypeVisitor() {
        return new AcceptableTypeVisitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLGrammar getLatestGrammar() {
        return DefaultJPQLGrammar.instance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isJoinFetchIdentifiable() {
        // Generic JPA does not support identifying a JOIN FETCH with an identification variable
        return false;
    }

    /**
     * The concrete instance that determines the return type of a function expression.
     */
    protected class AcceptableTypeVisitor extends AbstractContentAssistVisitor.AcceptableTypeVisitor {

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbsExpression expression) {
            type = queryContext.getType(Number.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ArithmeticFactor expression) {
            type = queryContext.getType(Number.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AvgFunction expression) {
            type = queryContext.getType(Number.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ConcatExpression expression) {
            type = queryContext.getType(CharSequence.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LengthExpression expression) {
            type = queryContext.getType(CharSequence.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LocateExpression expression) {
            // TODO: Handle the position
            type = queryContext.getType(CharSequence.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LowerExpression expression) {
            type = queryContext.getType(CharSequence.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ModExpression expression) {
            // In theory we would only allow Long and Integer
            type = queryContext.getType(Number.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SqrtExpression expression) {
            type = queryContext.getType(Number.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubstringExpression expression) {
            // TODO: Handle the position
            type = queryContext.getType(CharSequence.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SumFunction expression) {
            type = queryContext.getType(Number.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TrimExpression expression) {
            type = queryContext.getType(CharSequence.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpperExpression expression) {
            type = queryContext.getType(CharSequence.class);
        }
    }
}
