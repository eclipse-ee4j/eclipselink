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

import org.eclipse.persistence.annotations.Direction;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A PLSQLParameter annotation is used within a {@linkplain NamedPLSQLStoredProcedureQuery},
 * {@linkplain NamedPLSQLStoredFunctionQuery}, {@linkplain OracleObject}
 * or {@linkplain PLSQLRecord} annotation.
 *
 * @see NamedPLSQLStoredFunctionQuery
 * @see NamedPLSQLStoredProcedureQuery
 * @see OracleObject
 * @see PLSQLRecord
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
@Target({})
@Retention(RUNTIME)
public @interface PLSQLParameter {
    /**
     * The direction of the stored procedure parameter.
     */
    Direction direction() default Direction.IN;

    /**
     * Stored procedure parameter name.
     */
    String name();

    /**
     * The query parameter name.
     */
    String queryParameter() default "";

    /**
     * Define if the parameter is required, or optional and defaulted by the procedure.
     */
    boolean optional() default false;

    /**
     * The database data-type for the parameter.
     * <p>
     * This is either one of the type constants defined in
     * {@linkplain org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes},
     * or {@linkplain org.eclipse.persistence.platform.database.jdbc.JDBCTypes},
     * or a custom record or table type name.
     *
     * @see PLSQLRecord
     * @see org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes
     * @see org.eclipse.persistence.platform.database.jdbc.JDBCTypes
     */
    String databaseType() default "VARCHAR_TYPE";

    /**
     * The max length of the field value.
     */
    int length() default 255;

    /**
     * If a numeric, the max scale value.
     */
    int scale() default 0;

    /**
     * If a numeric, the max precision value.
     */
    int precision() default 0;
}
