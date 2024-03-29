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
package org.eclipse.persistence.nosql.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Allow a set of fields to be defined on a relationship.
 * This is a generic form of the @JoinColumns annotation, which is not specific to relational databases.
 * It can be used to map EIS and NoSQL data.
 *
 * @see NoSql
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface JoinFields {
    JoinField[] value() default {};
}
