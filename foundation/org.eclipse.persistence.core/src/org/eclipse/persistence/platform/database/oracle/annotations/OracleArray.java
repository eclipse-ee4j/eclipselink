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
import java.util.ArrayList;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * An OracleArray annotation is used to define an Oracle database VARRAY type.
 * This type can be used within PLSQL procedure calls.
 * 
 * @see NamedPLSQLStoredProcedureQuery
 * @author David McCann
 * @since EclipseLink 2.5
 */ 
@Target({TYPE})
@Retention(RUNTIME)
public @interface OracleArray {

    /**
     * (Required) The name of the VARRAY type in the database.
     */
    String name();

    /**
     * (Required) The name of the database type this VARRAY holds onto.
     */
    String nestedType() default "VARCHAR_TYPE";
    
    /**
     * (Optional) The Java Collection class to map the VARRAY to.
     * This can be any valid Collection implementation.
     */
    Class javaType() default ArrayList.class;
}