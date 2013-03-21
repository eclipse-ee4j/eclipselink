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
 * Struct types are extended object-relational data-types supported by some databases.
 * Struct types are user define types in the database such as OBJECT types on Oracle.
 * Structs can normally contains Arrays (VARRAY) or other Struct types, and can be stored in
 * a column or a table.
 * <p>
 * This annotation can be defined on a field/method to define an StructureMapping to an embedded Struct type.
 * The target Embeddable must be mapped using the Struct annotation.
 * 
 * @see Struct
 * @see org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor
 * @see org.eclipse.persistence.mappings.structures.StructureMapping
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Structure {
}
