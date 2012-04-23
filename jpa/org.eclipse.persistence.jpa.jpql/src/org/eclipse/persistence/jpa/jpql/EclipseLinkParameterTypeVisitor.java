/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkParameterTypeVisitor extends ParameterTypeVisitor
                                             implements EclipseLinkExpressionVisitor {

	/**
	 * Creates a new <code>EclipseLinkParameterTypeVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	public EclipseLinkParameterTypeVisitor(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CastExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DatabaseType expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExtractExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RegexpExpression expression) {

		Expression patternValue = expression.getPatternValue();
		Expression stringExpression = expression.getStringExpression();

		if (patternValue.isAncestor(inputParameter)) {
			this.expression = expression.getStringExpression();
		}
		else if (stringExpression.isAncestor(inputParameter)) {
			this.expression = expression;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnionClause expression) {
		type = Object.class;
	}
}