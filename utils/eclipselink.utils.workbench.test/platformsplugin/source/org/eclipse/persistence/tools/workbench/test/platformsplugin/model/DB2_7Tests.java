/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * currently, these tests are not run because we can only use one
 * version of the DB2 driver at a time; you can run this test alone
 * if you change your classpath to point at the 7.2.5 driver
 */
public class DB2_7Tests extends PlatformTests {

	public static Test suite() {
		return new TestSuite(DB2_7Tests.class);
	}

	public DB2_7Tests(String name) {
		super(name);
	}

	protected String[] driverJARNames() {
		return new String[] {"db2java_7.2.5.zip"};
	}

	/**
	 * the DB2 7.2 server is not set up with individual accounts
	 */
	protected String userName() {
		return "mwdev1";
	}

	protected String driverClassName() {
		return "COM.ibm.db2.jdbc.net.DB2Driver";
	}

	protected String connectionURL() {
		return "jdbc:db2:" + this.serverName() + ":" + this.databaseName();
	}

	/**
	 * the DB2 7.2 server in Ottawa
	 */
	protected String serverName() {
		return "tlsvrdb4.ca.oracle.com";
	}

	protected String databaseName() {
		return "TOPLINK";
	}

	protected String platformName() {
		return "IBM DB2";
	}

	protected String expectedVersionNumber() {
		return "07.02.0009";
	}

	// TODO add tests for:
	//     LONG VARCHAR FOR BIT DATA
	//     VARCHAR() FOR BIT DATA
	//     CHAR() FOR BIT DATA
	protected void appendColumnsToTableDDL(StringBuffer sb) {
		sb.append("	BIGINT_COL					BIGINT,"); sb.append(CR);
		sb.append("	BLOB_1000_COL			BLOB(1000),"); sb.append(CR);	// size is required in version 7
		sb.append("CHAR_COL					CHAR,"); sb.append(CR);
		sb.append("CHAR_20_COL				CHAR(20),"); sb.append(CR);
		sb.append("CLOB_1000_COL			CLOB(1000),"); sb.append(CR);	// size is required in version 7
		sb.append("DATE_COL					DATE,"); sb.append(CR);
		sb.append("DECIMAL_COL				DECIMAL,"); sb.append(CR);
		sb.append("DECIMAL_10_COL			DECIMAL(10),"); sb.append(CR);
		sb.append("DECIMAL_10_2_COL		DECIMAL(10,2),"); sb.append(CR);
		sb.append("DOUBLE_COL				DOUBLE,"); sb.append(CR);
		sb.append("FLOAT_COL					FLOAT,"); sb.append(CR);
		sb.append("FLOAT_10_COL			FLOAT(10),"); sb.append(CR);
		sb.append("	INTEGER_COL				INTEGER,"); sb.append(CR);
		sb.append("LONG_VARCHAR_COL		LONG VARCHAR,"); sb.append(CR);		// size is required
		sb.append("NUMERIC_COL				NUMERIC,"); sb.append(CR);
		sb.append("NUMERIC_10_COL			NUMERIC(10),"); sb.append(CR);
		sb.append("NUMERIC_10_2_COL		NUMERIC(10,2),"); sb.append(CR);
		sb.append("REAL_COL					REAL,"); sb.append(CR);
		sb.append("SMALLINT_COL				SMALLINT,"); sb.append(CR);
		sb.append("TIME_COL					TIME,"); sb.append(CR);
		sb.append("TIMESTAMP_COL			TIMESTAMP,"); sb.append(CR);
		sb.append("VARCHAR_20_COL			VARCHAR(20)"); sb.append(CR);		// size is required
	}

	protected void verifyTable(Map metaDataMap) {
		// BIGINT
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"BIGINT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "DATA_TYPE", Types.BIGINT);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TYPE_NAME", "BIGINT");
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "COLUMN_SIZE", 19);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "IS_NULLABLE", "YES");

		// BLOB(1000)
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"BLOB_1000_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "DATA_TYPE", -98);		// ???
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "TYPE_NAME", "BLOB");
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "COLUMN_SIZE", 1000);
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "IS_NULLABLE", "YES");

		// CHAR
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"CHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TYPE_NAME", "CHARacter");
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "IS_NULLABLE", "YES");

		// CHAR(20)
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"CHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TYPE_NAME", "CHARacter");
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "IS_NULLABLE", "YES");

		// CLOB(1000)
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"CLOB_1000_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "DATA_TYPE", -99);
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "TYPE_NAME", "CLOB");
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "COLUMN_SIZE", 1000);
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "IS_NULLABLE", "YES");

		// DATE
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"DATE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "DATA_TYPE", Types.DATE);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TYPE_NAME", "DATE");
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "IS_NULLABLE", "YES");

		// DECIMAL
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"DECIMAL_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TYPE_NAME", "DECimal");
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "COLUMN_SIZE", 5);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "IS_NULLABLE", "YES");

		// DECIMAL(10)
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"DECIMAL_10_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "TYPE_NAME", "DECimal");
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "IS_NULLABLE", "YES");

		// DECIMAL(10,2)
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"DECIMAL_10_2_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "TYPE_NAME", "DECimal");
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "DECIMAL_DIGITS", 2);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "IS_NULLABLE", "YES");

		// DOUBLE
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"DOUBLE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TYPE_NAME", "FLOAT");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_SIZE", 15);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "IS_NULLABLE", "YES");

		// FLOAT
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"FLOAT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TYPE_NAME", "FLOAT");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "COLUMN_SIZE", 15);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "IS_NULLABLE", "YES");

		// FLOAT(10)
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"FLOAT_10_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "DATA_TYPE", Types.REAL);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "TYPE_NAME", "REAL");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "COLUMN_SIZE", 7);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "IS_NULLABLE", "YES");

		// INTEGER
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"INTEGER_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "DATA_TYPE", Types.INTEGER);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TYPE_NAME", "INTeger");
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "IS_NULLABLE", "YES");

		// LONG VARCHAR
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"LONG_VARCHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "DATA_TYPE", Types.LONGVARCHAR);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "TYPE_NAME", "LONG VARCHAR");
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "COLUMN_SIZE", 32700);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "IS_NULLABLE", "YES");

		// NUMERIC
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NUMERIC_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TYPE_NAME", "DECimal");
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "COLUMN_SIZE", 5);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "IS_NULLABLE", "YES");

		// NUMERIC(10)
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NUMERIC_10_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "TYPE_NAME", "DECimal");
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "IS_NULLABLE", "YES");

		// NUMERIC(10,2)
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NUMERIC_10_2_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "TYPE_NAME", "DECimal");
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "DECIMAL_DIGITS", 2);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "IS_NULLABLE", "YES");

		// REAL
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"REAL_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "DATA_TYPE", Types.REAL);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TYPE_NAME", "REAL");
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "COLUMN_SIZE", 7);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "IS_NULLABLE", "YES");

		// SMALLINT
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"SMALLINT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "DATA_TYPE", Types.SMALLINT);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TYPE_NAME", "SMALLINT");
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "COLUMN_SIZE", 5);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "IS_NULLABLE", "YES");

		// TIME
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"TIME_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "DATA_TYPE", Types.TIME);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TYPE_NAME", "TIME");
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "COLUMN_SIZE", 8);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "IS_NULLABLE", "YES");

		// TIMESTAMP
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"TIMESTAMP_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "DATA_TYPE", Types.TIMESTAMP);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TYPE_NAME", "TIMESTAMP");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "COLUMN_SIZE", 26);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "DECIMAL_DIGITS", 6);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "IS_NULLABLE", "YES");

		// VARCHAR(20)
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"VARCHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TYPE_NAME", "VARCHAR");
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "IS_NULLABLE", "YES");

	}

}
