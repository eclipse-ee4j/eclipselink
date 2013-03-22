/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.platform.database.oracle.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.persistence.QueryHint;

/** 
 * A PLSQLNamedStoredProcedureQuery annotation allows the definition of queries that 
 * call PLSQL stored procedures as named queries.
 * The PLSQL support adds support for complex PLSQL types such as RECORD and TABLE types,
 * that are not accessible from JDBC.
 * 
 * A PLSQLNamedStoredProcedureQuery annotation may be defined on an Entity or
 * MappedSuperclass.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.3
 */ 
@Target({TYPE})
@Retention(RUNTIME)
public @interface NamedPLSQLStoredProcedureQuery {
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
     */
    Class resultClass() default void.class;

    /**
     * (Optional) The name of the SQLResultMapping.
     */
    String resultSetMapping() default "";

    /**
     * (Required) The name of the stored procedure.
     */
    String procedureName();
    
    /**
     * (Optional) Defines the parameters to the stored procedure.
     */
    PLSQLParameter[] parameters() default {};
}
