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
 * A BasicMap is used to map an {@linkplain org.eclipse.persistence.mappings.DirectMapMapping}, which
 * stores a collection of key-value pairs of simple types (String, Number, Date,
 * etc.). It is used in conjunction with a {@linkplain CollectionTable} which stores the key,
 * the value and a foreign key to the source object.
 * A converter may be used if the desired object type and the data type do not
 * match. This applied to both the key and value of the map.
 * <p>
 * A BasicMap can be specified within an Entity, MappedSuperclass and Embeddable
 * class.
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
public @interface BasicMap {
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
     * The name of the data column that holds the direct map key.
     * <p>
     * If the name on the key column is {@code ""},
     * the name will be defaulted to the concatenation of the following:
     * <ul>
     *  <li>the name of the property or field</li>
     *  <li>"{@code _KEY}"</li>
     * </ul>
     */
    Column keyColumn() default @Column;

    /**
     * Specify the key converter.
     * <p>
     * Default is equivalent to specifying {@code @Convert("none")},
     * meaning no converter will be added to the direct map key.
     */
    Convert keyConverter() default @Convert;

    /**
     * The name of the data column that holds the direct collection data.
     * <p>
     * Default is the property or field name.
     */
    Column valueColumn() default @Column;

    /**
     * Specify the value converter.
     * <p>
     * Default is equivalent to specifying {@code @Convert("none")},
     * meaning no converter will be added to the value column mapping.
     */
    Convert valueConverter() default @Convert;
}
