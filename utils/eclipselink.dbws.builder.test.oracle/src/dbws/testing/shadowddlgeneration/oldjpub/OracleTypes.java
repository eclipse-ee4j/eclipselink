/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package dbws.testing.shadowddlgeneration.oldjpub;

public class OracleTypes {

    // stolen from oracle.jdbc.OracleTypes - don't want dependency on oracle-specific imports

    public static final int ARRAY = 2003;
    public static final int BFILE = -13;
    public static final int BIGINT = -5;
    public static final int BINARY = -2;
    public static final int BINARY_DOUBLE = 101;
    public static final int BINARY_FLOAT = 100;
    public static final int BIT = -7;
    public static final int BLOB = 2004;
    public static final int BOOLEAN = 16;
    public static final int CHAR = 1;
    public static final int CLOB = 2005;
    public static final int CURSOR = -10;
    public static final int DATALINK = 70;
    public static final int DATE = 91;
    public static final int DECIMAL = 3;
    public static final int DOUBLE = 8;
    public static final int FIXED_CHAR = 999;
    public static final int FLOAT = 6;
    public static final int INTEGER = 4;
    public static final int JAVA_OBJECT = 2000;
    public static final int JAVA_STRUCT = 2008;
    public static final int LONGVARBINARY = -4;
    public static final int LONGVARCHAR = -1;
    public static final int NULL = 0;
    public static final int NUMBER = 2;
    public static final int NUMERIC = 2;
    public static final int OPAQUE = 2007;
    public static final int OTHER = 1111;
    public static final int PLSQL_INDEX_TABLE = -14;
    public static final int RAW = -2;
    public static final int REAL = 7;
    public static final int REF = 2006;
    public static final int ROWID = -8;
    public static final int SMALLINT = 5;
    public static final int STRUCT = 2002;
    public static final int TIME = 92;
    public static final int TIMESTAMP = 93;
    public static final int TIMESTAMPLTZ = -102;
    public static final int TIMESTAMPNS = -100;
    public static final int TIMESTAMPTZ = -101;
    public static final int TINYINT = -6;
    public static final int VARBINARY = -3;
    public static final int VARCHAR = 12;

    public static final int SQL_STATEMENTS = 1996;
    public static final int PACKAGE = 1999;
    public static final int TOPLEVEL = 1998;
    public static final int UNSUPPORTED = 1997;
    public static final int TABLE = 1995;
    public static final int INTERVALYM = 1994;
    public static final int INTERVALDS = 1993;
    public static final int PLSQL_RECORD = 1992;
    public static final int PLSQL_NESTED_TABLE = 1991;
    public static final int PLSQL_VARRAY_TABLE = 1990;
    public static final int AQTYPE = 5001;
    public static final int ANYDATA = 5002;
    public static final int SERIALIZABLE_JAVA_TYPE = 5003;
    public static final int JAVA_TYPE = 5004;
}
