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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MSAccessTests extends PlatformTests {

	public static Test suite() {
		return new TestSuite(MSAccessTests.class);
	}

	public MSAccessTests(String name) {
		super(name);
	}

	protected Connection buildConnection() throws Exception {
		try {
			return super.buildConnection();
		} catch (SQLException ex) {
			throw new RuntimeException(
				"You must have an ODBC data source named \'" +
				this.odbcDataSourceName() +
				"\'; and it should be pointing to an Access database.",
				ex);
		}
	}

	protected String driverClassName() {
		return "sun.jdbc.odbc.JdbcOdbcDriver";
	}

	protected String connectionURL() {
		return "jdbc:odbc:" + this.odbcDataSourceName();
	}

	protected String odbcDataSourceName() {
		return "MW";
	}

	protected String platformName() {
		return "Microsoft Access";
	}

	protected void verifyVersionNumber() throws Exception {
		// we're not too worried about which version of Access we're running against
	}

	/**
	 * Access barfs if a schema pattern is passed to
	 * DatabaseMetaData#getColumns()
	 */
	protected String schemaPattern() {
		return null;
	}

	protected void appendColumnsToTableDDL(StringBuffer sb) {
		// types from MS Access Help
		sb.append("	TEXT_COL				TEXT,"); sb.append(CR);
		sb.append("	TEXT_20_COL			TEXT(20),"); sb.append(CR);
		sb.append("	MEMO_COL				MEMO,"); sb.append(CR);
		sb.append("	NUMBER_COL			NUMBER,"); sb.append(CR);
		sb.append("	DATETIME_COL		DATETIME,"); sb.append(CR);
		sb.append("	CURRENCY_COL		CURRENCY,"); sb.append(CR);
//		sb.append("	AUTONUMBER_COL	AUTONUMBER,"); sb.append(CR);	// COUNTER?
		sb.append("	YESNO_COL			YESNO,"); sb.append(CR);
		sb.append("	OLEOBJECT_COL		OLEOBJECT,"); sb.append(CR);
//		sb.append("	HYPERLINK_COL		HYPERLINK,"); sb.append(CR);	// doesn't seem to work?

		// types from JDBC dump
		sb.append("	BIT_COL				BIT,"); sb.append(CR);
		sb.append("	BYTE_COL				BYTE,"); sb.append(CR);
		sb.append("	LONGBINARY_COL	LONGBINARY,"); sb.append(CR);
		sb.append("	VARBINARY_COL		VARBINARY,"); sb.append(CR);
		sb.append("	VARBINARY_20_COL	VARBINARY(20),"); sb.append(CR);
		sb.append("	BINARY_COL			BINARY,"); sb.append(CR);
		sb.append("	BINARY_20_COL		BINARY(20),"); sb.append(CR);
		sb.append("	LONGCHAR_COL		LONGCHAR,"); sb.append(CR);
		sb.append("	CHAR_COL				CHAR,"); sb.append(CR);
		sb.append("	CHAR_20_COL			CHAR(20),"); sb.append(CR);
		sb.append("	INTEGER_COL			INTEGER,"); sb.append(CR);
		sb.append("	COUNTER_COL		COUNTER,"); sb.append(CR);
		sb.append("	SMALLINT_COL		SMALLINT,"); sb.append(CR);
		sb.append("	REAL_COL				REAL,"); sb.append(CR);
		sb.append("	DOUBLE_COL			DOUBLE,"); sb.append(CR);
		sb.append("	VARCHAR_COL			VARCHAR,"); sb.append(CR);
		sb.append("	VARCHAR_20_COL		VARCHAR(20)"); sb.append(CR);
	}

	protected void verifyTable(Map metaDataMap) {
		// TEXT
//		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"TEXT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TYPE_NAME", "VARCHAR");
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "COLUMN_SIZE", 255);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "IS_NULLABLE", "YES");

		// TEXT(20)
//		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"TEXT_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "TYPE_NAME", "VARCHAR");
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_20_COL", "IS_NULLABLE", "YES");

		// MEMO
//		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"MEMO_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "DATA_TYPE", Types.LONGVARCHAR);
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "TYPE_NAME", "LONGCHAR");
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "COLUMN_SIZE", 1073741823);
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "MEMO_COL", "IS_NULLABLE", "YES");

		// NUMBER
//		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"NUMBER_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "TYPE_NAME", "DOUBLE");
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "COLUMN_SIZE", 15);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "IS_NULLABLE", "YES");

		// DATETIME
//		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"DATETIME_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "DATA_TYPE", Types.TIMESTAMP);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TYPE_NAME", "DATETIME");
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "COLUMN_SIZE", 19);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "IS_NULLABLE", "YES");

		// CURRENCY
//		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"CURRENCY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "DATA_TYPE", Types.NUMERIC);
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "TYPE_NAME", "CURRENCY");
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "COLUMN_SIZE", 19);
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "DECIMAL_DIGITS", 4);
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CURRENCY_COL", "IS_NULLABLE", "YES");

		// YESNO
//		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"YESNO_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "DATA_TYPE", Types.BIT);
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "TYPE_NAME", "BIT");
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "NULLABLE", DatabaseMetaData.columnNoNulls);
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "YESNO_COL", "IS_NULLABLE", "NO");

		// OLEOBJECT
//		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"OLEOBJECT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "DATA_TYPE", Types.LONGVARBINARY);
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "TYPE_NAME", "LONGBINARY");
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "COLUMN_SIZE", 1073741823);
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "OLEOBJECT_COL", "IS_NULLABLE", "YES");

		// BIT
//		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"BIT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "DATA_TYPE", Types.BIT);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TYPE_NAME", "BIT");
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "NULLABLE", DatabaseMetaData.columnNoNulls);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "IS_NULLABLE", "NO");

		// BYTE
//		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"BYTE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "DATA_TYPE", Types.TINYINT);
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "TYPE_NAME", "BYTE");
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "COLUMN_SIZE", 3);
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BYTE_COL", "IS_NULLABLE", "YES");

		// LONGBINARY
//		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"LONGBINARY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "DATA_TYPE", Types.LONGVARBINARY);
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "TYPE_NAME", "LONGBINARY");
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "COLUMN_SIZE", 1073741823);
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "LONGBINARY_COL", "IS_NULLABLE", "YES");

		// VARBINARY
//		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"VARBINARY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "DATA_TYPE", Types.VARBINARY);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "TYPE_NAME", "VARBINARY");
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "COLUMN_SIZE", 510);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_COL", "IS_NULLABLE", "YES");

		// VARBINARY(20)
//		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"VARBINARY_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "DATA_TYPE", Types.VARBINARY);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TYPE_NAME", "VARBINARY");
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "IS_NULLABLE", "YES");

		// BINARY
//		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"BINARY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "DATA_TYPE", Types.BINARY);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TYPE_NAME", "BINARY");
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "COLUMN_SIZE", 510);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "IS_NULLABLE", "YES");

		// BINARY(20)
//		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"BINARY_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "DATA_TYPE", Types.BINARY);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "TYPE_NAME", "BINARY");
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "IS_NULLABLE", "YES");

		// LONGCHAR
//		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"LONGCHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "DATA_TYPE", Types.LONGVARCHAR);
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "TYPE_NAME", "LONGCHAR");
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "COLUMN_SIZE", 1073741823);
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "LONGCHAR_COL", "IS_NULLABLE", "YES");

		// CHAR
//		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"CHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TYPE_NAME", "CHAR");
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_SIZE", 255);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "IS_NULLABLE", "YES");

		// CHAR(20)
//		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TABLE_SCHEM", null);
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

		// INTEGER
//		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TABLE_SCHEM", null);
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

		// COUNTER
//		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"COUNTER_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "DATA_TYPE", Types.INTEGER);
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "TYPE_NAME", "COUNTER");
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "NULLABLE", DatabaseMetaData.columnNoNulls);
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "COUNTER_COL", "IS_NULLABLE", "NO");

		// SMALLINT
//		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TABLE_SCHEM", null);
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

		// REAL
//		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_SCHEM", null);
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

		// DOUBLE
//		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"DOUBLE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TYPE_NAME", "DOUBLE");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_SIZE", 15);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "IS_NULLABLE", "YES");

		// VARCHAR
//		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap,	"VARCHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "TYPE_NAME", "VARCHAR");
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "COLUMN_SIZE", 255);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_COL", "IS_NULLABLE", "YES");

		// VARCHAR(20)
//		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TABLE_CAT", null);		// the catalog is the local file name
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TABLE_SCHEM", null);
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
