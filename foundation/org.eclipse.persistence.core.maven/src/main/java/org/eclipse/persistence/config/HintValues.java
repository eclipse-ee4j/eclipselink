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
package org.eclipse.persistence.config;

/**
 * Hint values.
 *
 * The class defines boolean values used by some EclipseLink query hints.
 *
 * <p>JPA Query Hint Usage:
 *
 * <p><code>query.setHint(QueryHints.REFRESH, HintValues.TRUE);</code>
 * <p>or
 * <p><code>@QueryHint(name=QueryHints.REFRESH, value=HintValues.TRUE)</code>
 *
 * <p>Hint values are case-insensitive.
 *
 * @see QueryHints
 */
public class HintValues {
    public static final String TRUE = "True";
    public static final String FALSE = "False";
    public static final String PERSISTENCE_UNIT_DEFAULT = "PersistenceUnitDefault";
}
