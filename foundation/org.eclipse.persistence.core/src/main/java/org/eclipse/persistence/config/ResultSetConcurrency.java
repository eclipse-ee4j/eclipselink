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
//     James Sutherland - initial impl
package org.eclipse.persistence.config;

/**
 * JDBC ResultSet concurrency hint values.
 *
 * The class contains all the valid values for QueryHints.RESULT_SET_CONCURRENCY query hint.
 * This can be used on ScrollableCursor queries to set the JDBC ResultSet concurrency.
 *
 * JPA Query Hint Usage:
 *
 * <p><code>query.setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.ForwardOnly);</code>
 * <p>or
 * <p><code>@QueryHint(name=QueryHints.RESULT_SET_CONCURRENCY, value=ResultSetConcurrency.ForwardOnly)</code>
 *
 * <p>Hint values are case-insensitive.
 * "" could be used instead of default value ResultSetConcurrency.Updatable.
 *
 * @see QueryHints#RESULT_SET_CONCURRENCY
 * @see org.eclipse.persistence.queries.ScrollableCursorPolicy#setResultSetConcurrency(int)
 *
 * @author James Sutherland
 */
public class ResultSetConcurrency {
    /** The concurrency mode for a ResultSet object that may be updated. */
    public static final String  Updatable = "Updatable";

    /** The concurrency mode for a ResultSet object that may NOT be updated. */
    public static final String  ReadOnly = "ReadOnly";

    /**
     * The default type is Updatable.
     */
    public static final String DEFAULT = Updatable;
}
