/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.platform.database.oracle.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An OracleObject annotation is used to define an Oracle database OBJECT type.
 * This type can be used within PLSQL procedure calls.
 *
 * @see NamedPLSQLStoredProcedureQuery
 * @author David McCann
 * @since EclipseLink 2.5
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface OracleObject {

    /**
     * (Required) The name of the OBJECT type in the database.
     */
    String name();

    /**
     * (Optional) The Java class to map the OBJECT type to.
     * This class must be mapped using a @Struct annotation.
     */
    Class javaType() default void.class;

    /**
     * (Required) Defines the fields in the record type.
     */
    PLSQLParameter[] fields();
}
