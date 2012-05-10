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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Sybase12_0Tests extends PlatformTests {

	public static Test suite() {
		return new TestSuite(Sybase12_0Tests.class);
	}

	public Sybase12_0Tests(String name) {
		super(name);
	}

	protected String[] driverJARNames() {
		return new String[] {"jconn2.jar"};
	}

	protected String driverClassName() {
		return "com.sybase.jdbc2.jdbc.SybDriver";
	}

	protected String connectionURL() {
		return "jdbc:sybase:Tds:" + this.serverName() + ":5001/" + this.databaseName();
	}

	/**
	 * the Sybase Adaptive Server Enterprise 12.0 in Ottawa
	 */
	protected String serverName() {
		return "tlsvrdb7.ca.oracle.com";
	}

	protected String expectedVersionNumber() {
		return "12.0";
	}

	/**
	 * the Sybase server is not set up with individual accounts
	 */
	protected String userName() {
		return "MWDEV1";
	}

	protected String databaseName() {
		return this.userName();
	}

	protected void appendColumnsToTableDDL(StringBuffer sb) {
		sb.append("BINARY_COL				BINARY NULL,"); sb.append(CR);
		sb.append("BINARY_100_COL			BINARY(100) NULL,"); sb.append(CR);
		sb.append("BIT_COL						BIT,"); sb.append(CR);		// NULL not allowed
		sb.append("CHAR_COL					CHAR NULL,"); sb.append(CR);
		sb.append("CHAR_20_COL				CHAR(20) NULL,"); sb.append(CR);
		sb.append("DATETIME_COL			DATETIME NULL,"); sb.append(CR);
		sb.append("DECIMAL_COL				DECIMAL NULL,"); sb.append(CR);
		sb.append("DECIMAL_10_COL			DECIMAL(10) NULL,"); sb.append(CR);
		sb.append("DECIMAL_10_2_COL		DECIMAL(10,2) NULL,"); sb.append(CR);
		sb.append("DOUBLE_COL				DOUBLE PRECISION NULL,"); sb.append(CR);
		sb.append("FLOAT_COL					FLOAT NULL,"); sb.append(CR);
		sb.append("FLOAT_10_COL			FLOAT(10) NULL,"); sb.append(CR);
		sb.append("IMAGE_COL					IMAGE NULL,"); sb.append(CR);
		sb.append("INT_COL						INT NULL,"); sb.append(CR);
		sb.append("MONEY_COL				MONEY NULL,"); sb.append(CR);
		sb.append("NCHAR_COL					NCHAR NULL,"); sb.append(CR);
		sb.append("NCHAR_20_COL			NCHAR(20) NULL,"); sb.append(CR);
		sb.append("NUMERIC_COL				NUMERIC NULL,"); sb.append(CR);
		sb.append("NUMERIC_10_COL			NUMERIC(10) NULL,"); sb.append(CR);
		sb.append("NUMERIC_10_2_COL		NUMERIC(10,2) NULL,"); sb.append(CR);
		sb.append("NVARCHAR_COL			NVARCHAR NULL,"); sb.append(CR);
		sb.append("NVARCHAR_20_COL		NVARCHAR(20) NULL,"); sb.append(CR);
		sb.append("REAL_COL					REAL NULL,"); sb.append(CR);
		sb.append("SMALLDATETIME_COL	SMALLDATETIME NULL,"); sb.append(CR);
		sb.append("SMALLINT_COL				SMALLINT NULL,"); sb.append(CR);
		sb.append("SMALLMONEY_COL		SMALLMONEY NULL,"); sb.append(CR);
		sb.append("TEXT_COL					TEXT NULL,"); sb.append(CR);
		sb.append("TINYINT_COL				TINYINT NULL,"); sb.append(CR);
		sb.append("VARBINARY_COL			VARBINARY NULL,"); sb.append(CR);
		sb.append("VARBINARY_20_COL		VARBINARY(20) NULL,"); sb.append(CR);
		sb.append("VARCHAR_COL				VARCHAR NULL,"); sb.append(CR);
		sb.append("VARCHAR_20_COL			VARCHAR(20) NULL"); sb.append(CR);
	}

	protected void verifyTable(Map metaDataMap) {
		// BINARY
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"BINARY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "DATA_TYPE", Types.BINARY);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TYPE_NAME", "binary");
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "IS_NULLABLE", "YES");

		// BINARY(100)
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"BINARY_100_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "DATA_TYPE", Types.BINARY);
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "TYPE_NAME", "binary");
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "COLUMN_SIZE", 100);
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_100_COL", "IS_NULLABLE", "YES");

		// BIT
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"BIT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "DATA_TYPE", Types.BIT);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TYPE_NAME", "bit");
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "NUM_PREC_RADIX", 2);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "NULLABLE", DatabaseMetaData.columnNoNulls);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "IS_NULLABLE", "NO");

		// CHAR
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"CHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TYPE_NAME", "char");
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "IS_NULLABLE", "YES");

		// CHAR(20)
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"CHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TYPE_NAME", "char");
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "IS_NULLABLE", "YES");

		// DATETIME
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"DATETIME_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "DATA_TYPE", Types.TIMESTAMP);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TYPE_NAME", "datetime");
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "COLUMN_SIZE", 23);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "DECIMAL_DIGITS", 3);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "IS_NULLABLE", "YES");

		// DECIMAL
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"DECIMAL_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TYPE_NAME", "decimal");
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "COLUMN_SIZE", 18);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "IS_NULLABLE", "YES");

		// DECIMAL(10)
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"DECIMAL_10_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "TYPE_NAME", "decimal");
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_COL", "IS_NULLABLE", "YES");

		// DECIMAL(10,2)
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"DECIMAL_10_2_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "TYPE_NAME", "decimal");
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "DECIMAL_DIGITS", 2);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_10_2_COL", "IS_NULLABLE", "YES");

		// DOUBLE PRECISION
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"DOUBLE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TYPE_NAME", "double precis");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_SIZE", 15);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "IS_NULLABLE", "YES");

		// FLOAT
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"FLOAT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TYPE_NAME", "double precis");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "COLUMN_SIZE", 15);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "IS_NULLABLE", "YES");

		// FLOAT(10)
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"FLOAT_10_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "TYPE_NAME", "double precis");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "COLUMN_SIZE", 15);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_10_COL", "IS_NULLABLE", "YES");

		// IMAGE
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"IMAGE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "DATA_TYPE", Types.LONGVARBINARY);
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "TYPE_NAME", "image");
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "COLUMN_SIZE", 2147483647);
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "IMAGE_COL", "IS_NULLABLE", "YES");

		// INT
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"INT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "DATA_TYPE", Types.INTEGER);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "TYPE_NAME", "int");
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "IS_NULLABLE", "YES");

		// MONEY
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"MONEY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "TYPE_NAME", "money");
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "COLUMN_SIZE", 19);
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "DECIMAL_DIGITS", 4);
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "MONEY_COL", "IS_NULLABLE", "YES");

		// NCHAR
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"NCHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "TYPE_NAME", "char");
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "IS_NULLABLE", "YES");

		// NCHAR(20)
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"NCHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "TYPE_NAME", "char");
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_20_COL", "IS_NULLABLE", "YES");

		// NUMERIC
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"NUMERIC_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "DATA_TYPE", Types.NUMERIC);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TYPE_NAME", "numeric");
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "COLUMN_SIZE", 18);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "IS_NULLABLE", "YES");

		// NUMERIC(10)
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"NUMERIC_10_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "DATA_TYPE", Types.NUMERIC);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "TYPE_NAME", "numeric");
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_COL", "IS_NULLABLE", "YES");

		// NUMERIC(10,2)
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"NUMERIC_10_2_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "DATA_TYPE", Types.NUMERIC);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "TYPE_NAME", "numeric");
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "DECIMAL_DIGITS", 2);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_10_2_COL", "IS_NULLABLE", "YES");

		// NVARCHAR
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"NVARCHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "TYPE_NAME", "varchar");
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_COL", "IS_NULLABLE", "YES");

		// NVARCHAR(20)
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"NVARCHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "TYPE_NAME", "varchar");
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR_20_COL", "IS_NULLABLE", "YES");

		// REAL
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"REAL_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "DATA_TYPE", Types.REAL);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TYPE_NAME", "real");
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "COLUMN_SIZE", 7);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "IS_NULLABLE", "YES");

		// SMALLDATETIME
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"SMALLDATETIME_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "DATA_TYPE", Types.TIMESTAMP);
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "TYPE_NAME", "smalldatetime");
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "COLUMN_SIZE", 16);
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLDATETIME_COL", "IS_NULLABLE", "YES");

		// SMALLINT
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"SMALLINT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "DATA_TYPE", Types.SMALLINT);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TYPE_NAME", "smallint");
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "COLUMN_SIZE", 5);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "IS_NULLABLE", "YES");

		// SMALLMONEY
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"SMALLMONEY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "TYPE_NAME", "smallmoney");
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "DECIMAL_DIGITS", 4);
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLMONEY_COL", "IS_NULLABLE", "YES");

		// TEXT
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"TEXT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "DATA_TYPE", Types.LONGVARCHAR);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TYPE_NAME", "text");
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "COLUMN_SIZE", 2147483647);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "IS_NULLABLE", "YES");

		// TINYINT
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"TINYINT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "DATA_TYPE", Types.TINYINT);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "TYPE_NAME", "tinyint");
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "COLUMN_SIZE", 3);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "IS_NULLABLE", "YES");

		// VARBINARY
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"VARBINARY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "DATA_TYPE", Types.VARBINARY);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "TYPE_NAME", "varbinary");
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "IS_NULLABLE", "YES");

		// VARBINARY(20)
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"VARBINARY_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "DATA_TYPE", Types.VARBINARY);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TYPE_NAME", "varbinary");
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "IS_NULLABLE", "YES");

		// VARCHAR
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"VARCHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "TYPE_NAME", "varchar");
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "IS_NULLABLE", "YES");

		// VARCHAR(20)
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"VARCHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TYPE_NAME", "varchar");
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "IS_NULLABLE", "YES");

	}

	protected String platformName() {
		return "Sybase";
	}

}
