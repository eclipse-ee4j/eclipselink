/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.AsOfClause;
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConnectByClause;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.HierarchicalQueryClause;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.StartWithClause;
import org.eclipse.persistence.jpa.jpql.parser.TableExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;

/**
 * TODO
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
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
	public void visit(AsOfClause expression) {
		type = Object.class;
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
	public void visit(ConnectByClause expression) {
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
	public void visit(HierarchicalQueryClause expression) {
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
	public void visit(StartWithClause expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TableExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TableVariableDeclaration expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnionClause expression) {
		type = Object.class;
	}
}