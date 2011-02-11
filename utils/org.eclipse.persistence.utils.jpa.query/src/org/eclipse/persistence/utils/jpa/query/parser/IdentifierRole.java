/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * A role describes the purpose of the JPQL identifier. To retrieve the role of
 * an identifier, one can use {@link JPQLExpression#identifierRole(String)}
 * where the string is the identifier.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public enum IdentifierRole
{
	/**
	 * Indicates the identifier aggregates two expressions together. The
	 * identifiers are <b>AND</b>, <b>OR</b>, +, -, * and /.
	 */
	AGGREGATE,

	/**
	 * Indicates the identifier is used to create a clause. They are the root of
	 * a portion of the query. An example is <b>SELECT</b>.
	 */
	CLAUSE,

	/**
	 * Indicates the identifier is used to complement an expression, it is not
	 * required for creating an expression. Examples are <b>AS</b> or
	 * <b>OUTER</b>.
	 */
	COMPLETEMENT,

	/**
	 * Indicates the identifier is kind of a function, it does not return a value
	 * but it is used to perform some operation over some expression. The
	 * expression can have an expression before and then after but it's not used
	 * to aggregate those two expression. An example is <b>x MEMBER y</b>.
	 */
	COMPOUND_FUNCTION,

	/**
	 * Indicates the identifier is used to create a function, it has some
	 * parameters and returns a value. An example is <b>ABS(x)</b>, usually the
	 * identifier has some values encapsulated with parenthesis.
	 * <p>
	 * Note: <b>TRUE</b>, <b>FALSE</b>, <b>NULL</b>, <b>CURRENT_DATE</b>,
	 * <b>CURRENT_TIME</b>, <b>CURRENT_TIMESTAMP</b> are considered functions.
	 */
	FUNCTION,

	/**
	 * Indicates the identifier is not part of the language but it has been
	 * reserved for future use. The identifiers are <b>BIT_LENGTH</b>,
	 * <b>CHAR_LENGTH</b>, <b>CHARACTER_LENGTH</b>, <b>POSITION</b>, and
	 * <b>UNKNOWN</b>.
	 */
	UNUSED
}