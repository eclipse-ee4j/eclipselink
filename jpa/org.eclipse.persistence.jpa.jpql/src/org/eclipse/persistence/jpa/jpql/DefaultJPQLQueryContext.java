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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;

/**
 * This context is used to store information related to the JPQL query.
 *
 * <pre><code> {@link IQuery} externalQuery = ...;
 *
 * JPQLQueryContext context = new JPQLQueryContext(DefaultJPQLGrammar.instance());
 * context.setQuery(query);</code></pre>
 *
 * If the JPQL query is already parsed, then the context can use it and it needs to be set before
 * setting the {@link IQuery}:
 * <pre><code> {@link JPQLExpression} jpqlExpression = ...;
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
	protected DefaultJPQLQueryContext(JPQLQueryContext currentContext, Expression currentQuery) {
		super(currentContext, currentQuery);
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
	protected LiteralVisitor buildLiteralVisitor() {
		return new DefaultLiteralVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ResolverBuilder buildResolverBuilder() {
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