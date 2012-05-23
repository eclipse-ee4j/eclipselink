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

public class MySQL5Tests extends MySQL4Tests {

	public static Test suite() {
		return new TestSuite(MySQL5Tests.class);
	}

	public MySQL5Tests(String name) {
		super(name);
	}

	/**
	 * the MySQL 5.0 Server in Ottawa
	 */
	protected String serverName() {
		return "tlsvrdb4.ca.oracle.com";
	}

	protected String expectedVersionNumber() {
		return "5.0.15";
	}

	protected String tableName() {
		return super.tableName().toLowerCase();	// ???
	}

	protected void verifyTable(Map metaDataMap) {
		// BIT
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "DATA_TYPE", Types.OTHER);		// different from MySQL4
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "TYPE_NAME", "bit");		// different from MySQL4
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BIT_COL", "IS_NULLABLE", "YES");

		// BOOL
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "DATA_TYPE", Types.TINYINT);
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "TYPE_NAME", "tinyint");
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BOOL_COL", "IS_NULLABLE", "YES");

		// BOOLEAN
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "DATA_TYPE", Types.TINYINT);
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "TYPE_NAME", "tinyint");
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BOOLEAN_COL", "IS_NULLABLE", "YES");

		// TINYINT
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "DATA_TYPE", Types.TINYINT);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "TYPE_NAME", "tinyint");
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "COLUMN_SIZE", 4);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TINYINT_COL", "IS_NULLABLE", "YES");

		// BIGINT
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "DATA_TYPE", Types.BIGINT);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "TYPE_NAME", "bigint");
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BIGINT_COL", "IS_NULLABLE", "YES");

		// LONG VARBINARY
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "DATA_TYPE", Types.LONGVARBINARY);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "TYPE_NAME", "mediumblob");
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "COLUMN_SIZE", 16277215);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARBINARY_COL", "IS_NULLABLE", "YES");

		// MEDIUMBLOB
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "DATA_TYPE", Types.LONGVARBINARY);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "TYPE_NAME", "mediumblob");
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "COLUMN_SIZE", 16277215);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMBLOB_COL", "IS_NULLABLE", "YES");

		// LONGBLOB
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "DATA_TYPE", Types.LONGVARBINARY);
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "TYPE_NAME", "longblob");
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "COLUMN_SIZE", 2147483647);
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "LONGBLOB_COL", "IS_NULLABLE", "YES");

		// BLOB
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "DATA_TYPE", Types.LONGVARBINARY);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TYPE_NAME", "blob");
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "COLUMN_SIZE", 65535);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "IS_NULLABLE", "YES");

		// TINYBLOB
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "DATA_TYPE", Types.BINARY);
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "TYPE_NAME", "tinyblob");
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "COLUMN_SIZE", 255);
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TINYBLOB_COL", "IS_NULLABLE", "YES");

		// VARBINARY(20)
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "TYPE_NAME", "varbinary");
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARBINARY_20_COL", "IS_NULLABLE", "YES");

		// BINARY
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "TYPE_NAME", "binary");
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_COL", "IS_NULLABLE", "YES");

		// BINARY(20)
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "TYPE_NAME", "binary"); // different from MySQL4
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_20_COL", "IS_NULLABLE", "YES");

		// LONG VARCHAR
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "DATA_TYPE", Types.LONGVARCHAR);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "TYPE_NAME", "mediumtext");
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "COLUMN_SIZE", 16277215);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_VARCHAR_COL", "IS_NULLABLE", "YES");

		// MEDIUMTEXT
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "DATA_TYPE", Types.LONGVARCHAR);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "TYPE_NAME", "mediumtext");
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "COLUMN_SIZE", 16277215);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMTEXT_COL", "IS_NULLABLE", "YES");

		// LONGTEXT
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "DATA_TYPE", Types.LONGVARCHAR);
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "TYPE_NAME", "longtext");
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "COLUMN_SIZE", 2147483647);
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "LONGTEXT_COL", "IS_NULLABLE", "YES");

		// TEXT
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "DATA_TYPE", Types.LONGVARCHAR);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "TYPE_NAME", "text");
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "COLUMN_SIZE", 65535);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TEXT_COL", "IS_NULLABLE", "YES");

		// TINYTEXT
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "TYPE_NAME", "tinytext");
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "COLUMN_SIZE", 255);
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TINYTEXT_COL", "IS_NULLABLE", "YES");

		// CHAR
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TYPE_NAME", "char");
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "IS_NULLABLE", "YES");

		// CHAR(20)
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "DATA_TYPE", Types.CHAR);	// different from MySQL4
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "TYPE_NAME", "char");	// different from MySQL4
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_20_COL", "IS_NULLABLE", "YES");

		// NUMERIC
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "TYPE_NAME", "decimal");
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMERIC_COL", "IS_NULLABLE", "YES");

		// DECIMAL
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "TYPE_NAME", "decimal");
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DECIMAL_COL", "IS_NULLABLE", "YES");

		// DEC
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "TYPE_NAME", "decimal");
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DEC_COL", "IS_NULLABLE", "YES");

		// FIXED
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "TYPE_NAME", "decimal");
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FIXED_COL", "IS_NULLABLE", "YES");

		// INTEGER
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "DATA_TYPE", Types.INTEGER);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "TYPE_NAME", "int");
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "COLUMN_SIZE", 11);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "INTEGER_COL", "IS_NULLABLE", "YES");

		// INT
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "DATA_TYPE", Types.INTEGER);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "TYPE_NAME", "int");
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "COLUMN_SIZE", 11);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "INT_COL", "IS_NULLABLE", "YES");

		// MEDIUMINT
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "DATA_TYPE", Types.INTEGER);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "TYPE_NAME", "mediumint");
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "COLUMN_SIZE", 9);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "MEDIUMINT_COL", "IS_NULLABLE", "YES");

		// SMALLINT
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "DATA_TYPE", Types.SMALLINT);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "TYPE_NAME", "smallint");
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "COLUMN_SIZE", 6);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "SMALLINT_COL", "IS_NULLABLE", "YES");

		// FLOAT
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DATA_TYPE", Types.REAL);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TYPE_NAME", "float");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "COLUMN_SIZE", 12);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "IS_NULLABLE", "YES");

		// DOUBLE
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "TYPE_NAME", "double");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_SIZE", 22);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_COL", "IS_NULLABLE", "YES");

		// DOUBLE PRECISION
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "TYPE_NAME", "double");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "COLUMN_SIZE", 22);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DOUBLE_PRECISION_COL", "IS_NULLABLE", "YES");

		// REAL
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "DATA_TYPE", Types.DOUBLE);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TYPE_NAME", "double");
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "COLUMN_SIZE", 22);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "IS_NULLABLE", "YES");

		// VARCHAR(20)
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "TYPE_NAME", "varchar");
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR_20_COL", "IS_NULLABLE", "YES");

		// ENUM
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "TYPE_NAME", "enum");
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "COLUMN_SIZE", 2);
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "ENUM_COL", "IS_NULLABLE", "YES");

		// SET
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "TYPE_NAME", "set");
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "COLUMN_SIZE", 3);
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "SET_COL", "IS_NULLABLE", "YES");

		// DATE
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "DATA_TYPE", Types.DATE);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TYPE_NAME", "date");
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "IS_NULLABLE", "YES");

		// TIME
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "DATA_TYPE", Types.TIME);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TYPE_NAME", "time");
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "COLUMN_SIZE", 8);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "IS_NULLABLE", "YES");

		// DATETIME
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "DATA_TYPE", Types.TIMESTAMP);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "TYPE_NAME", "datetime");
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "COLUMN_SIZE", 19);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DATETIME_COL", "IS_NULLABLE", "YES");

		// TIMESTAMP
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "DATA_TYPE", Types.TIMESTAMP);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TYPE_NAME", "timestamp");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "COLUMN_SIZE", 19);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "COLUMN_DEF", "CURRENT_TIMESTAMP");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "IS_NULLABLE", "YES");

		// YEAR
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "TABLE_CAT", "");
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "TABLE_SCHEM", null);
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "DATA_TYPE", Types.DATE);
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "TYPE_NAME", "year");
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "COLUMN_SIZE", 4);
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "REMARKS", "");
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "YEAR_COL", "IS_NULLABLE", "YES");
	}

}
