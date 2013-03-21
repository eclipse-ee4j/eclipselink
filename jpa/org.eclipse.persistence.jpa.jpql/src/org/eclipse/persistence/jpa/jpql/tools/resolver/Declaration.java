/*******************************************************************************
 * Copyright (c) 2012, 2013, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPQLQueryDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.Join;

/**
 * The abstract definition of {@link JPQLQueryDeclaration}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public abstract class Declaration implements JPQLQueryDeclaration {

	/**
	 * Either the range variable declaration if this is a range declaration otherwise the
	 * collection-valued path expression when this is a collection member declaration.
	 */
	protected Expression baseExpression;

	/**
	 * The declaration expression, which is either an {@link IdentificationVariableDeclaration} or
	 * a {@link CollectionMemberDeclaration} when part of a <b>FROM</b> clause, otherwise it's
	 * either the {@link DeleteClause} or the {@link UpdateClause}.
	 */
	protected Expression declarationExpression;

	/**
	 * The identification variable used to declare the "root" object.
	 */
	protected IdentificationVariable identificationVariable;

	/**
	 * The "root" object for objects which may not be reachable by navigation, it is either the
	 * abstract schema name (entity name), a derived path expression (which is only defined in a
	 * subquery) or <code>null</code> if this {@link Declaration} is a collection member declaration.
	 */
	protected String rootPath;

	/**
	 * Creates a new <code>Declaration</code>.
	 */
	protected Declaration() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getBaseExpression() {
		return baseExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getDeclarationExpression() {
		return declarationExpression;
	}

	/**
	 * Returns the {@link IdentificationVariable} used to declare the "root" object.
	 *
	 * @return The alias for the "root" object
	 */
	public IdentificationVariable getIdentificationVariable() {
		return identificationVariable;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Join> getJoins() {
		return Collections.emptyList();
	}

	/**
	 * Returns the "root" object for objects which may not be reachable by navigation, it is
	 * either the abstract schema name (entity name), a derived path expression (which is only
	 * defined in a subquery) or <code>null</code> if this {@link Declaration} is a collection
	 * member declaration.
	 *
	 * @return The "root" object for objects which may not be reachable by navigation or
	 * <code>null</code> if this {@link Declaration} is a collection member declaration
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getVariableName() {
		if (identificationVariable == null) {
			return ExpressionTools.EMPTY_STRING;
		}
		return identificationVariable.getVariableName();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasJoins() {
		return false;
	}

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
		sb.append(AbstractExpression.SPACE);
		sb.append(identificationVariable);
		return sb.toString();
	}
}