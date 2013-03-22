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

/** 
 * A PLSQLRecord annotation is used to define a database PLSQL RECORD type.
 * This type can be used within PLSQL procedure calls.
 * 
 * @see NamedPLSQLStoredProcedureQuery
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface PLSQLRecord {

    /**
     * (Required) The name of the record type in the database.
     */
    String name();

    /**
     * (Required) The name of the database OBJECT TYPE that mirrors the record's structure.
     * The record will be converted to/from this type so it can be passed through JDBC.
     */
    String compatibleType();
    
    /**
     * (Optional) The Java class to map the object-type to.
     * This class must be mapped using a @Struct annotation.
     */
    Class javaType() default void.class;
    
    /**
     * (Required) Defines the fields in the record type.
     */
    PLSQLParameter[] fields();
}
