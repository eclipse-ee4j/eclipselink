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
 * Result type hint values.
 * 
 * The class contains all the valid values for QueryHints.RESULT_TYPE query hint.
 * By default in JPA for non-single select queries an Array of values is returned.
 * If getSingleResult() is called the first array is returned, for getResultList() a List of arrays is returned.
 * <p>i.e. "Select e.firstName, e.lastName from Employee e" returns List<Object[]>
 * <p>or the native query, "SELECT * FROM EMPLOYEE" returns List<Object[]>
 * <p>The ResultType can be used to instead return a Map of values (DatabaseRecord, ReportQueryResult).
 * <p>It can also be used to return a single column, or single value.
 * 
 * JPA Query Hint Usage:
 * 
 * <p><code>query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);</code>
 * <p>or 
 * <p><code>@QueryHint(name=QueryHints.RESULT_TYPE, value=ResultType.Map)</code>
 * 
 * <p>Hint values are case-insensitive.
 * "" could be used instead of default value ResultType.Array.
 * 
 * @see QueryHints#RESULT_TYPE
 * @see org.eclipse.persistence.sessions.Record
 * @see org.eclipse.persistence.sessions.DatabaseRecord
 * @see org.eclipse.persistence.queries.ReportQueryResult
 * @see org.eclipse.persistence.queries.ReportQuery#setReturnType(int)
 * @see org.eclipse.persistence.queries.DataReadQuery#setUseAbstractRecord(boolean)
 * 
 * @author James Sutherland
 */
public class ResultType {
    /**
     * An Object array of values is returned (List<Object[]> or Object[]).
     */
    public static final String  Array = "Array";
    
    /**
     * A Map of key value pairs is returned.
     * For a native queries the keys are the column names, (List<DatabaseRecord>, or DatabaseRecord).
     * For JPQL queries the keys are the attribute names, (List<ReportQueryResult>, or ReportQueryResult).
     */
    public static final String  Map = "Map";

    /**
     * A List of the first selected value is returned.
     */
    public static final String  Attribute = "Attribute";

    /**
     * The first value of the first rows is returned.
     */
    public static final String  Value = "Value";    

 
    /**
     * The default type is Array.
     */
    public static final String DEFAULT = Array;
}
