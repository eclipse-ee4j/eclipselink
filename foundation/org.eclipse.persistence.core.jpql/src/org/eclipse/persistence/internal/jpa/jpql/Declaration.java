/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * A <code>Declaration</code> is the corresponding representation of a single declaration defined in
 * the <code><b>FROM</b></code> clause of a query.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
abstract class Declaration {

	/**
	 * Either the range variable declaration if this is a range declaration otherwise the
	 * collection-valued path expression when this is a collection member declaration.
	 */
	Expression baseExpression;

	/**
	 * The declaration expression, which is either an {@link IdentificationVariableDeclaration} or
	 * a {@link CollectionMemberDeclaration} when part of a <b>FROM</b> clause, otherwise it's
	 * either the {@link DeleteClause} or the {@link UpdateClause}.
	 */
	Expression declarationExpression;

	/**
	 *
	 */
	private ClassDescriptor descriptor;

	/**
	 * The identification variable used to declare an abstract schema name or a collection-valued
	 * path expression.
	 */
	IdentificationVariable identificationVariable;

	/**
	 *
	 */
	private DatabaseMapping mapping;

	/**
	 * The {@link JPQLQueryContext} is used to query information about the application metadata and
	 * cached information.
	 */
	final JPQLQueryContext queryContext;

	/**
	 * The "root" object for objects which may not be reachable by navigation, it is either the
	 * abstract schema name (entity name), a derived path expression (which is only defined in a
	 * subquery) or <code>null</code> if this {@link Declaration} is a collection member declaration.
	 */
	String rootPath;

	private Class<?> type;

	/**
	 * Creates a new <code>Declaration</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	Declaration(JPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	abstract org.eclipse.persistence.expressions.Expression buildExpression();

	/**
	 * Retrieves
	 *
	 * @param variableName
	 * @return
	 */
	Expression findExpressionWithVariableName(String variableName) {
		if (getVariableName().equals(variableName)) {
			return baseExpression;
		}
		return null;
	}

	/**
	 * Returns the range variable declaration if this is a range declaration otherwise the
	 * collection-valued path expression when this is a collection member declaration.
	 *
	 * @return Either the range variable declaration or the collection-valued path expression
	 */
	Expression getBaseExpression() {
		return baseExpression;
	}

	/**
	 * Returns the declaration expression, which is either an {@link IdentificationVariableDeclaration}
	 * or a {@link CollectionMemberDeclaration} when part of a <b>FROM</b> clause, otherwise it's
	 * either the {@link DeleteClause} or the {@link UpdateClause}.
	 *
	 * @return The root of the declaration expression
	 */
	Expression getDeclarationExpression() {
		return declarationExpression;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	final ClassDescriptor getDescriptor() {
		if (descriptor == null) {
			descriptor = resolveDescriptor();
		}
		return descriptor;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	final DatabaseMapping getMapping() {
		if (mapping == null) {
			mapping = resolveMapping();
		}
		return mapping;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	final Class<?> getType() {
		if (type == null) {
			type = resolveType();
		}
		return type;
	}

	/**
	 * Returns the identification variable name that is defining either the abstract schema name
	 * or the collection-valued path expression
	 *
	 * @return The identification variable or an empty string if none was defined
	 */
	String getVariableName() {
		if (identificationVariable == null) {
			return ExpressionTools.EMPTY_STRING;
		}
		return identificationVariable.getVariableName();
	}

	/**
	 * Determines whether
	 *
	 * @param variableName
	 * @return
	 */
	boolean hasAlias(String variableName) {
		return getVariableName().equals(variableName);
	}

	/**
	 * Determines whether the "root" object is a derived path expression where the identification
	 * variable is declared in the superquery, otherwise it's an entity name.
	 *
	 * @return <code>true</code> if the root path is a derived path expression; <code>false</code>
	 * otherwise
	 */
	abstract boolean isDerived();

	/**
	 * Determines whether this {@link Declaration} represents a range identification variable
	 * declaration, example: "Employee e".
	 *
	 * @return <code>true</code> if the declaration is over an abstract schema name; <code>false</code>
	 * if it's over a collection-valued path expression
	 * @see #isDerived()
	 */
	abstract boolean isRange();

	/**
	 * Resolves
	 *
	 * @return
	 */
	abstract ClassDescriptor resolveDescriptor();

	/**
	 * Resolves
	 *
	 * @return
	 */
	abstract DatabaseMapping resolveMapping();

	/**
	 * Resolves
	 *
	 * @return
	 */
	abstract Class<?> resolveType();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		if (declarationExpression != null) {
			return declarationExpression.toParsedText();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(rootPath);

		if (identificationVariable != null) {
			sb.append(AbstractExpression.SPACE);
			sb.append(identificationVariable.getText());
		}

		return sb.toString();
	}
}