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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** 
 * Struct types are extended object-relational data-types supported by some databases.
 * Struct types are user define types in the database such as OBJECT types on Oracle.
 * Structs can normally contains Arrays (VARRAY) or other Struct types, and can be stored in
 * a column or a table.
 * <p>
 * This annotation define a class to map to a database Struct type.
 * The class should normally be an Embeddable, but could also be an Entity if stored in a object table.
 * 
 * @see org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor
 * @see org.eclipse.persistence.mappings.structures.StructureMapping
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Struct {
    /**
     * (Required) The database name of the database structure type.
     */
    String name();

    /**
     * (Optional) Defines the order of the fields contained in the database structure type.
     */
    String[] fields() default {}; 
}
