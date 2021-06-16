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
package org.eclipse.persistence.nosql.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Define a structured data type's foreign key field for an object mapped to NoSql data.
 * This is a generic form of the @JoinColumn annotation, which is not specific to relational databases.
 * It can be use to map EIS and NoSQL data.
 *
 * @see NoSql
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface JoinField {
    /**
     * (Optional) The name of the foreign key/id reference field in the source record.
     */
    String name() default "";

    /**
     * (Optional) The name of the id field in the target record.
     */
    String referencedFieldName() default "";
}
