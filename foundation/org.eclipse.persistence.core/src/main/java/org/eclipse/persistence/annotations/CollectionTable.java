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

import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.UniqueConstraint;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *  A CollectionTable annotation is used in conjunction with a {@linkplain BasicCollection}
 *  or a {@linkplain BasicMap} annotation. However, if a CollectionTable is not defined, one
 *  will be defaulted.
 *
 * @see BasicMap
 * @see BasicCollection
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 *
 * @deprecated Use {@linkplain jakarta.persistence.CollectionTable}.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Deprecated
public @interface CollectionTable {
    /**
     * The name of the collection table.
     * <p>
     * If it is not specified, it is defaulted to the concatenation of the following:
     * <ul>
     * <li>the name of the source entity</li>
     * <li>"{@code _}"</li>
     * <li>the name of the relationship property or field of the source entity.</li>
     * </ul>
     */
    String name() default "";

    /**
     * The catalog of the table.
     * <p>
     * It defaults to the persistence unit default catalog.
     */
    String catalog() default "";

    /**
     * The schema of the table.
     * <p>
     * It defaults to the persistence unit default schema.
     */
    String schema() default "";

    /**
     * Used to specify a primary key column that is used as a foreign
     * key to join to another table. If the source entity uses a composite
     * primary key, a primary key join column must be specified for each field
     * of the composite primary key. In a single primary key case, a primary key
     * join column may optionally be specified.
     * <p>
     * Defaulting applies otherwise as follows:
     * <ul>
     * <li><em>name</em>, the same name as the primary key column of the primary table of the
     * source entity.</li>
     * <li><em>referencedColumnName</em>, the same name of primary key column of the primary
     * table of the source entity.</li>
     * </ul>
     */
    PrimaryKeyJoinColumn[] primaryKeyJoinColumns() default {};

    /**
     * Unique constraints that are to be placed on the table. These
     * are only used if table generation is in effect.
     */
    UniqueConstraint[] uniqueConstraints() default {};
}
