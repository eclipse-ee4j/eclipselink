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
package org.eclipse.persistence.jpa.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.parser.*;
import org.eclipse.persistence.jpa.jpql.tools.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;

/**
 * An abstract implementation of {@link IJPQLQueryBuilder} that parses a JPQL query or any JPQL
 * fragments and creates the {@link StateObject} representation by delegating the creation to an
 * instance of {@link BasicStateObjectBuilder}.
 *
 * @see DefaultJPQLQueryBuilder
 * @see EclipseLinkJPQLQueryBuilder
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractJPQLQueryBuilder implements IJPQLQueryBuilder {

    /**
     * Keeps a reference for future use.
     */
    private BasicStateObjectBuilder builder;

    /**
     * Creates a new <code>AbstractJPQLQueryBuilder</code>.
     */
    protected AbstractJPQLQueryBuilder() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICaseExpressionStateObjectBuilder buildCaseExpressionStateObjectBuilder(StateObject parent) {
        return new DefaultCaseExpressionStateObjectBuilder(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryStateObject buildStateObject(IManagedTypeProvider provider,
                                                 CharSequence jpqlQuery,
                                                 boolean tolerant) {

        return buildStateObject(provider, jpqlQuery, JPQLStatementBNF.ID, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryStateObject buildStateObject(IManagedTypeProvider provider,
                                                 CharSequence jpqlQuery,
                                                 String queryBNFId,
                                                 boolean tolerant) {

        // First parse the JPQL query
        JPQLExpression jpqlExpression = parse(jpqlQuery, getGrammar(), tolerant);

        // Now visit the parsed expression and create the state object hierarchy
        BasicStateObjectBuilder builder = getStateObjectBuilder();

        try {
            builder.jpqlQueryBuilder    = this;
            builder.managedTypeProvider = provider;

            jpqlExpression.accept(wrap(builder));

            return builder.parent;
        }
        finally {
            builder.jpqlQueryBuilder    = null;
            builder.managedTypeProvider = null;
            builder.parent              = null;
            builder.stateObject         = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StateObject buildStateObject(StateObject parent,
                                        CharSequence jpqlFragment,
                                        String queryBNFId) {

        // First parse the JPQL fragment
        JPQLExpression jpqlExpression = parse(jpqlFragment, parent.getGrammar(), queryBNFId);

        // We keep track of the old stuff because during the creation of a JPQLQueryStateObject,
        // it is possible the state model uses this builder to create some objects
        BasicStateObjectBuilder builder  = getStateObjectBuilder();
        IJPQLQueryBuilder oldBuilder     = builder.jpqlQueryBuilder;
        IManagedTypeProvider oldProvider = builder.managedTypeProvider;
        JPQLQueryStateObject oldParent   = builder.parent;
        StateObject oldStateObject       = builder.stateObject;

        try {
            builder.jpqlQueryBuilder    = this;
            builder.managedTypeProvider = parent.getManagedTypeProvider();
            builder.parent              = parent.getRoot();
            builder.stateObject         = parent;

            // Visit the Expression for which a StateObject is needed
            jpqlExpression.getQueryStatement().accept(wrap(builder));

            return builder.stateObject;
        }
        finally {
            builder.jpqlQueryBuilder    = oldBuilder;
            builder.managedTypeProvider = oldProvider;
            builder.parent              = oldParent;
            builder.stateObject         = oldStateObject;
        }
    }

    /**
     * Creates the builder that creates the {@link StateObject} for each {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}.
     *
     * @return The builder that will be visiting the {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}
     */
    protected abstract BasicStateObjectBuilder buildStateObjectBuilder();

    /**
     * Returns the builder that creates the {@link StateObject} for each {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}.
     *
     * @return The builder that will be visiting the {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}
     */
    protected final BasicStateObjectBuilder getStateObjectBuilder() {
        if (builder == null) {
            builder = buildStateObjectBuilder();
        }
        return builder;
    }

    /**
     * Parses the given JPQL query with tolerant mode turned on.
     *
     * @param jpqlQuery The string representation of the JPQL query to parse
     * @param jpqlGrammar The JPQL grammar that defines how to parse a JPQL query
     * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
     * to parse invalid or incomplete queries
     * @return The root of the parsed tree representation of the JPQL query
     */
    protected JPQLExpression parse(CharSequence jpqlQuery, JPQLGrammar jpqlGrammar, boolean tolerant) {
        return new JPQLExpression(jpqlQuery, jpqlGrammar, tolerant);
    }

    /**
     * Parses the given JPQL fragment with tolerant mode turned on.
     *
     * @param jpqFragment The string representation of the portion of a JPQL query to parse
     * @param jpqlGrammar The JPQL grammar that defines how to parse a JPQL query
     * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF}
     * @return The root of the parsed tree representation of the JPQL fragment
     */
    protected JPQLExpression parse(CharSequence jpqFragment,
                                   JPQLGrammar jpqlGrammar,
                                   String queryBNFId) {

        return new JPQLExpression(jpqFragment, jpqlGrammar, queryBNFId, true);
    }

    /**
     * If a subclass needs to wrap the given {@link BasicStateObjectBuilder} with another visitor can
     * do so by simply overriding this method. {@link #buildStateObjectBuilder()} can't be easily
     * overridden with an {@link ExpressionVisitor} due to the constraint on the return type.
     *
     * @param builder The builder to wrap
     * @return By default the given builder is returned
     */
    protected ExpressionVisitor wrap(BasicStateObjectBuilder builder) {
        return builder;
    }
}
