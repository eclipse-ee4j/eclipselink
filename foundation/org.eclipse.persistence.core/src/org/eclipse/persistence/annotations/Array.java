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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** 
 * Array types are extended object-relational data-types supported by some databases.
 * Array types are user define types in the database such as VARRAY types on Oracle.
 * Arrays can contains basic types (VARCHAR) or other Struct types, and can be stored in
 * a column or in a Struct type.
 * <p>
 * This annotation can be defined on a collection attribute that is
 * persisted to an Array type.  The collection can be of basic types, or embeddable
 * class mapped using a Struct.
 * 
 * @see org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor
 * @see org.eclipse.persistence.mappings.structures.ArrayMapping
 * @see org.eclipse.persistence.mappings.structures.ObjectArrayMapping
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Array {
    /**
     * (Optional) The basic or embeddable class that is the element
     * type of the collection.  This element is optional only if the
     * collection field or property is defined using Java generics,
     * and must be specified otherwise.  It defaults to the
     * paramterized type of the collection when defined using
     * generics.
     */
    Class targetClass() default void.class;
    
    /**
     * (Required) The database name of the database array structure type.
     */
    String databaseType();
}
