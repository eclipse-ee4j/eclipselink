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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The {@link ExpressionVisitor} that adds support for the additional JPQL identifiers supported by`
 * EclipseLink.
 *
 * @version 2.0
 * @since 2.0
 * @author Pascal Filion
 */
public interface EclipseLinkExpressionVisitor extends ExpressionVisitor {

	/**
	 * Visits the {@link FuncExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(FuncExpression expression);
	
        /**
         * Visits the {@link ColumnExpression} expression.
         *
         * @param expression The {@link Expression} to visit
         */
        void visit(ColumnExpression expression);
	
        /**
         * Visits the {@link SQLExpression} expression.
         *
         * @param expression The {@link Expression} to visit
         */
        void visit(SQLExpression expression);
        
        /**
         * Visits the {@link OperatorExpression} expression.
         *
         * @param expression The {@link Expression} to visit
         */
        void visit(OperatorExpression expression);

	/**
	 * Visits the {@link TreatExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(TreatExpression expression);
}