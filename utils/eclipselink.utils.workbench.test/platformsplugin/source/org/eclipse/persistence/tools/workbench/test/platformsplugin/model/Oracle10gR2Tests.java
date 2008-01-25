/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

/**
 * no new datatypes in R2
 */
public class Oracle10gR2Tests extends Oracle10gTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", Oracle10gR2Tests.class.getName()});
	}

	public static Test suite() {
		return new TestSuite(Oracle10gR2Tests.class);
	}

	public Oracle10gR2Tests(String name) {
		super(name);
	}

	/**
	 * the Oracle 10.2.0.3.0 server in Ottawa
	 */
	protected String serverName() {
		return "tlsvrdb3.ca.oracle.com";
	}

	protected String expectedVersionNumber() {
		return "10.2.0.3.0";
	}

}
