/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Struct types are extended object-relational data-types supported by some databases.
 * Struct types are user define types in the database such as OBJECT types on Oracle.
 * Structs can normally contain Arrays (VARRAY) or other Struct types, and can be stored in
 * a column or a table.
 * <p>
 * This annotation can be defined on a field/method to define
 * an {@linkplain org.eclipse.persistence.mappings.structures.StructureMapping} to an embedded Struct type.
 * The target Embeddable must be mapped using the {@linkplain Struct} annotation.
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
