/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractConditionalClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateItemStateObject;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;

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