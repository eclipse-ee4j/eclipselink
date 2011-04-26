/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.internal.jpql.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * This helper can perform the following operations over a JPQL query:
 * <ul>
 * <li>Calculates the result type of a query: {@link #getResultType()};</li>
 * <li>Calculates the type of an input parameter: {@link #getParameterType(String)}.</li>
 * <li>Calculates the possible choices to complete the query from a given
 *     position (used for content assist): {@link #buildContentAssistItems(int)}.</li>
 * <li>Validates the query by introspecting it grammatically and semantically:
 *     <ul>
 *     <li>{@link #validate()},</li>
 *     <li>{@link #validateGrammar()},</li>
 *     <li>{@link #validateSemantic()}.</li>
 *     </ul></li>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public class JPQLQueryHelper extends AbstractJPQLQueryHelper {

	/**
	 * Creates a new <code>JPQLQueryHelper</code>.
	 */
	public JPQLQueryHelper() {
		super();
	}

	/**
	 * Creates a new <code>JPQLQueryHelper</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 */
	public JPQLQueryHelper(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentAssistProposals buildContentAssistProposals(int position) {
		return super.buildContentAssistProposals(position);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getParameterType(String parameterName) {
		return super.getParameterType(parameterName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParsedJPQLQuery() {
		return super.getParsedJPQLQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedTypeProvider getProvider() {
		return super.getProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IQuery getQuery() {
		return super.getQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getResultType() {
		return super.getResultType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType(Class<?> type) {
		return super.getType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeHelper getTypeHelper() {
		return super.getTypeHelper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeRepository getTypeRepository() {
		return super.getTypeRepository();
	}

	/**
	 * Sets the external form of the JPQL query, which will be parsed and information will be
	 * extracted for later access.
	 *
	 * @param query The external form of the JPQL query
	 */
	@Override
	public void setQuery(IQuery query) {
		super.setQuery(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<JPQLQueryProblem> validate() {
		return super.validate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<JPQLQueryProblem> validateGrammar() {
		return super.validateGrammar();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<JPQLQueryProblem> validateSemantic() {
		return super.validateSemantic();
	}
}