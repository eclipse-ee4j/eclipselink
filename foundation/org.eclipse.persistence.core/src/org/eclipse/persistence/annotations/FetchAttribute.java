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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A fetch attribute is specified within a fetch group and is used as a
 * performance enhancement that allows a group of attributes of an object to be
 * loaded on demand, which means that the data for an attribute might not loaded
 * from the underlying data source until an explicit access call for the
 * attribute first occurs. It avoids the wasteful practice of loading up all
 * data of the object's attributes, in which the user is interested in only
 * partial of them.
 *
 * A great deal of caution and careful system use case analysis should be use
 * when using the fetch group feature, as the extra round-trip would well offset
 * the gain from the deferred loading in many cases.
 *
 * EclipseLink fetch group support is twofold: the pre-defined fetch groups at
 * the Entity or MappedSuperclass level; and dynamic (use case) fetch groups at
 * the query level.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.1
 */
@Target({})
@Retention(RUNTIME)
public @interface FetchAttribute {
    /**
     * (Required) The fetch attribute name.
     */
    String name();
}
