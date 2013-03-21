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

import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static org.eclipse.persistence.annotations.Direction.IN;

/** 
 * A PLSQLParameter annotation is used within a 
 * NamedPLSQLStoredProcedureQuery or PLSQLRecord annotation.
 * 
 * @see NamedPLSQLStoredProcedureQuery
 * @see PLSQLRecord
 * @author James Sutherland
 * @since EclipseLink 2.3
 */ 
@Target({})
@Retention(RUNTIME)
public @interface PLSQLParameter {
    /**
     * (Optional) The direction of the stored procedure parameter.
     */
    Direction direction() default IN;

    /**
     * (Required) Stored procedure parameter name.
     */
    String name();

    /**
     * (Optional) The query parameter name.
     */
    String queryParameter() default "";
    
    /**
     * (Optional) Define if the parameter is required, or optional and defaulted by the procedure.
     */
    boolean optional() default false;
    
    /**
     * (Optional) The database data-type for the paramter.
     * This either one of the type constants defined in OraclePLSQLTypes, or JDBCTypes,
     * or a custom record or table type name.
     * @see PLSQLRecord
     * @see OraclePLSQLTypes
     * @see JDBCTypes
     */
    String databaseType() default "VARCHAR_TYPE";
    
    /**
     * (Optional) The max length of the field value.
     */
    int length() default 255;
    
    /**
     * (Optional) If a numeric, the max scale value.
     */
    int scale() default 0;

    /**
     * (Optional) If a numeric, the max precision value.
     */
    int precision() default 0;
}
