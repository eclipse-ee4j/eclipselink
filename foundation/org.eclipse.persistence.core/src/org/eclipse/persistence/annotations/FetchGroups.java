/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     01/19/2010-2.1 Guy Pelletier
//       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A FetchGroups annotation allows the definition of multiple FetchGroup. It
 * may be specified on an Entity or MappedSuperclass.
 *
 * @see org.eclipse.persistence.annotations.FetchGroup
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.1
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface FetchGroups {
    /**
     * (Required) An array of fetch group.
     */
    FetchGroup[] value();
}
