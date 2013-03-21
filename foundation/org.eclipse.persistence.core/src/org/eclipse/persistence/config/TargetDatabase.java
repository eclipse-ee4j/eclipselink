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
 * Target database persistence property values.
 * 
 * <p>JPA persistence property Usage:
 * 
 * <p><code>properties.add(PersistenceUnitProperties.TargetDatabase, TargetDatabase.Oracle);</code>
 * 
 * <p>Property values are case-insensitive
 */
public class TargetDatabase {
    public static final String  Auto = "Auto";
    public static final String  Oracle = "Oracle";
    public static final String  Oracle11 = "Oracle11";
    public static final String  Oracle10 = "Oracle10g";
    public static final String  Oracle9 = "Oracle9i";
    public static final String  Oracle8 = "Oracle8i";
    public static final String  Attunity = "Attunity";
    public static final String  Cloudscape = "Cloudscape";
    public static final String  Database = "Database";
    public static final String  DB2 = "DB2";
    public static final String  DB2Mainframe = "DB2Mainframe";
    public static final String  DBase = "DBase";
    public static final String  Derby = "Derby";
    public static final String  HANA = "HANA";
    public static final String  HSQL = "HSQL";
    public static final String  Informix = "Informix";
    public static final String  JavaDB = "JavaDB";
    public static final String  MaxDB = "MaxDB";
    public static final String  MySQL4 = "MySQL4";
    public static final String  MySQL = "MySQL";
    public static final String  PointBase = "PointBase";
    public static final String  PostgreSQL = "PostgreSQL";
    public static final String  SQLAnywhere = "SQLAnywhere";
    public static final String  SQLServer = "SQLServer";
    public static final String  Sybase = "Sybase";
    public static final String  Symfoware = "Symfoware";
    public static final String  TimesTen = "TimesTen";
 
    public static final String DEFAULT = Auto;
}
