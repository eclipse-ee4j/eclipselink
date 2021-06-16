/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.config;

/**
 * Tuning persistence property values.
 *
 * <p>JPA persistence property usage:
 *
 * <p><code>properties.add(PersistenceUnitProperties.TUNING, TunerType.Safe);</code>
 * <p>Property values are case-insensitive.
 *
 * @see PersistenceUnitProperties#TUNING
 */
public class TunerType {
    public static final String Safe = "Safe";
    public static final String Standard = "Standard";
}
