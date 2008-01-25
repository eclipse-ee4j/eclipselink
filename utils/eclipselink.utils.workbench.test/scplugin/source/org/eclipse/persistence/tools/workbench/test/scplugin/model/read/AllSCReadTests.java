/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.scplugin.model.read;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class AllSCReadTests
{
	private AllSCReadTests()
	{
		super();
	}

	public static void main(String[] args)
	{
		TestRunner.main(new String[] { "-c", AllSCReadTests.class.getName() });
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite("SC Model Tests");

		suite.addTest(DatabaseSessionTests.suite());
		suite.addTest(ModelLoadTests.suite());
		suite.addTest(SessionsXml904Test.suite());
		suite.addTest(LoadTopLinkSessionsTest.suite());
		suite.addTest(SequenceTest.suite());

		return suite;
	}
}
