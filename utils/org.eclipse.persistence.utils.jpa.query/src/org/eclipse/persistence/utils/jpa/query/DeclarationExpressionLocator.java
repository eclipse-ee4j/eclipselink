/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteClause;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteStatement;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.FromClause;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleFromClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateClause;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateStatement;

/**
 * This locator (visitor) is responsible to traverse up the parsed tree and to locate the declaration
 * expressions where the identification variables are declared. The possible choices are:
 * <ul>
 * <li>The <b>FROM</b> clause from a select query;
 * <li>The (simple) <b>FROM</b> clause from a simple select query;
 * <li>the <b>DELETE</b> clause from a delete query.
 * <li>the <b>UPDATE</b> clause from an update query.
 * </ul>
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class DeclarationExpressionLocator extends AbstractTraverseParentVisitor {

	/**
	 * The {@link Expression Expressions} where the identification variables are declared, it's
	 * possible to have more than one when the query has subqueries.
	 */
	private Set<Expression> expressions;

	/**
	 * Keeps track of the {@link Expression} in order to prevent an infinite loop if part of the
	 * expression is invalid. For instance, if the FROM clause is not declared, then the <b>SELECT</b>
	 * statement would loop infinitely.
	 */
	private Expression traversedExpression;

	/**
	 * Determines whether the parent declaration expression should be part of the set. This means
	 * if this is <code>true</code>, then from a subquery, the subquery's declaration and the top-
	 * level query's declaration will be retrieved; <code>false</code> will only retrieve the
	 * subquery's declaration.
	 */
	private boolean traverseParentQueries;

	/**
	 * Creates a new <code>DeclarationExpressionLocator</code>.
	 */
	DeclarationExpressionLocator() {
		this(true);
	}

	/**
	 * Creates a new <code>DeclarationExpressionLocator</code>.
	 *
	 * @param traverseParentQueries Determines whether the parent declaration expression should be
	 * part of the set when visiting a subquery
	 */
	DeclarationExpressionLocator(boolean traverseParentQueries) {
		super();
		this.traverseParentQueries = traverseParentQueries;
		this.expressions           = new LinkedHashSet<Expression>();
	}

	/**
	 * Returns the {@link Expression Expressions} where the identification variables are declared.
	 *
	 * @return The set of {@link Expression Expressions} used to define the identification variables
	 */
	Set<Expression> declarationExpresions() {
		return expressions;
	}

	/**
	 * Returns the first {@link Expression} representing a declaration.
	 *
	 * @return The first {@link Expression} representing a declaration
	 */
	Expression getExpression() {
		return expressions.iterator().next();
	}

	/**
	 * Returns the reversed other of the {@link Expression Expressions} where the identification
	 * variables are declared, which sub-query to the top-level query.
	 *
	 * @return The reversed set of {@link Expression Expressions} used to define the identification
	 * variables
	 */
	Set<Expression> reversedDeclarationExpresions() {
		List<Expression> expressionList = new ArrayList<Expression>(expressions);
		Collections.reverse(expressionList);
		return new LinkedHashSet<Expression>(expressionList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {
		expressions.add(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression) {
		if (traversedExpression == null) {
			traversedExpression = expression;
			expression.getDeleteClause().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression) {
		expressions.add(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		if (traversedExpression == null) {
			traversedExpression = expression;
			expression.getFromClause().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		if (expressions.add(expression) && traverseParentQueries) {
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression) {

		expression.getFromClause().accept(this);

		// Continue because the top-level FROM clause is also needed
		if (traverseParentQueries) {
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {
		expressions.add(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression) {
		if (traversedExpression == null) {
			traversedExpression = expression;
			expression.getUpdateClause().accept(this);
		}
	}
}