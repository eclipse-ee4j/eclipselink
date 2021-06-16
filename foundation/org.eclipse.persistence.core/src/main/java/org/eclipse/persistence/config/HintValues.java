/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
