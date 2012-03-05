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

import org.eclipse.persistence.jpa.jpql.parser.ColumnExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.OperatorExpression;
import org.eclipse.persistence.jpa.jpql.parser.SQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;

/**
 * This visitor traverses an {@link Expression} and retrieves the "literal" value. The literal to
 * retrieve depends on the {@link LiteralType type}. The literal is basically a string value like an
 * identification variable name, an input parameter, a path expression, an abstract schema name,
 * etc.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class EclipseLinkLiteralVisitor extends LiteralVisitor
                                             implements EclipseLinkExpressionVisitor {

	/**
	 * Creates a new <code>EclipseLinkLiteralVisitor</code>.
	 */
	public EclipseLinkLiteralVisitor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpression expression) {
		if (type == LiteralType.STRING_LITERAL) {
			literal = expression.getFunctionName();
		}
	}

        /**
         * {@inheritDoc}
         */
        public void visit(SQLExpression expression) {
                if (type == LiteralType.STRING_LITERAL) {
                        literal = expression.getSQL();
                }
        }

        /**
         * {@inheritDoc}
         */
        public void visit(OperatorExpression expression) {
                if (type == LiteralType.STRING_LITERAL) {
                        literal = expression.getOperator();
                }
        }

        /**
         * {@inheritDoc}
         */
        public void visit(ColumnExpression expression) {
                if (type == LiteralType.STRING_LITERAL) {
                        literal = expression.getColumn();
                }
        }

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
		expression.getCollectionValuedPathExpression().accept(this);
		expression.getEntityType().accept(this);
	}
}