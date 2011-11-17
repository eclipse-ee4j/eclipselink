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
package org.eclipse.persistence.jpa.jpql;

/**
 * Some {@link Expression} can have a "literal", this enumeration is used to visit an {@link
 * Expression} and to retrieve the right value.
 *
 * @see JPQLQueryContext#literal(Expression, LiteralType)
 * @see LiteralVisitor
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public enum LiteralType {

	/**
	 * Retrieves the abstract schema name only.
	 */
	ABSTRACT_SCHEMA_NAME,

	/**
	 * Retrieves the entity type name only.
	 */
	ENTITY_TYPE,

	/**
	 * Retrieves an identification variable name only.
	 */
	IDENTIFICATION_VARIABLE,

	/**
	 * Retrieves the input parameter value.
	 */
	INPUT_PARAMETER,

	/**
	 * Retrieves the entire state field path or collection-valued path expression.
	 */
	PATH_EXPRESSION_ALL_PATH,

	/**
	 * Retrieves the identification variable name of a path expression.
	 */
	PATH_EXPRESSION_IDENTIFICATION_VARIABLE,

	/**
	 * Retrieves the last path of a state field path or collection-valued path expression.
	 */
	PATH_EXPRESSION_LAST_PATH,

	/**
	 * Retrieves the result variable.
	 */
	RESULT_VARIABLE,

	/**
	 * Retrieves the string literal only.
	 */
	STRING_LITERAL;
}