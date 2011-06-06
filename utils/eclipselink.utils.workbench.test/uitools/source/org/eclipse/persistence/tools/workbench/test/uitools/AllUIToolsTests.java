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
package org.eclipse.persistence.tools.workbench.test.uitools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.uitools.app.AllUIToolsAppTests;
import org.eclipse.persistence.tools.workbench.test.uitools.swing.AllUIToolsSwingTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllUIToolsTests {
	
	public static Test suite() {
		return suite(true);
	}
	
	public static Test suite(boolean all) {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllUIToolsTests.class));
	
		suite.addTest(AllUIToolsAppTests.suite());
		suite.addTest(AllUIToolsSwingTests.suite());
		
		suite.addTest(ComponentEnablerTest.suite());
		suite.addTest(DisplayableTests.suite());
		suite.addTest(PreferencesRecentFilesManagerTests.suite());
		suite.addTest(PropertyValueModelDisplayableAdapterTests.suite());
		suite.addTest(SimpleDisplayableTests.suite());

		// remove the following test suite because it opens a window and
		// modifies the focus when running the tests  ~bjv
		// suite.addTest(SwitcherPanelTests.suite());
	
		return suite;
	}
	
	private AllUIToolsTests() {
		super();
		throw new UnsupportedOperationException();
	}
	
}
