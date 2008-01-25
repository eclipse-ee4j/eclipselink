/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.uitools.swing;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class AllUIToolsSwingTests
{
	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllUIToolsSwingTests.class.getName()});
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllUIToolsSwingTests.class));
		
		suite.addTest(ExtendedListModelTests.suite());
		suite.addTest(ExtendedComboBoxModelTests.suite());
		suite.addTest(IndirectComboBoxModelTests.suite());
		
		return suite;
	}
	
	private AllUIToolsSwingTests() {
		super();
	}
}
