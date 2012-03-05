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
 * This {@link EclipseLinkExpressionVisitor} traverses up the hierarchy. It is up to the subclass to
 * complete the behavior.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractEclipseLinkTraverseParentVisitor extends AbstractTraverseParentVisitor
                                                               implements EclipseLinkExpressionVisitor {

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpression expression) {
		expression.getParent().accept(this);
	}
	
        /**
         * {@inheritDoc}
         */
        public void visit(ColumnExpression expression) {
                expression.getParent().accept(this);
        }
	
        /**
         * {@inheritDoc}
         */
        public void visit(SQLExpression expression) {
                expression.getParent().accept(this);
        }
        
        /**
         * {@inheritDoc}
         */
        public void visit(OperatorExpression expression) {
                expression.getParent().accept(this);
        }

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
		expression.getParent().accept(this);
	}
}