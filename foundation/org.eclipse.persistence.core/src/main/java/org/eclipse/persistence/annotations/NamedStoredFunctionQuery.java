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
//     06/12/2017-2.7 Lukas Jungmann
//       - 518155: [jpa22] add support for repeatable annotations
package org.eclipse.persistence.annotations;

import jakarta.persistence.QueryHint;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A NamedStoredFunctionQuery annotation allows the definition of queries that
 * call stored functions as named queries.
 * <p>
 * A NamedStoredFunctionQuery annotation may be defined on an Entity or MappedSuperclass.
 *
 * @author James
 * @since EclipseLink 2.3
 */
@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NamedStoredFunctionQueries.class)
public @interface NamedStoredFunctionQuery {
    /**
     * Unique name that references this stored function query.
     */
    String name();

    /**
     * Query hints.
     */
    QueryHint[] hints() default {};

    /**
     * The name of the {@linkplain jakarta.persistence.SqlResultSetMapping}.
     */
    String resultSetMapping() default "";

    /**
     * The name of the stored function.
     */
    String functionName();

    /**
     * Defines if the stored function should be called by index or by name.
     * <p>
     * By index requires that the {@linkplain #parameters()} are defined in the same order as the function on the database.
     * <p>
     * By name requires the database platform support naming function parameters.
     */
    boolean callByIndex() default false;

    /**
     * Defines the parameters to the stored function.
     */
    StoredProcedureParameter[] parameters() default {};

    /**
     * Defines the return value of the stored function.
     */
    StoredProcedureParameter returnParameter();
}
