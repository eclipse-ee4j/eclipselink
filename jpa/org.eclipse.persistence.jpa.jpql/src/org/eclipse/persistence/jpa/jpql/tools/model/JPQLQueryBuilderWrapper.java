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

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.tools.model.query.AbstractConditionalClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.SimpleSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.UpdateItemStateObject;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;

/**
 * This builder wraps another builder and simply delegates the calls to it.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class JPQLQueryBuilderWrapper implements IJPQLQueryBuilder {

    /**
     * The delegate builder that receives the calls from this one.
     */
    private final IJPQLQueryBuilder delegate;

    /**
     * Creates a new <code>JPQLQueryBuilderWrapper</code>.
     *
     * @param delegate The delegate builder that receives the calls from this one
     * @exception NullPointerException If the given delegate is <code>null</code>
     */
    protected JPQLQueryBuilderWrapper(IJPQLQueryBuilder delegate) {
        super();
        Assert.isNotNull(delegate, "The delegate builder cannot be null");
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public ICaseExpressionStateObjectBuilder buildCaseExpressionStateObjectBuilder(StateObject parent) {
        return delegate.buildCaseExpressionStateObjectBuilder(parent);
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryStateObject buildStateObject(IManagedTypeProvider provider,
                                                 CharSequence jpqlQuery,
                                                 boolean tolerant) {

        return delegate.buildStateObject(provider, jpqlQuery, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryStateObject buildStateObject(IManagedTypeProvider provider,
                                                 CharSequence jpqlQuery,
                                                 String queryBNFId,
                                                 boolean tolerant) {

        return delegate.buildStateObject(provider, jpqlQuery, queryBNFId, tolerant);
    }

    /**
     * {@inheritDoc}
     */
    public StateObject buildStateObject(StateObject parent,
                                        CharSequence jpqlFragment,
                                        String queryBNFId) {

        return delegate.buildStateObject(parent, jpqlFragment, queryBNFId);
    }

    /**
     * {@inheritDoc}
     */
    public IConditionalExpressionStateObjectBuilder buildStateObjectBuilder(AbstractConditionalClauseStateObject stateObject) {
        return delegate.buildStateObjectBuilder(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    public ISelectExpressionStateObjectBuilder buildStateObjectBuilder(SelectClauseStateObject stateObject) {
        return delegate.buildStateObjectBuilder(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    public ISimpleSelectExpressionStateObjectBuilder buildStateObjectBuilder(SimpleSelectClauseStateObject stateObject) {
        return delegate.buildStateObjectBuilder(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    public INewValueStateObjectBuilder buildStateObjectBuilder(UpdateItemStateObject stateObject) {
        return delegate.buildStateObjectBuilder(stateObject);
    }

    /**
     * Returns the delegate builder that receives the calls from this one.
     *
     * @return The wrapped builder
     */
    protected IJPQLQueryBuilder getDelegate() {
        return delegate;
    }

    /**
     * {@inheritDoc}
     */
    public JPQLGrammar getGrammar() {
        return delegate.getGrammar();
    }
}
