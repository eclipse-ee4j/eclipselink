/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     06/12/2017-2.7 Lukas Jungmann
//       - 518155: [jpa22] add support for repeatable annotations
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;

import jakarta.persistence.QueryHint;

/**
 * A NamedStoredProcedureQuery annotation allows the definition of queries that
 * call stored procedures as named queries.
 *
 * A NamedStoredProcedureQuery annotation may be defined on an Entity or
 * MappedSuperclass.
 *
 * @see org.eclipse.persistence.annotations.StoredProcedureParameter
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NamedStoredProcedureQueries.class)
public @interface NamedStoredProcedureQuery {
    /**
     * (Required) Unique name that references this stored procedure query.
     */
    String name();

    /**
     * (Optional) Query hints.
     */
    QueryHint[] hints() default {};

    /**
     * (Optional) Refers to the class of the result.
     * @deprecated
     * @see resultClasses
     */
    Class resultClass() default void.class;

    /**
     * (Optional) Refers to the classes of the result.
     */
    Class[] resultClasses() default {};

    /**
     * (Optional) The name of the SQLResultMapping.
     * @deprecated
     * @see resultSetMappings
     */
    String resultSetMapping() default "";

    /**
     * (Optional) The names of the SQLResultMappings.
     */
    String[] resultSetMappings() default {};

    /**
     * (Required) The name of the stored procedure.
     */
    String procedureName();

    /**
     * (Optional) Defines if stored procedure returns a result set.
     * This is only relevant on databases that support returning result sets
     * from stored procedures.
     */
    boolean returnsResultSet() default false;

    /**
     * (Optional) Defines if the stored procedure returns multiple result sets.
     * This is only relevant on databases that support multiple result sets from
     * stored procedures.
     */
    boolean multipleResultSets() default false;

    /**
     * (Optional) Defines if the stored procedure should be called by index or
     * by name. By index requires that the StoredProcedureParameter are defined
     * in the same order as the procedure on the database. By name requires the
     * database platform support naming procedure parameters.
     */
    boolean callByIndex() default false;

    /**
     * (Optional) Defines the parameters to the stored procedure.
     */
    StoredProcedureParameter[] parameters() default {};
}
