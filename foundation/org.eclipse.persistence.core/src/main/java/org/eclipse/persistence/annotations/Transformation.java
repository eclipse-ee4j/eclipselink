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
//     Andrei Ilitchev (Oracle), March 7, 2008
//        - New file introduced for bug 211300.
package org.eclipse.persistence.annotations;

import jakarta.persistence.FetchType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Transformation is an optional annotation for {@linkplain org.eclipse.persistence.mappings.TransformationMapping}.
 * {@linkplain org.eclipse.persistence.mappings.TransformationMapping} allows to map an attribute to one or more database columns.
 * <p>
 * Transformation annotation is an optional part of {@linkplain org.eclipse.persistence.mappings.TransformationMapping} definition.
 * Unless the {@linkplain org.eclipse.persistence.mappings.TransformationMapping} is write-only, it should have a {@linkplain ReadTransformer}
 * defining transformation of database column(s) value(s)into attribute value.
 * Also, unless it's a read-only mapping, either {@linkplain WriteTransformer} annotation or {@linkplain WriteTransformers} annotation
 * should be specified. Each {@linkplain WriteTransformer} defines transformation of the attribute value to a single
 * database column value (column is specified in the {@linkplain WriteTransformer}).
 * <p>
 * Transformation can be specified within an Entity, MappedSuperclass and Embeddable class.
 *
 * @see ReadTransformer
 * @see WriteTransformer
 * @see WriteTransformers
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Transformation {
    /**
     * Defines whether the value of the field or property should
     * be lazily loaded or must be eagerly fetched. The {@linkplain jakarta.persistence.FetchType#EAGER} strategy is a
     * requirement on the persistence provider runtime that the value must be
     * eagerly fetched. The {@linkplain jakarta.persistence.FetchType#LAZY} strategy is a hint to the persistence provider
     * runtime.
     * <p>
     * If not specified, defaults to {@linkplain jakarta.persistence.FetchType#EAGER}.
     */
    FetchType fetch() default FetchType.EAGER;

    /**
     * The optional element is a hint whether the value
     * of the field or property may be null. It is disregarded
     * for primitive types, which are considered non-optional.
     */
    boolean optional() default true;
}
