/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.scplugin.model.adapter;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class AllSCAdapterTests
{
	private AllSCAdapterTests()
	{
		super();
	}

	public static void main(String[] args)
	{
		TestRunner.main(new String[] { "-c", AllSCAdapterTests.class.getName() });
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite("SCAdapter Tests");
		
		suite.addTest(DatabaseLoginAdapterTest.suite());
		suite.addTest(DatabaseSessionAdapterTest.suite());
		suite.addTest(DefaultSessionLogAdapterTest.suite());
		suite.addTest(SCPlatformManagerTest.suite());
		suite.addTest(ReadConnectionPoolAdapterTest.suite());
		suite.addTest(SessionBrokerAdapterTest.suite());
		suite.addTest(TopLinkSessionsAdapterTest.suite());

		return suite;
	}
}
