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
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Oracle10g adds a couple of "binary" number datatypes
 */
public class Oracle10gTests extends Oracle9iTests {

	public static Test suite() {
		return new TestSuite(Oracle10gTests.class);
	}

	public Oracle10gTests(String name) {
		super(name);
	}

	/**
	 * the Oracle 10.1.0.5.0 server in Ottawa
	 */
	protected String serverName() {
		return "tlsvrdb5.ca.oracle.com";
	}

	protected String platformName() {
		return "Oracle10g";
	}

	protected String expectedVersionNumber() {
		return "10.1.0.5.0";
	}

	protected void appendColumnsToTableDDL(StringBuffer sb) {
		sb.append("	BINARY_FLOAT_COL			BINARY_FLOAT,"); sb.append(CR);
		sb.append("	BINARY_DOUBLE_COL		BINARY_DOUBLE,"); sb.append(CR);
		super.appendColumnsToTableDDL(sb);
	}

	protected int varraySize() {
		return 2817;
	}

	protected void verifyTable(Map metaDataMap) {
		// BINARY_FLOAT
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"BINARY_FLOAT_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "DATA_TYPE", 100);	// ????
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "TYPE_NAME", "BINARY_FLOAT");
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "COLUMN_SIZE", 4);
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_FLOAT_COL", "IS_NULLABLE", "YES");

		// BINARY_DOUBLE
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"BINARY_DOUBLE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "DATA_TYPE", 101);		// ????
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "TYPE_NAME", "BINARY_DOUBLE");
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "COLUMN_SIZE", 8);
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "BINARY_DOUBLE_COL", "IS_NULLABLE", "YES");

		super.verifyTable(metaDataMap);
	}

}
