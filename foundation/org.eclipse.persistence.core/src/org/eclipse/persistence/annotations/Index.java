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
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * Allow a database INDEX to be define when generating DDL.
 * The @Index can be defined on a Entity class, or on an attribute.
 * The column is defaulted when defined on a attribute.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.2
 */ 
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
public @interface Index {
    /** The name of the INDEX, defaults to INDEX_<table-name> */
    String name() default "";
    
    /** The schema of the INDEX */
    String schema() default "";
    
    /** The catalog of the INDEX */
    String catalog() default "";
    
    /** The table to define the index on, defaults to entities primary table. */
    String table() default "";
    
    boolean unique() default false;
    
    /**
     * Specify the set of columns to define the index on.
     * Not required when annotated on a field/method.
     */
    String[] columnNames() default {};
}
