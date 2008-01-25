/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.uitools;

import org.eclipse.persistence.tools.workbench.test.uitools.app.AllUIToolsAppTests;
import org.eclipse.persistence.tools.workbench.test.uitools.swing.AllUIToolsSwingTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllUIToolsTests {
	
	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllUIToolsTests.class.getName()});
	}
	
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
		// screws with the focus when running the tests  ~bjv
		// suite.addTest(SwitcherPanelTests.suite());
	
		return suite;
	}
	
	private AllUIToolsTests() {
		super();
		throw new UnsupportedOperationException();
	}
	
}
