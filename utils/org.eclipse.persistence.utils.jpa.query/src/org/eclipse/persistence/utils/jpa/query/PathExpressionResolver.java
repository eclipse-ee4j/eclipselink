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

import org.eclipse.persistence.expressions.Expression;

/**
 * This builder is responsible to create the tree hierarchy of {@link Expression expressions}
 * representing a state field or collection-valued path expression.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
interface PathExpressionResolver extends Cloneable {

	/**
	 * Returns a copy of this {@link PathExpressionResolver}.
	 *
	 * @return The copy of this object
	 */
	PathExpressionResolver clone();

	/**
	 * Retrieves the {@link Expression} for the path represented by this resolver.
	 *
	 * @return The {@link Expression} for a path
	 */
	Expression getExpression();

	/**
	 * Retrieves the {@link Expression} for the given variable name, which can either be an
	 * identification variable or a path from a state field path expression or a collection-valued
	 * path expression.
	 *
	 * @param variableName The name of the variable for which an {@link Expression} is needed
	 * @return The {@link Expression} for a the given variable name
	 */
	Expression getExpression(String variableName);

	/**
	 * Determines whether the {@link Expression} to be created, which wraps the attribute or query
	 * key name allows the target of the 1:1 relationship to be <code>null</code> if there is no
	 * corresponding relationship in the database.
	 *
	 * @return <code>true</code> to allow <code>null</code> if the corresponding relationship in the
	 * database does not exists; <code>false</code> otherwise
	 */
	boolean isNullAllowed();

	/**
	 * Determines whether the {@link Expression} to be created, which wraps the given attribute or
	 * query key name allows the target of the 1:1 relationship to be <code>null</code> if there is
	 * no corresponding relationship in the database.
	 *
	 * @param variableName The name of the variable to determine if the relationship can be
	 * <code>null</code>
	 * @return <code>true</code> to allow <code>null</code> if the corresponding relationship in the
	 * database does not exists; <code>false</code> otherwise
	 */
	boolean isNullAllowed(String variableName);

	/**
	 * Sets whether the {@link Expression} to be created, which wraps the attribute or query
	 * key name allows the target of the 1:1 relationship to be <code>null</code> if there is no
	 * corresponding relationship in the database.
	 *
	 * @param nullAllowed <code>true</code> to allow <code>null</code> if the corresponding
	 * relationship in the database does not exists; <code>false</code> otherwise
	 */
	void setNullAllowed(boolean nullAllowed);
}