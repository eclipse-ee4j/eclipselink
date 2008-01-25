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

import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DataSource;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ReadConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;

public class ReadConnectionPoolAdapterTest extends AbstractAdapterTest
{
	public ReadConnectionPoolAdapterTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(ReadConnectionPoolAdapterTest.class, "ReadConnectionPoolAdapter Test");
	}

	public void testUseNonTransactionalReadLogin() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DataSource ds = buildOracleDataSource();
		ServerSessionAdapter session = sessions.addServerSessionNamed("MyServerSession", noServerPlatform(), ds);
		ReadConnectionPoolAdapter pool = session.getReadConnectionPool();

		pool.setUseNonTransactionalReadLogin( true);

		assertTrue("useNonTransactionalReadLogin - Should not have problem", pool.usesNonTransactionalReadLogin());

	}
}