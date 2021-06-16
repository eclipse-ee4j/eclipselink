/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//     06/12/2017-2.7 Lukas Jungmann
//       - 518155: [jpa22] add support for repeatable annotations
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;

/**
 * Allow a cache index to be define.
 * A cache index allow singleResult queries to obtain a cache hit when querying on the indexed fields.
 * resultList queries cannot obtain cache hits, as it is unknown if all of the objects are in memory,
 * (unless the cache usage query hint is used).
 * The index should be unique, but if not unique, the first indexed object will be returned.
 * Cache indexes are only relevant when caching is enabled.
 * The @CacheIndex can be defined on a Entity class, or on an attribute.
 * The column is defaulted when defined on a attribute.
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
     * Not required when annotated on a field/method.
     */
    String[] columnNames() default {};

    /**
     * Specify if the indexed field is updateable.
     * If updateable the object will be re-indexed on each update/refresh.
     */
    boolean updateable() default true;
}
