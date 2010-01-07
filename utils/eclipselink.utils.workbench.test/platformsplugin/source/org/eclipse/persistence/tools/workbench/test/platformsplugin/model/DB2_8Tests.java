/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

public class DB2_8Tests extends DB2_7Tests {

	public static Test suite() {
		return new TestSuite(DB2_8Tests.class);
	}

	public DB2_8Tests(String name) {
		super(name);
	}

	protected String[] driverJARNames() {
		return new String[] {"db2java_8.1.zip", "db2jcc_8.1.jar"};
	}

	protected String userName() {
		return this.defautlUserName();
	}

	/**
	 * the DB2 8.1 server in Ottawa
	 */
	protected String serverName() {
		return "tlsvrdb2.ca.oracle.com";
	}

	protected String platformName() {
		return "IBM DB2";
	}

	protected String expectedVersionNumber() {
		return "08.02.0003";
	}

	protected void appendColumnsToTableDDL(StringBuffer sb) {
		sb.append("	BLOB_COL			BLOB,"); sb.append(CR);
		sb.append("CLOB_COL			CLOB,"); sb.append(CR);

// not sure which version these belong to since the servers are not
// configured to support these types:
//		sb.append("DBCLOB_COL			DBCLOB,"); sb.append(CR);
//		sb.append("GRAPHIC_COL			GRAPHIC,"); sb.append(CR);
//		sb.append("LONG_VARGRAPHIC_COL			LONG VARGRAPHIC,"); sb.append(CR);
//		sb.append("VARGRAPHIC_COL			VARGRAPHIC,"); sb.append(CR);

		super.appendColumnsToTableDDL(sb);
	}

	/**
	 * there are a number of differences between versions 7 and 8,
	 * so we simply override this method and don't call
	 * super.verifyTable(Map)
	 */
	protected void verifyTable(Map metaDataMap) {
		// BIGINT
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"BIGINT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "DATA_TYPE", Types.BIGINT);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TYPE_NAME", "BIGINT");
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "COLUMN_SIZE", 19);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "IS_NULLABLE", "YES");

		// BLOB
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"BLOB_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "DATA_TYPE", Types.BLOB);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TYPE_NAME", "BLOB");
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "COLUMN_SIZE", 1048576);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "IS_NULLABLE", "YES");

		// BLOB(1000)
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"BLOB_1000_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BLOB_1000_COL", "DATA_TYPE", Types.BLOB);
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
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TYPE_NAME", "CHAR");
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
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TYPE_NAME", "CHAR");
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "IS_NULLABLE", "YES");

		// CLOB
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"CLOB_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "DATA_TYPE", Types.CLOB);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "TYPE_NAME", "CLOB");
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "COLUMN_SIZE", 1048576);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "IS_NULLABLE", "YES");

		// CLOB(1000)
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"CLOB_1000_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CLOB_1000_COL", "DATA_TYPE", Types.CLOB);
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
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TYPE_NAME", "DECIMAL");
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
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "TYPE_NAME", "DECIMAL");
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
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "TYPE_NAME", "DECIMAL");
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
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TYPE_NAME", "DOUBLE");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_SIZE", 53);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NUM_PREC_RADIX", 2);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "IS_NULLABLE", "YES");

		// FLOAT
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"FLOAT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TYPE_NAME", "DOUBLE");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "COLUMN_SIZE", 53);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "NUM_PREC_RADIX", 2);
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
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "COLUMN_SIZE", 24);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "NUM_PREC_RADIX", 2);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "IS_NULLABLE", "YES");

		// INTEGER
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"INTEGER_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "DATA_TYPE", Types.INTEGER);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TYPE_NAME", "INTEGER");
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
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TYPE_NAME", "DECIMAL");
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
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "TYPE_NAME", "DECIMAL");
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
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "TYPE_NAME", "DECIMAL");
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
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "COLUMN_SIZE", 24);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "NUM_PREC_RADIX", 2);
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
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "DECIMAL_DIGITS", 0);
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

		// VARCHAR
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
