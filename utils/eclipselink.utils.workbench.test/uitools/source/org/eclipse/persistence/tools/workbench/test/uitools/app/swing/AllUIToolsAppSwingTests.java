/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * 
 */
public class AllUIToolsAppSwingTests {
	
	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllUIToolsAppSwingTests.class.getName()});
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllUIToolsAppSwingTests.class));

		suite.addTest(CheckBoxModelAdapterTests.suite());
		suite.addTest(ComboBoxModelAdapterTests.suite());
		suite.addTest(DateSpinnerModelAdapterTests.suite());
		suite.addTest(DocumentAdapterTests.suite());
		suite.addTest(ListModelAdapterTests.suite());
		suite.addTest(ListSpinnerModelAdapterTests.suite());
		suite.addTest(NumberSpinnerModelAdapterTests.suite());
		suite.addTest(ObjectListSelectionModelTests.suite());
		suite.addTest(PrimitiveListTreeModelTests.suite());
		suite.addTest(RadioButtonModelAdapterTests.suite());
		suite.addTest(SpinnerModelAdapterTests.suite());
		suite.addTest(TableModelAdapterTests.suite());
		suite.addTest(TreeModelAdapterTests.suite());
	
		return suite;
	}
	
	private AllUIToolsAppSwingTests() {
		super();
	}
	
}
