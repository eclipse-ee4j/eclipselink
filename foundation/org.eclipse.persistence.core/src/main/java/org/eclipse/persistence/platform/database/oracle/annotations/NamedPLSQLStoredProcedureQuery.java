/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle.annotations;

import jakarta.persistence.QueryHint;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A NamedPLSQLStoredProcedureQuery annotation allows the definition of queries that
 * call PLSQL stored procedures as named queries.
 * <p>
 * The PLSQL support adds support for complex PLSQL types such as RECORD and TABLE types,
 * that are not accessible from JDBC.
 * <p>
 * A NamedPLSQLStoredProcedureQuery annotation may be defined on an Entity or MappedSuperclass.
 *
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NamedPLSQLStoredProcedureQueries.class)
public @interface NamedPLSQLStoredProcedureQuery {
    /**
     * Unique name that references this stored procedure query.
     */
    String name();

    /**
     * Query hints.
     */
    QueryHint[] hints() default {};

    /**
     * Refers to the class of the result.
     */
    Class<?> resultClass() default void.class;

    /**
     * The name of the {@linkplain jakarta.persistence.SqlResultSetMapping}.
     */
    String resultSetMapping() default "";

    /**
     * The name of the stored procedure.
     */
    String procedureName();

    /**
     * Defines the parameters to the stored procedure.
     */
    PLSQLParameter[] parameters() default {};
}
