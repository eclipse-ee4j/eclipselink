/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.uitools.app.adapters;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * 
 */
public class AllUIToolsAppAdaptersTests {
	
	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllUIToolsAppAdaptersTests.class.getName()});
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllUIToolsAppAdaptersTests.class));

		suite.addTest(PreferencesCollectionValueModelTests.suite());
		suite.addTest(PreferencePropertyValueModelTests.suite());
	
		return suite;
	}
	
	private AllUIToolsAppAdaptersTests() {
		super();
	}
	
}
