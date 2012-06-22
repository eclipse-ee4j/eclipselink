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

public class Sybase12_5Tests extends Sybase12_0Tests {

	public static Test suite() {
		return new TestSuite(Sybase12_5Tests.class);
	}

	public Sybase12_5Tests(String name) {
		super(name);
	}

	protected String[] driverJARNames() {
		return new String[] {"jconn3.jar"};
	}

	protected String driverClassName() {
		return "com.sybase.jdbc3.jdbc.SybDriver";
	}

	/**
	 * the Sybase Adaptive Server Enterprise 12.5 in Ottawa
	 */
	protected String serverName() {
		return "tlsvrdb2.ca.oracle.com";
	}

	protected String expectedVersionNumber() {
		return "12.5.2";
	}

	protected void appendColumnsToTableDDL(StringBuffer sb) {
		sb.append("DATE_COL					DATE NULL,"); sb.append(CR);
		sb.append("TIME_COL					TIME NULL,"); sb.append(CR);
		sb.append("UNICHAR_COL				UNICHAR NULL,"); sb.append(CR);
		sb.append("UNICHAR_20_COL			UNICHAR(20) NULL,"); sb.append(CR);
		sb.append("UNIVARCHAR_COL			UNIVARCHAR NULL,"); sb.append(CR);
		sb.append("UNIVARCHAR_20_COL	UNIVARCHAR(20) NULL,"); sb.append(CR);
		super.appendColumnsToTableDDL(sb);
	}

	protected void verifyTable(Map metaDataMap) {
		// DATE
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"DATE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "DATA_TYPE", Types.DATE);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "TYPE_NAME", "date");
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "COLUMN_SIZE", 10);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "DATE_COL", "IS_NULLABLE", "YES");

		// TIME
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"TIME_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "DATA_TYPE", Types.TIME);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "TYPE_NAME", "time");
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "COLUMN_SIZE", 12);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "DECIMAL_DIGITS", 3);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIME_COL", "IS_NULLABLE", "YES");

		// UNICHAR
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"UNICHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "TYPE_NAME", "unichar");
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_COL", "IS_NULLABLE", "YES");

		// UNICHAR(20)
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"UNICHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "DATA_TYPE", Types.CHAR);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "TYPE_NAME", "unichar");
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "UNICHAR_20_COL", "IS_NULLABLE", "YES");

		// UNIVARCHAR
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"UNIVARCHAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "DATA_TYPE", 35);		// ???
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "TYPE_NAME", "univarchar");
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "COLUMN_SIZE", 1);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_COL", "IS_NULLABLE", "YES");

		// UNIVARCHAR(20)
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "TABLE_CAT", this.userName());
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "TABLE_SCHEM", this.userName());
		this.verifyColumnAttribute(metaDataMap,	"UNIVARCHAR_20_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "DATA_TYPE", 35);		// ???
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "TYPE_NAME", "univarchar");
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "COLUMN_SIZE", 20);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "NUM_PREC_RADIX", null);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "UNIVARCHAR_20_COL", "IS_NULLABLE", "YES");

		super.verifyTable(metaDataMap);
	}

}
