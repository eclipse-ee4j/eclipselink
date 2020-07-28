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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The Customizer annotation is used to specify a class that implements the
 * org.eclipse.persistence.config.DescriptorCustomizer
 * interface and is to run against an entity's class descriptor after all
 * metadata processing has been completed.
 *
 * The Customizer annotation may be defined on an Entity, MappedSuperclass or
 * Embeddable class. In the case of inheritance, a Customizer is not inherited
 * from its parent classes.
 *
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Customizer {
    /**
     * (Required) Defines the name of the descriptor customizer that should be
     * applied to this entity's descriptor.
     */
    Class value();
}
