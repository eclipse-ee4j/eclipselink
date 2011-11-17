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
package org.eclipse.persistence.internal.jpa.jpql;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;

/**
 * The abstract definition of a range declaration, which is used to navigate to a "root" object.
 *
 * @see DerivedDeclaration
 * @see RangeDeclaration
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
abstract class AbstractRangeDeclaration extends Declaration {

	/**
	 * The identification variables that are defined in the <b>JOIN</b> expressions.
	 */
	List<String> joinIdentificationVariables;

	/**
	 * The list of <b>JOIN</b> expressions that are declared in the same declaration than the
	 * range variable declaration.
	 */
	List<Join> joins;

	/**
	 * Creates a new <code>AbstractRangeDeclaration</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	AbstractRangeDeclaration(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * Adds the given {@link Join} with its identification variable, which can be <code>null</code>.
	 *
	 * @param join The {@link Join} that was found in the list of joins
	 */
	void addJoin(Join join) {
		if (joins == null) {
			joins = new LinkedList<Join>();
		}
		joins.add(join);
	}

	private List<String> buildJoinIdentificationVariables() {

		List<String> variables;

		if (joins != null) {
			variables = new LinkedList<String>();

			for (Join join : joins) {
				IdentificationVariable identificationVariable = (IdentificationVariable) join.getIdentificationVariable();
				variables.add(identificationVariable.getVariableName());
			}
		}
		else {
			variables = Collections.emptyList();
		}

		return variables;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Expression findExpressionWithVariableName(String variableName) {

		Expression expression = super.findExpressionWithVariableName(variableName);

		if ((expression == null) && (joins != null)) {
			for (Join join : joins) {
				IdentificationVariable identificationVariable = (IdentificationVariable) join.getIdentificationVariable();
				if (identificationVariable.getVariableName().equals(variableName)) {
					return join;
				}
			}
		}

		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	RangeVariableDeclaration getBaseExpression() {
		return (RangeVariableDeclaration) super.getBaseExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	IdentificationVariableDeclaration getDeclarationExpression() {
		return (IdentificationVariableDeclaration) super.getDeclarationExpression();
	}

	/**
	 * Returns the identification variables that are defined in the <b>JOIN</b> expressions.
	 *
	 * @return The identification variables that are defined in the <b>JOIN</b> expressions
	 */
	List<String> getJoinIdentificationVariables() {
		if (joinIdentificationVariables == null) {
			joinIdentificationVariables = buildJoinIdentificationVariables();
		}
		return joinIdentificationVariables;
	}

	/**
	 * Returns the <b>JOIN</b> expressions that were part of the range variable declaration in the
	 * ordered they were parsed.
	 *
	 * @return The ordered list of <b>JOIN</b> expressions or an empty collection if none was
	 * present
	 */
	Collection<Join> getJoins() {
		return (joins == null) ? Collections.<Join>emptyList() : joins;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean hasAlias(String variableName) {
		return super.hasAlias(variableName) || hasJoinAlias(variableName);
	}

	private boolean hasJoinAlias(String variableName) {

		if (joins != null) {

			for (Join join : joins) {
				IdentificationVariable identificationVariable = (IdentificationVariable) join.getIdentificationVariable();
				if (identificationVariable.getVariableName().equals(variableName)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Determines whether the declaration contains <b>JOIN</b> expressions. This can be
	 * <code>true</code> only when {@link #isRange()} returns <code>true</code>. A collection
	 * member declaration does not have <b>JOIN</b> expressions.
	 *
	 * @return <code>true</code> if at least one <b>JOIN</b> expression was parsed; otherwise
	 * <code>false</code>
	 */
	boolean hasJoins() {
		return joins != null;
	}
}