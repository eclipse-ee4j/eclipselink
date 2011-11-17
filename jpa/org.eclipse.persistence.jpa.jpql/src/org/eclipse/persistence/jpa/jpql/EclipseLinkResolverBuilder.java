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

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;

/**
 * @version 2.4
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
	public void visit(FuncExpression expression) {
		resolver = buildClassResolver(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {

		// Visit the identification variable in order to create the resolver
		expression.getCollectionValuedPathExpression().accept(this);

		// Retrieve the entity type name
		String entityTypeName = getQueryContext().literal(
			expression.getEntityType(),
			LiteralType.ENTITY_TYPE
		);

		// Wrap the Resolver for down casting
		resolver = new TreatResolver(resolver, entityTypeName);
	}
}