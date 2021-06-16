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
 * Allow a database INDEX to be define when generating DDL.
 * The @Index can be defined on a Entity class, or on an attribute.
 * The column is defaulted when defined on a attribute.
 *
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
@Repeatable(Indexes.class)
public @interface Index {
    /** The name of the INDEX, defaults to {@literal INDEX_<table-name>} */
    String name() default "";

    /** The schema of the INDEX */
    String schema() default "";

    /** The catalog of the INDEX */
    String catalog() default "";

    /** The table to define the index on, defaults to entities primary table. */
    String table() default "";

    boolean unique() default false;

    /**
     * Specify the set of columns to define the index on.
     * Not required when annotated on a field/method.
     */
    String[] columnNames() default {};
}
