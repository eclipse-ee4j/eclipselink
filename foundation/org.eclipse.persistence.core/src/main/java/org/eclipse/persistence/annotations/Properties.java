/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Andrei Ilitchev (Oracle), April 8, 2008
//        - New file introduced for bug 217168.
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for user defined properties.
 * A single Property may be specified directly on a mapped attribute or its get/set method.
 * Multiple Properties should be wrapped into Properties annotation.
 *
 * Properties may be assigned to either a mapped attribute (or its get/set method)
 * within Entity, MappedSuperclass and Embeddable class;
 * or to Entity, MappedSuperclass and Embeddable class.
 *
 * In case orm xml is used Property an Properties annotations specified on mapped attributes are ignored,
 * specified on a class are merged with the ones specified in orm xml with the latter taking precedence in case of conflicts.
 *
 * Properties defined in MappedSuperclass are passed to all inheriting Entities and MappedSuperclasses.
 * In case of a conflict property value defined directly on a class always overrides the value passed from class's parent.
 *
 * @see jakarta.persistence.Embeddable
 * @see jakarta.persistence.Entity
 * @see jakarta.persistence.Id
 * @see jakarta.persistence.Basic
 * @see jakarta.persistence.ManyToMany
 * @see jakarta.persistence.ManyToOne
 * @see jakarta.persistence.MappedSuperclass
 * @see jakarta.persistence.OneToMany
 * @see jakarta.persistence.OneToOne
 * @see org.eclipse.persistence.annotations.BasicCollection
 * @see org.eclipse.persistence.annotations.BasicMap
 * @see org.eclipse.persistence.annotations.Property
 * @see org.eclipse.persistence.annotations.Transformation
 * @see org.eclipse.persistence.annotations.VariableOneToOne
 *
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0
 */
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
public @interface Properties {
    Property[] value();
}
