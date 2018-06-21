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
//     03/26/2008-1.0M6 Guy Pelletier
//       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A DiscriminatorClass is used within a VariableOneToOne annotation.
 *
 * @author Guy Pelletier
 * @since Eclipselink 1.0
 */
@Target({})
@Retention(RUNTIME)
public @interface DiscriminatorClass {
    /**
     * (Required) The discriminator to be stored on the database.
     */
    String discriminator();

    /**
     * (Required) The class to the instantiated with the given discriminator.
     */
    Class value();
}
