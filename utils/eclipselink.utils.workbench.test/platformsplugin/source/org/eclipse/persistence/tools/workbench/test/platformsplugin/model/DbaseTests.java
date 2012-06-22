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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DbaseTests extends PlatformTests {

	public static Test suite() {
		return new TestSuite(DbaseTests.class);
	}

	public DbaseTests(String name) {
		super(name);
	}

	protected Connection buildConnection() throws Exception {
		try {
			return super.buildConnection();
		} catch (SQLException ex) {
			throw new RuntimeException(
				"You must have an ODBC data source named \'" +
				this.odbcDataSourceName() +
				"\'; and it should be pointing to a dBASE database.",
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
		return "MW dBASE";
	}

	protected String platformName() {
		return "dBASE";
	}

	protected void verifyVersionNumber() throws Exception {
		// we're not too worried about which version of dBASE we're running against
	}

	protected void appendColumnsToTableDDL(StringBuffer sb) {
		sb.append("	BINARY_COL			BINARY,"); sb.append(CR);
		sb.append("	BINARY_20_COL		BINARY(20),"); sb.append(CR);
		sb.append("	CHAR_COL				CHAR,"); sb.append(CR);
		sb.append("	CHAR_20_COL			CHAR(20),"); sb.append(CR);
		sb.append("	DATE_COL				DATE,"); sb.append(CR);
		sb.append("	FLOAT_COL				FLOAT,"); sb.append(CR);
		sb.append("	LOGICAL_COL			LOGICAL,"); sb.append(CR);
		sb.append("	MEMO_COL				MEMO,"); sb.append(CR);
		sb.append("	NUMERIC_COL			NUMERIC"); sb.append(CR);
	}

	protected void verifyTable(Map metaDataMap) {
		// the dBASE driver does not return any meta-data
	}

}
