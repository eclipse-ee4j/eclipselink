/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.tools;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class AllSCUIToolsTests
{
	private AllSCUIToolsTests()
	{
		super();
	}

	public static void main(String[] args)
	{
		TestRunner.main(new String[] { "-c", AllSCUIToolsTests.class.getName() });
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite("SC UI Tools Tests");

		suite.addTest(BooleanCellRendererAdapterTest.suite());
		suite.addTest(IPAddressTest.suite());

		return suite;
	}
}