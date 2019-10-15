/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql;

/**
 * Some {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} can have a "literal",
 * this enumeration is used to visit an {@link org.eclipse.persistence.jpa.jpql.parser.Expression
 * Expression} and to retrieve the right value.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext#literal(org.eclipse.persistence.jpa.jpql.parser.Expression, LiteralType) JPQLQueryContext.literal(Expression, LiteralType)
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
