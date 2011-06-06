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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 */
public class Oracle8iTests extends OracleTests {

	public static Test suite() {
		return new TestSuite(Oracle8iTests.class);
	}

	public Oracle8iTests(String name) {
		super(name);
	}

	/**
	 * the Oracle 8.1.7.4.1 server in Ottawa
	 */
	protected String serverName() {
		return "tlsvrdb4.ca.oracle.com";
	}

	protected void appendColumnsToTableDDL(StringBuffer sb) {
		super.appendColumnsToTableDDL(sb);
	}

	protected String platformName() {
		return "Oracle8i";
	}

	protected String expectedVersionNumber() {
		return "8.1.7.4.1";
	}

}
