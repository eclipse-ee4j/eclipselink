/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsio;

import org.eclipse.persistence.tools.workbench.test.mappingsio.legacy.AllMappingsIOLegacyTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllMappingsIOTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllMappingsIOTests.class.getName()});
	}

	public static Test suite() {
		return suite(true);
	}

	public static Test suite(boolean all) {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllMappingsIOTests.class));

		suite.addTest(AllMappingsIOLegacyTests.suite(all));
	
		suite.addTest(ReadWriteTests.suite());
		
		return suite;
	}

	private AllMappingsIOTests() {
		super();
		throw new UnsupportedOperationException();
	}

}
