/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.annotations;

import jakarta.persistence.ParameterMode;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A StoredProcedureParameter annotation is used within a
 * {@linkplain NamedStoredProcedureQuery} and {@linkplain NamedStoredFunctionQuery} annotation.
 *
 * @see NamedStoredFunctionQuery
 * @see NamedStoredProcedureQuery
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({})
@Retention(RUNTIME)
public @interface StoredProcedureParameter {
    /**
     * The direction of the stored procedure parameter.
     * @deprecated Use {@linkplain #mode()}.
     */
    @Deprecated
    Direction direction() default Direction.IN;

    /**
     * The direction of the stored procedure parameter.
     */
    ParameterMode mode() default ParameterMode.IN;

    /**
     * Stored procedure parameter name.
     */
    String name() default "";

    /**
     * The query parameter name.
     */
    String queryParameter();

    /**
     * Define if the parameter is required, or optional and defaulted by the procedure.
     */
    boolean optional() default false;

    /**
     * The type of Java class desired back from the procedure,
     * this is dependent on the type returned from the procedure.
     */
    Class<?> type() default void.class;

    /**
     * The JDBC type code, this is dependent on the type returned
     * from the procedure.
     */
    int jdbcType() default -1;

    /**
     * The JDBC type name, this may be required for ARRAY or
     * STRUCT types.
     */
    String jdbcTypeName() default "";
}
