/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
