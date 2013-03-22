/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial impl
 ******************************************************************************/  
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
