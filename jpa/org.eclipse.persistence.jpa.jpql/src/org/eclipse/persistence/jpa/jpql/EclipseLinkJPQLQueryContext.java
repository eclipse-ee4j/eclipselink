/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;

/**
 * This context is used to store information related to the JPQL query. It supports the EclipseLink
 * extension over the default JPQL grammar.
 *
 * <pre><code> {@link org.eclipse.persistence.jpa.jpql.spi.IQuery IQuery} externalQuery = ...;
 *
 * JPQLQueryContext context = new JPQLQueryContext(DefaultJPQLGrammar.instance());
 * context.setQuery(query);</code></pre>
 *
 * If the JPQL query is already parsed, then the context can use it and it needs to be set before
 * setting the {@link org.eclipse.persistence.jpa.jpql.spi.IQuery IQuery}:
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
public class EclipseLinkJPQLQueryContext extends JPQLQueryContext {

	/**
	 * Creates a new <code>EclipseLinkJPQLQueryContext</code>.
	 *
	 * @param jpqlGrammar The grammar that defines how to parse a JPQL query
	 */
	public EclipseLinkJPQLQueryContext(JPQLGrammar jpqlGrammar) {
		super(jpqlGrammar);
	}

	/**
	 * Creates a new <code>EclipseLinkJPQLQueryContext</code>.
	 *
	 * @param parent The parent context
	 * @param currentQuery The parsed tree representation of the subquery
	 */
	protected EclipseLinkJPQLQueryContext(JPQLQueryContext parent, Expression currentQuery) {
		super(parent, currentQuery);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DeclarationResolver buildDeclarationResolver(DeclarationResolver parent) {
		return new EclipseLinkDeclarationResolver(parent, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLQueryContext buildJPQLQueryContext(JPQLQueryContext currentContext,
	                                                 Expression currentQuery) {

		return new EclipseLinkJPQLQueryContext(currentContext, currentQuery);
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
	@Override
	protected EclipseLinkParameterTypeVisitor buildParameterTypeVisitor() {
		return new EclipseLinkParameterTypeVisitor(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ResolverBuilder buildResolverBuilder() {
		return new EclipseLinkResolverBuilder(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EclipseLinkJPQLQueryContext getParent() {
		return (EclipseLinkJPQLQueryContext) super.getParent();
	}
}