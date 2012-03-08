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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.Join;

/**
 * A <code>JPQLQueryDeclaration</code> represents either an identification variable declaration or a
 * collection member declaration. For a subquery, the declaration can be a derived path expression.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface JPQLQueryDeclaration {

	/**
	 * Returns the range variable declaration if this is a range declaration otherwise the
	 * collection-valued path expression when this is a collection member declaration.
	 *
	 * @return Either the range variable declaration or the collection-valued path expression
	 */
	Expression getBaseExpression();

	/**
	 * Returns the declaration expression, which is either an {@link IdentificationVariableDeclaration}
	 * or a {@link CollectionMemberDeclaration} when part of a <b>FROM</b> clause, otherwise it's
	 * either the {@link DeleteClause} or the {@link UpdateClause}.
	 *
	 * @return The root of the declaration expression
	 */
	Expression getDeclarationExpression();

	/**
	 * Returns the <b>JOIN</b> expressions that were part of the range variable declaration in the
	 * ordered they were parsed.
	 *
	 * @return The ordered list of <b>JOIN</b> expressions or an empty collection if none was
	 * present
	 */
	List<Join> getJoins();

	/**
	 * Returns the identification variable name that is defining either the abstract schema name
	 * or the collection-valued path expression
	 *
	 * @return The identification variable or an empty string if none was defined
	 */
	String getVariableName();

	/**
	 * Determines whether the declaration contains <b>JOIN</b> expressions. This can be
	 * <code>true</code> only when {@link #isRange()} returns <code>true</code>. A collection
	 * member declaration does not have <b>JOIN</b> expressions.
	 *
	 * @return <code>true</code> if at least one <b>JOIN</b> expression was parsed;
	 * otherwise <code>false</code>
	 */
	boolean hasJoins();

	/**
	 * Determines whether this {@link Declaration}
	 *
	 * @return <code>true</code>
	 */
	boolean isCollection();

	/**
	 * Determines whether the "root" object is a derived path expression where the identification
	 * variable is declared in the superquery, otherwise it's an entity name.
	 *
	 * @return <code>true</code> if the root path is a derived path expression; <code>false</code>
	 * otherwise
	 */
	boolean isDerived();

	/**
	 * Determines whether this {@link Declaration} represents a range identification variable
	 * declaration, example: "Employee e".
	 *
	 * @return <code>true</code> if the declaration is over an abstract schema name; <code>false</code>
	 * if it's over a collection-valued path expression
	 * @see #isDerived()
	 */
	boolean isRange();
}