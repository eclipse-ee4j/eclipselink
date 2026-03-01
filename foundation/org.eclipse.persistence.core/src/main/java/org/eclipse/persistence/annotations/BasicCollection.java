/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.annotations;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A BasicCollection is used to map an {@linkplain org.eclipse.persistence.mappings.DirectCollectionMapping},
 * which stores a collection of simple types (String, Number, Date, etc.). It is used in conjunction with
 * a {@linkplain CollectionTable} which stores the value and a foreign key to the source object.
 * <p>
 * Converters may be used if the desired object type and the data type do not match.
 * <p>
 * A BasicCollection can be specified on within an Entity, MappedSuperclass
 * and Embeddable class.
 *
 * @see CollectionTable
 * @see Convert
 * @see Converter
 * @see ObjectTypeConverter
 * @see TypeConverter
 *
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 * @deprecated Use {@linkplain jakarta.persistence.ElementCollection}.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Deprecated
public @interface BasicCollection {
    /**
     * Defines whether the value of the field or property should
     * be lazily loaded or must be eagerly fetched. The {@linkplain jakarta.persistence.FetchType#EAGER} strategy is a
     * requirement on the persistence provider runtime that the value must be
     * eagerly fetched. The {@linkplain jakarta.persistence.FetchType#LAZY} strategy is a hint to the persistence provider
     * runtime.
     * <p>
     * If not specified, defaults to {@linkplain jakarta.persistence.FetchType#LAZY}.
     */
    FetchType fetch() default FetchType.LAZY;

    /**
     * The name of the value column that holds the direct collection data.
     * <p>
     * Defaults to the property or field name.
     */
    Column valueColumn() default @Column;
}
