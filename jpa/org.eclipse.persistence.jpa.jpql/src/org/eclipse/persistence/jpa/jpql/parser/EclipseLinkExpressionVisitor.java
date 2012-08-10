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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The {@link ExpressionVisitor} that adds support for the additional JPQL identifiers supported by
 * EclipseLink that is not defined in the JPA function specification.
 * <p>
 * <b>Important:</b> If a new specification of the Java persistence is released, this interface will
 * be augmented to support the new functionality.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see AbstractEclipseLinkExpressionVisitor
 * @see AbstractEclipseLinkTraverseChildrenVisitor
 * @see AbstractEclipseLinkTraverseParentVisitor
 * @see EclipseLinkAnonymousExpressionVisitor
 *
 * @version 2.5
 * @since 2.0
 * @author Pascal Filion
 */
public interface EclipseLinkExpressionVisitor extends ExpressionVisitor {

	/**
	 * Visits the {@link AsOfClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(AsOfClause expression);

	/**
	 * Visits the {@link CastExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(CastExpression expression);

	/**
	 * Visits the {@link ConnectByClause} expression.
	 *
	 * @param expression The {@link ConnectByClause} to visit
	 */
	void visit(ConnectByClause expression);

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

	/**
	 * Visits the {@link HierarchicalQueryClause} expression.
	 *
	 * @param expression The {@link HierarchicalQueryClause} to visit
	 */
	void visit(HierarchicalQueryClause expression);

	/**
	 * Visits the {@link RegexpExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(RegexpExpression expression);

	/**
	 * Visits the {@link StartWithClause} expression.
	 *
	 * @param expression The {@link StartWithClause} to visit
	 */
	void visit(StartWithClause expression);

	/**
	 * Visits the {@link TableExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(TableExpression expression);

	/**
	 * Visits the {@link TableVariableDeclaration} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(TableVariableDeclaration expression);

	/**
	 * Visits the {@link UnionClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(UnionClause expression);
}