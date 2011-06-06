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
import java.util.Collection;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Oracle9i adds various timestamp datatypes and an XML datatype
 */
public class Oracle9iTests extends Oracle8iTests {

	public static Test suite() {
		return new TestSuite(Oracle9iTests.class);
	}

	public Oracle9iTests(String name) {
		super(name);
	}

	/**
	 * the Oracle 9.2.0.8.0 server in Ottawa
	 */
	protected String serverName() {
		return "tlsvrdb1.ca.oracle.com";
	}

	protected String platformName() {
		return "Oracle9i";
	}

	protected String expectedVersionNumber() {
		return "9.2.0.8.0";
	}

	protected void addIgnorableTypeNamesTo(Collection adtNames) {
		super.addIgnorableTypeNamesTo(adtNames);
		// ignore custom time stuff... 
		adtNames.add("TIMESTAMP(2)");
		adtNames.add("TIMESTAMP(2) WITH TIME ZONE");
		adtNames.add("TIMESTAMP(2) WITH LOCAL TIME ZONE");
		adtNames.add("INTERVAL YEAR(4) TO MONTH");
		adtNames.add("INTERVAL DAY(3) TO SECOND(2)");
	}

	protected void appendColumnsToTableDDL(StringBuffer sb) {
		sb.append("	TIMESTAMP_COL				TIMESTAMP,"); sb.append(CR);
		sb.append("	TIMESTAMP_2_COL			TIMESTAMP(2),"); sb.append(CR);
		sb.append("	TIMESTAMP_TZ_COL		TIMESTAMP WITH TIME ZONE,"); sb.append(CR);
		sb.append("	TIMESTAMP_TZ_2_COL		TIMESTAMP(2) WITH TIME ZONE,"); sb.append(CR);
		sb.append("	TIMESTAMP_LTZ_COL		TIMESTAMP WITH LOCAL TIME ZONE,"); sb.append(CR);
		sb.append("	TIMESTAMP_LTZ_2_COL	TIMESTAMP(2) WITH LOCAL TIME ZONE,"); sb.append(CR);
		sb.append("	INTERVAL_YEAR_COL		INTERVAL YEAR TO MONTH,"); sb.append(CR);
		sb.append("	INTERVAL_YEAR_4_COL	INTERVAL YEAR(4) TO MONTH,"); sb.append(CR);
		sb.append("	INTERVAL_DAY_COL			INTERVAL DAY TO SECOND,"); sb.append(CR);
		sb.append("	INTERVAL_DAY_3_COL		INTERVAL DAY(3) TO SECOND(2),"); sb.append(CR);
		sb.append("	XMLTYPE_COL					XMLTYPE,"); sb.append(CR);
		super.appendColumnsToTableDDL(sb);
	}

	protected int unicodeSizeFor(int size) {
		return 2 * size;
	}

	protected int varraySize() {
		return 2617;
	}

	protected void verifyTable(Map metaDataMap) {
		// TIMESTAMP
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"TIMESTAMP_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "DATA_TYPE", Types.TIMESTAMP);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "TYPE_NAME", "TIMESTAMP(6)");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "COLUMN_SIZE", 11);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "DECIMAL_DIGITS", 6);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_COL", "IS_NULLABLE", "YES");

		// TIMESTAMP(2)
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"TIMESTAMP_2_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "TYPE_NAME", "TIMESTAMP(2)");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "COLUMN_SIZE", 11);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "DECIMAL_DIGITS", 2);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_2_COL", "IS_NULLABLE", "YES");

		// TIMESTAMP WITH TIME ZONE
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"TIMESTAMP_TZ_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "DATA_TYPE", -101);		// ????
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "TYPE_NAME", "TIMESTAMP(6) WITH TIME ZONE");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "COLUMN_SIZE", 13);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "DECIMAL_DIGITS", 6);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_COL", "IS_NULLABLE", "YES");

		// TIMESTAMP(2) WITH TIME ZONE
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"TIMESTAMP_TZ_2_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "TYPE_NAME", "TIMESTAMP(2) WITH TIME ZONE");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "COLUMN_SIZE", 13);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "DECIMAL_DIGITS", 2);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_TZ_2_COL", "IS_NULLABLE", "YES");

		// TIMESTAMP WITH LOCAL TIME ZONE
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"TIMESTAMP_LTZ_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "DATA_TYPE", -102);		// ????
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "TYPE_NAME", "TIMESTAMP(6) WITH LOCAL TIME ZONE");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "COLUMN_SIZE", 11);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "DECIMAL_DIGITS", 6);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_COL", "IS_NULLABLE", "YES");

		// TIMESTAMP(2) WITH LOCAL TIME ZONE
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"TIMESTAMP_LTZ_2_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "TYPE_NAME", "TIMESTAMP(2) WITH LOCAL TIME ZONE");
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "COLUMN_SIZE", 11);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "DECIMAL_DIGITS", 2);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "TIMESTAMP_LTZ_2_COL", "IS_NULLABLE", "YES");

		// INTERVAL YEAR TO MONTH
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"INTERVAL_YEAR_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "DATA_TYPE", -103);		// ????
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "TYPE_NAME", "INTERVAL YEAR(2) TO MONTH");
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "COLUMN_SIZE", 2);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_COL", "IS_NULLABLE", "YES");

		// INTERVAL YEAR(4) TO MONTH
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"INTERVAL_YEAR_4_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "TYPE_NAME", "INTERVAL YEAR(4) TO MONTH");
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "COLUMN_SIZE", 4);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "DECIMAL_DIGITS", 0);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_YEAR_4_COL", "IS_NULLABLE", "YES");

		// INTERVAL DAY TO SECOND
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"INTERVAL_DAY_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "DATA_TYPE", -104);		// ????
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "TYPE_NAME", "INTERVAL DAY(2) TO SECOND(6)");
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "COLUMN_SIZE", 2);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "DECIMAL_DIGITS", 6);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_COL", "IS_NULLABLE", "YES");

		// INTERVAL DAY(3) TO SECOND(2)
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"INTERVAL_DAY_3_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "TYPE_NAME", "INTERVAL DAY(3) TO SECOND(2)");
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "COLUMN_SIZE", 3);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "DECIMAL_DIGITS", 2);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "INTERVAL_DAY_3_COL", "IS_NULLABLE", "YES");

		// XMLTYPE
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "TABLE_CAT", null);
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "TABLE_SCHEM", this.userName().toUpperCase());
		this.verifyColumnAttribute(metaDataMap,	"XMLTYPE_COL", "TABLE_NAME", this.tableName());
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "DATA_TYPE", Types.OTHER);
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "TYPE_NAME", "XMLTYPE");
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "COLUMN_SIZE", 2000);
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "DECIMAL_DIGITS", null);
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "NUM_PREC_RADIX", 10);
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "NULLABLE", DatabaseMetaData.columnNullable);
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "REMARKS", null);
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "COLUMN_DEF", null);
		this.verifyColumnAttribute(metaDataMap, "XMLTYPE_COL", "IS_NULLABLE", "YES");

		super.verifyTable(metaDataMap);
	}

}
