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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.config;

/**
 * Query type hint values.
 * 
 * The class contains all the valid values for QueryHints.QUERY_TYPE query hint.
 * A fully qualified class name of a valid subclass of DatabaseQuery can also be used.
 * <p>i.e. "org.acme.persistence.CustomQuery"
 * 
 * JPA Query Hint Usage:
 * 
 * <p><code>query.setHint(QueryHints.QUERY_TYPE, QueryType.ReadObject);</code>
 * <p>or 
 * <p><code>@QueryHint(name=QueryHints.QUERY_TYPE, value=QueryType.ReadObject)</code>
 * 
 * <p>Hint values are case-insensitive.
 * "" could be used instead of default value CacheUsage.DEFAULT.
 * 
 * @see QueryHints#QUERY_TYPE
 * @see org.eclipse.persistence.queries.DatabaseQuery
 */
public class QueryType {
    public static final String  Auto = "Auto";
    public static final String  ReadObject = "ReadObject";
    public static final String  ReadAll = "ReadAll";
    public static final String  Report = "Report";
    public static final String  ResultSetMapping = "ResultSetMapping";
    
    public static final String  DeleteAll = "DeleteAll";
    public static final String  UpdateAll = "UpdateAll";
    
    public static final String  DataRead = "DataRead";
    public static final String  DataModify = "DataModify";
    public static final String  ValueRead = "ValueRead";
    public static final String  DirectRead = "DirectRead";
 
    public static final String DEFAULT = Auto;
}
