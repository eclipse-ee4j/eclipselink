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
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.Map;

/**
 * For now, this class is actually targets Oracle8i.
 */
public abstract class OracleTests extends PlatformTests {

	public OracleTests(String name) {
		super(name);
	}

	/**
	 * extend to drop array and user-defined type
	 */
	protected void dropDatabaseObjects() throws Exception {
		super.dropDatabaseObjects();
		Statement stmt;

		stmt = this.connection.createStatement();
		try {
			stmt.executeUpdate(this.buildDropArrayDDL());
		} catch (SQLException ex) {/* ignore any problems dropping the table */}
		stmt.close();

		stmt = this.connection.createStatement();
		try {
			stmt.executeUpdate(this.buildDropTypeDDL());
		} catch (SQLException ex) {/* ignore any problems dropping the type */}
		stmt.close();
	}

	/**
	 * extend to create user-defined type and array
	 */
	protected void createDatabaseObjects() throws Exception {
		Statement stmt;

		stmt = this.connection.createStatement();
		stmt.executeUpdate(this.buildCreateTypeDDL());
		stmt.close();

		stmt = this.connection.createStatement();
		stmt.executeUpdate(this.buildCreateArrayDDL());
		stmt.close();

		super.createDatabaseObjects();
	}

	protected String[] driverJARNames() {
		return new String[] {"OracleThinJDBC_jdk14_10.2.0.1.0.jar"};
	}

	protected String driverClassName() {
		return "oracle.jdbc.OracleDriver";
	}

	protected String connectionURL() {
		return "jdbc:oracle:thin:@" + this.serverName() + ":1521:" + this.instanceName();
	}

	protected abstract String serverName();

	protected String instanceName() {
		return "TOPLINK";
	}

	/**
	 * more recent Oracle platforms return twice what 8i did...
	 */
	protected int unicodeSizeFor(int size) {
		return size;
	}

	/**
	 * this varies across versions...
	 */
	protected int varraySize() {
		return 2615;
	}

	protected void addIgnorableTypeNamesTo(Collection adtNames) {
		super.addIgnorableTypeNamesTo(adtNames);
		adtNames.add(this.typeName());
		adtNames.add(this.arrayName());
	}

	protected String buildDropTypeDDL() {
		return "DROP TYPE " + this.typeName();
	}

	protected String buildCreateTypeDDL() {
		return "CREATE TYPE " + this.typeName() + " AS OBJECT (" +
			this.typeName() + "_NUMBER_10_0_COL			NUMBER(10)," +
			this.typeName() + "_VARCHAR2_20_COL			VARCHAR2(20)" +
		")";
	}

	protected String typeName() {
		return this.baseName() + "_TYPE";
	}

	protected String buildDropArrayDDL() {
		return "DROP TYPE " + this.arrayName();
	}

	protected String buildCreateArrayDDL() {
		return "CREATE TYPE " + this.arrayName() + " AS VARRAY(50) OF " + this.typeName();
	}

	protected String arrayName() {
		return this.typeName() + "_ARRAY";
	}

	protected void appendColumnsToTableDDL(StringBuffer sb) {
		sb.append("	VARCHAR2_20_COL			VARCHAR2(20),"); sb.append(CR);	// size is required
		sb.append("	NVARCHAR2_20_COL		NVARCHAR2(20),"); sb.append(CR);	// size is required
		sb.append("	NUMBER_COL					NUMBER,"); sb.append(CR);
		sb.append("	NUMBER_3_0_COL			NUMBER(3),"); sb.append(CR);
		sb.append("	NUMBER_10_0_COL			NUMBER(10),"); sb.append(CR);
		sb.append("	NUMBER_20_2_COL			NUMBER(20, 2),"); sb.append(CR);
		sb.append("	LONG_COL						LONG,"); sb.append(CR);
		sb.append("	DATE_COL						DATE,"); sb.append(CR);
		sb.append("	FLOAT_COL					FLOAT,"); sb.append(CR);				// ANSI datatype?
		sb.append("	FLOAT_100_COL				FLOAT(100),"); sb.append(CR);				// ANSI datatype?
		sb.append("	REAL_COL						REAL,"); sb.append(CR);					// ANSI datatype?
		sb.append("	RAW_1000_COL				RAW(1000),"); sb.append(CR);		// size is required
//		sb.append("	LONG_RAW_COL				LONG RAW,"); sb.append(CR);		// only one LONG allowed per table (LONG or LONG RAW)
		sb.append("	ROWID_COL					ROWID,"); sb.append(CR);
		sb.append("	UROWID_COL					UROWID,"); sb.append(CR);
		sb.append("	UROWID_2000_COL			UROWID(2000),"); sb.append(CR);
		sb.append("	CHAR_COL						CHAR,"); sb.append(CR);
		sb.append("	CHAR_1000_COL				CHAR(1000),"); sb.append(CR);
		sb.append("	NCHAR_COL					NCHAR,"); sb.append(CR);
		sb.append("	NCHAR_1000_COL			NCHAR(1000),"); sb.append(CR);
		sb.append("	CLOB_COL						CLOB,"); sb.append(CR);
		sb.append("	NCLOB_COL					NCLOB,"); sb.append(CR);
		sb.append("	BLOB_COL						BLOB,"); sb.append(CR);
		sb.append("	BFILE_COL						BFILE,"); sb.append(CR);
		sb.append("	" + this.typeName() + "_COL " + this.typeName() + ","); sb.append(CR);		// STRUCT?
		sb.append("	" + this.arrayName() + "_COL " + this.arrayName()); sb.append(CR);			// ARRAY?
	}

	protected void verifyTable(Map metaDataMap) {
		// VARCHAR2(20)
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"VARCHAR2_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "DATA_TYPE", Types.VARCHAR);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "TYPE_NAME", "VARCHAR2");
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "VARCHAR2_20_COL", "IS_NULLABLE", "YES");

		// NVARCHAR2(20)
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NVARCHAR2_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "TYPE_NAME", "NVARCHAR2");
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "COLUMN_SIZE", this.unicodeSizeFor(20));
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NVARCHAR2_20_COL", "IS_NULLABLE", "YES");

		// NUMBER
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NUMBER_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "TYPE_NAME", "NUMBER");
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "COLUMN_SIZE", 22);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_COL", "IS_NULLABLE", "YES");

		// NUMBER(3)
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NUMBER_3_0_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "TYPE_NAME", "NUMBER");
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "COLUMN_SIZE", 3);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_3_0_COL", "IS_NULLABLE", "YES");

		// NUMBER(10)
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NUMBER_10_0_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "TYPE_NAME", "NUMBER");
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_10_0_COL", "IS_NULLABLE", "YES");

		// NUMBER(20, 2)
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NUMBER_20_2_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "DATA_TYPE", Types.DECIMAL);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "TYPE_NAME", "NUMBER");
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "DECIMAL_DIGITS", 2);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NUMBER_20_2_COL", "IS_NULLABLE", "YES");

		// LONG
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"LONG_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "DATA_TYPE", Types.LONGVARCHAR);
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "TYPE_NAME", "LONG");
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "COLUMN_SIZE", 0);
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "LONG_COL", "IS_NULLABLE", "YES");

		// DATE
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"DATE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "DATA_TYPE", Types.DATE);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TYPE_NAME", "DATE");
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "COLUMN_SIZE", 7);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "IS_NULLABLE", "YES");

		// FLOAT
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"FLOAT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DATA_TYPE", Types.FLOAT);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "TYPE_NAME", "FLOAT");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "COLUMN_SIZE", 126);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_COL", "IS_NULLABLE", "YES");

		// FLOAT(100)
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"FLOAT_100_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "DATA_TYPE", Types.FLOAT);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "TYPE_NAME", "FLOAT");
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "COLUMN_SIZE", 100);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FLOAT_100_COL", "IS_NULLABLE", "YES");

		// REAL
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"REAL_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "DATA_TYPE", Types.FLOAT);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "TYPE_NAME", "FLOAT");
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "COLUMN_SIZE", 63);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "REAL_COL", "IS_NULLABLE", "YES");

		// RAW(1000)
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"RAW_1000_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "DATA_TYPE", Types.VARBINARY);
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "TYPE_NAME", "RAW");
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "COLUMN_SIZE", 1000);
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "RAW_1000_COL", "IS_NULLABLE", "YES");

		// ROWID
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"ROWID_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "TYPE_NAME", "ROWID");
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "ROWID_COL", "IS_NULLABLE", "YES");

		// UROWID
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"UROWID_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "TYPE_NAME", "UROWID");
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "COLUMN_SIZE", 4000);
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "UROWID_COL", "IS_NULLABLE", "YES");

		// UROWID(2000)
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"UROWID_2000_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "TYPE_NAME", "UROWID");
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "COLUMN_SIZE", 2000);
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "UROWID_2000_COL", "IS_NULLABLE", "YES");

		// CHAR
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"CHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "TYPE_NAME", "CHAR");
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_COL", "IS_NULLABLE", "YES");

		// CHAR(1000)
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"CHAR_1000_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "TYPE_NAME", "CHAR");
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "COLUMN_SIZE", 1000);
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CHAR_1000_COL", "IS_NULLABLE", "YES");

		// NCHAR
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NCHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "TYPE_NAME", "NCHAR");
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "COLUMN_SIZE", this.unicodeSizeFor(1));
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_COL", "IS_NULLABLE", "YES");

		// NCHAR(1000)
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NCHAR_1000_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "TYPE_NAME", "NCHAR");
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "COLUMN_SIZE", this.unicodeSizeFor(1000));
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NCHAR_1000_COL", "IS_NULLABLE", "YES");

		// CLOB
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"CLOB_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "DATA_TYPE", Types.CLOB);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "TYPE_NAME", "CLOB");
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "COLUMN_SIZE", 4000);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "CLOB_COL", "IS_NULLABLE", "YES");

		// NCLOB
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"NCLOB_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "TYPE_NAME", "NCLOB");
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "COLUMN_SIZE", 4000);
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "NCLOB_COL", "IS_NULLABLE", "YES");

		// BLOB
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"BLOB_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "DATA_TYPE", Types.BLOB);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "TYPE_NAME", "BLOB");
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "COLUMN_SIZE", 4000);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BLOB_COL", "IS_NULLABLE", "YES");

		// BFILE
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"BFILE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "DATA_TYPE", -13);	// ???
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "TYPE_NAME", "BFILE");
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "COLUMN_SIZE", 530);
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BFILE_COL", "IS_NULLABLE", "YES");

		// FOO_TYPE (user-defined datatype)
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"FOO_TYPE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "TYPE_NAME", "FOO_TYPE");
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_COL", "IS_NULLABLE", "YES");

		// FOO_TYPE_ARRAY (user-defined datatype)
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"FOO_TYPE_ARRAY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "TYPE_NAME", "FOO_TYPE_ARRAY");
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "COLUMN_SIZE", this.varraySize());
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "FOO_TYPE_ARRAY_COL", "IS_NULLABLE", "YES");
	}

}
