/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.platform.database.oracle.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A PLSQLTable annotation is used to define a database PLSQL TABLE type.
 * This type can be used within PLSQL procedure calls.
 *
 * @see NamedPLSQLStoredProcedureQuery
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface PLSQLTable {

    /**
     * (Required) The name of the record type in the database.
     */
    String name();

    /**
     * (Required) The name of the database VARRAY type that mirrors the table's structure.
     * The table will be converted to/from this type so it can be passed through JDBC.
     */
    String compatibleType();

    /**
     * (Required) The name of the database OBJECT TYPE or VARRAY type that mirrors the record's structure.
     * The record will be converted to/from this type so it can be passed through JDBC.
     */
    String nestedType() default "VARCHAR_TYPE";

    /**
     * (Optional) The Java Collection class to map the varray to.
     * This can be any valid Collection implementation.
     */
    Class javaType() default ArrayList.class;

    /**
     * (Optional) Indicates a non-associative (nested) table.
     * This method would typically be used when generating a constructor for the
     * collection in PL/SQL (as the constructors for associative arrays (Varray)
     * and a non-associative (nested) tables differ).
     */
    boolean isNestedTable() default false;
}
