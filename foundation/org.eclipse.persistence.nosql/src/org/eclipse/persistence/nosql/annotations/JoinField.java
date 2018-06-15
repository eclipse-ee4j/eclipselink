/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
