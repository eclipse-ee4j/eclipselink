/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.persistence.ParameterMode;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static org.eclipse.persistence.annotations.Direction.IN;

/**
 * A StoredProcedureParameter annotation is used within a
 * NamedStoredProcedureQuery annotation.
 *
 * @see org.eclipse.persistence.annotations.NamedStoredProcedureQuery
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({})
@Retention(RUNTIME)
public @interface StoredProcedureParameter {
    /**
     * (Optional) The direction of the stored procedure parameter.
     * @deprecated
     * @see mode()
     */
    Direction direction() default IN;

    /**
     * (Optional) The direction of the stored procedure parameter.
     */
    ParameterMode mode() default ParameterMode.IN;

    /**
     * (Optional) Stored procedure parameter name.
     */
    String name() default "";

    /**
     * (Required) The query parameter name.
     */
    String queryParameter();

    /**
     * (Optional) Define if the parameter is required, or optional and defaulted by the procedure.
     */
    boolean optional() default false;

    /**
     * (Optional) The type of Java class desired back from the procedure,
     * this is dependent on the type returned from the procedure.
     */
    Class type() default void.class;

    /**
     * (Optional) The JDBC type code, this is dependent on the type returned
     * from the procedure.
     */
    int jdbcType() default -1;

    /**
     * (Optional) The JDBC type name, this may be required for ARRAY or
     * STRUCT types.
     */
    String jdbcTypeName() default "";
}
