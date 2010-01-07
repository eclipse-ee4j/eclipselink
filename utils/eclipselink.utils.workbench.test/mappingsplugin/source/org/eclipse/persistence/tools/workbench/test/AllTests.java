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
package org.eclipse.persistence.tools.workbench.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * Pull together the tests from all the libraries and plug-ins.
 */
public class AllTests {

	public static Test suite() {
		return suite(true);
	}

	/**
	 * for now, there is only one difference between "All" and "Most" tests...
	 */
	static Test suite(boolean all) {
		TestTools.setUpJUnitThreadContextClassLoader();
		TestTools.invalidateSystemStreams();
		TestTools.setUpOracleProxy();

		String quantity = all ? "All" : "Most";
		TestSuite suite = new TestSuite(quantity + " EclipseLink Workbench Tests");

		// resolve all the classes dynamically, so we don't have compile dependencies
		suite.addTest(suiteForClassNamed("org.eclipse.persistence.tools.workbench.test.utility.AllUtilityTests", all));
		suite.addTest(suiteForClassNamed("org.eclipse.persistence.tools.workbench.test.uitools.AllUIToolsTests", all));
		suite.addTest(suiteForClassNamed("org.eclipse.persistence.tools.workbench.test.framework.AllFrameworkTests", all));
		suite.addTest(suiteForClassNamed("org.eclipse.persistence.tools.workbench.test.platformsplugin.AllPlatformsPluginTests", all));
		suite.addTest(suiteForClassNamed("org.eclipse.persistence.tools.workbench.test.mappingsmodel.AllMappingsModelTests", all));
		suite.addTest(suiteForClassNamed("org.eclipse.persistence.tools.workbench.test.mappingsplugin.AllMappingsPluginTests", all));
		suite.addTest(suiteForClassNamed("org.eclipse.persistence.tools.workbench.test.mappingsio.AllMappingsIOTests", all));
		suite.addTest(suiteForClassNamed("org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests", all));

		return suite;
	}

	private static Test suiteForClassNamed(String javaClassName, boolean all) {
		return (Test) ClassTools.invokeStaticMethod(ClassTools.classForName(javaClassName), "suite", boolean.class, Boolean.valueOf(all));
	}

	private AllTests() {
		super();
		throw new UnsupportedOperationException();
	}
}
