/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The {@link ExpressionVisitor} that adds support for the additional JPQL identifiers supported by
 * EclipseLink that is not defined in the JPA function specification.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.0
 * @author Pascal Filion
 */
public interface EclipseLinkExpressionVisitor extends ExpressionVisitor {

	/**
	 * Visits the {@link CastExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(CastExpression expression);

	/**
	 * Visits the {@link DatabaseType} expression.
	 *
	 * @param expression The {@link DatabaseType} to visit
	 */
	void visit(DatabaseType expression);

	/**
	 * Visits the {@link ExtractExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ExtractExpression expression);
}