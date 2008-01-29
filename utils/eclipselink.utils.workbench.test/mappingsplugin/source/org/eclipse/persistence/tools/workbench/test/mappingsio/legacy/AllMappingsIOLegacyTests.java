/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsio.legacy;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllMappingsIOLegacyTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllMappingsIOLegacyTests.class.getName()});
	}

	public static Test suite() {
		return suite(true);
	}

	public static Test suite(boolean all) {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllMappingsIOLegacyTests.class));

        suite.addTest(BackwardCompatibilityTests60.suite());	
		return suite;
	}

	private AllMappingsIOLegacyTests() {
		super();
	}

}
