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
//     06/12/2017-2.7 Lukas Jungmann
//       - 518155: [jpa22] add support for repeatable annotations
package org.eclipse.persistence.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Allow a cache index to be defined.
 * A cache index allows singleResult queries to obtain a cache hit when querying on the indexed fields.
 * resultList queries cannot obtain cache hits, as it is unknown if all the objects are in memory,
 * (unless the cache usage query hint is used).
 * <p>
 * The index should be unique, but if not unique, the first indexed object will be returned.
 * Cache indexes are only relevant when caching is enabled.
 * <p>
 * The CacheIndex can be defined on an Entity class, or on an attribute.
 * The column is defaulted when defined on an attribute.
 *
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
@Repeatable(CacheIndexes.class)
public @interface CacheIndex {
    /**
     * Specify the set of columns to define the index on.
     * <p>
     * Not required when annotated on a field/method.
     */
    String[] columnNames() default {};

    /**
     * Specify if the indexed field is updateable.
     * <p>
     * If updateable the object will be re-indexed on each update/refresh.
     */
    boolean updateable() default true;
}
