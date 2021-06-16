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
 * JDBC ResultSet type hint values.
 *
 * The class contains all the valid values for QueryHints.RESULT_SET_TYPE query hint.
 * This can be used on ScrollableCursor queries to set the JDBC ResultSet scroll type.
 *
 * JPA Query Hint Usage:
 *
 * <p><code>query.setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.ForwardOnly);</code>
 * <p>or
 * <p><code>@QueryHint(name=QueryHints.RESULT_SET_TYPE, value=ResultSetType.ForwardOnly)</code>
 *
 * <p>Hint values are case-insensitive.
 * "" could be used instead of default value ResultSetType.ScrollInsensitive.
 *
 * @see QueryHints#RESULT_SET_TYPE
 * @see org.eclipse.persistence.queries.ScrollableCursorPolicy#setResultSetType(int)
 *
 * @author James Sutherland
 */
public class ResultSetType {
    /** The rows in a result set will be processed in a forward direction; first-to-last. */
    public static final String  Forward = "Forward";

    /** The type for a ResultSet object whose cursor may move only forward. */
    public static final String  ForwardOnly = "ForwardOnly";

    /** The order in which rows in a result set will be processed is unknown. */
    public static final String  Unknown = "Unknown";

    /** The rows in a result set will be processed in a reverse direction; last-to-first. */
    public static final String  Reverse = "Reverse";

    /** The type for a ResultSet object that is scrollable but generally not sensitive to changes made by others. */
    public static final String  ScrollInsensitive = "ScrollInsensitive";

    /** The type for a ResultSet object that is scrollable and generally sensitive to changes made by others. */
    public static final String  ScrollSensitive = "ScrollSensitive";

    /**
     * The default type is ScrollInsensitive.
     */
    public static final String DEFAULT = ScrollInsensitive;
}
