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
//     James Sutherland - initial API and implementation
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
 * Defines a primary key generator that may be
 * referenced by name when a generator element is specified for
 * the {@linkplain jakarta.persistence.GeneratedValue} annotation.
 * <p>
 * A UUID generator may be specified on the entity class or on the primary key
 * field or property.
 * <p>
 * The scope of the generator name is global
 * to the persistence unit (across all generator types).
 * <p><b>Example:</b>
 * {@snippet :
 *  @Entity
 *  public class Employee {
 *      ...
 *      @UuidGenerator(name="uuid")
 *      @Id
 *      @GeneratedValue(generator="uuid")
 *      int id;
 *      ...
 *  }
 * }
 *
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
@Repeatable(UuidGenerators.class)
public @interface UuidGenerator {
    /**
     * The name of the UUID generator, names must be unique for the persistence unit.
     */
    String name();

}
