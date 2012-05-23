/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.adapter;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DataSource;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;

public class SessionBrokerAdapterTest extends AbstractAdapterTest
{
	public SessionBrokerAdapterTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(SessionBrokerAdapterTest.class, "SessionBrokerAdater Test");
	}

	public void testAddSession() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());
		DataSource ds = buildOracleDataSource();

		sessions.addDatabaseSessionNamed("SC-EmployeeTest", noServerPlatform(), ds);
		final Vector result = new Vector(1);

		sessionBroker.addCollectionChangeListener(SessionBrokerAdapter.SESSIONS_COLLECTION, new CollectionChangeListener()
		{
			public void collectionChanged(CollectionChangeEvent e)
			{
				fail("CollectionChangeListener.collectionChanged should not have been called");
			}

			public void itemsAdded(CollectionChangeEvent e)
			{
				result.add(Boolean.TRUE);
			}

			public void itemsRemoved(CollectionChangeEvent e)
			{
				fail("CollectionChangeListener.itemsRemoved should not have been called");
			}
		});

		sessionBroker.manage("SC-EmployeeTest");
		assertTrue("No even was fired when adding a session to the broker", !result.isEmpty());
		assertTrue("The session was not properly added", sessionBroker.sessionsSize() == 1);
	}

	public void testRemoveSession() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());
		DataSource ds = buildOracleDataSource();

		final Vector result = new Vector(1);

		sessions.addDatabaseSessionNamed("SC-EmployeeTest", noServerPlatform(), ds);
		sessionBroker.manage("SC-EmployeeTest");
		sessionBroker.addCollectionChangeListener(SessionBrokerAdapter.SESSIONS_COLLECTION, new CollectionChangeListener()
		{
			public void collectionChanged(CollectionChangeEvent e)
			{
				result.add(Boolean.TRUE);
			}

			public void itemsAdded(CollectionChangeEvent e)
			{
				fail("CollectionChangeListener.itemsAdded should not have been called");
			}

			public void itemsRemoved(CollectionChangeEvent e)
			{
				fail("CollectionChangeListener.itemsRemoved should not have been called");
			}
		});

		sessionBroker.unManage("SC-EmployeeTest");
		assertTrue("No even was fired when removing a session to the broker", !result.isEmpty());
		assertTrue("The session was not removed", sessionBroker.sessionsSize() == 0);
	}

	public void testRemoveSessions1() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());
		DataSource ds = buildOracleDataSource();

		final Vector sessionList = new Vector(2);
		final Vector result = new Vector(1);

		sessionList.add(sessions.addDatabaseSessionNamed("SC-EmployeeTest", noServerPlatform(), ds));
		sessionList.add(sessions.addDatabaseSessionNamed("SC-EmployeeTest2", noServerPlatform(), ds));

		sessionBroker.manage("SC-EmployeeTest");
		sessionBroker.manage("SC-EmployeeTest2");
		sessionBroker.addCollectionChangeListener(SessionBrokerAdapter.SESSIONS_COLLECTION, new CollectionChangeListener()
		{
			public void collectionChanged(CollectionChangeEvent e)
			{
				result.add(Boolean.TRUE);
			}

			public void itemsAdded(CollectionChangeEvent e)
			{
				fail("CollectionChangeListener.itemsAdded should not have been called");
			}

			public void itemsRemoved(CollectionChangeEvent e)
			{
				fail("CollectionChangeListener.itemsRemoved should not have been called");
			}
		});

		sessionBroker.unManage(sessionList);
		assertTrue("No even was fired when removing a session to the broker", !result.isEmpty());
		assertTrue("Not all the sessions were removed", sessionBroker.sessionsSize() == 0);
	}

	public void testRemoveSessions2() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());
		DataSource ds = buildOracleDataSource();

		final Vector sessionList = new Vector(2);
		final Vector result = new Vector(1);

		sessionList.add(sessions.addDatabaseSessionNamed("SC-EmployeeTest", noServerPlatform(), ds));
		sessionList.add(sessions.addDatabaseSessionNamed("SC-EmployeeTest2", noServerPlatform(), ds));

		sessionBroker.manage("SC-EmployeeTest");
		sessionBroker.manage("SC-EmployeeTest2");

		CollectionAspectAdapter adapter = new CollectionAspectAdapter(SessionBrokerAdapter.SESSIONS_COLLECTION, sessionBroker)
		{
			protected Iterator getValueFromSubject()
			{
				return ((SessionBrokerAdapter) subject).sessions();
			}
		};

		adapter.addCollectionChangeListener(CollectionAspectAdapter.VALUE, new CollectionChangeListener()
		{
			public void collectionChanged(CollectionChangeEvent e)
			{
				result.add(Boolean.TRUE);
			}

			public void itemsAdded(CollectionChangeEvent e)
			{
				fail("CollectionChangeListener.itemsAdded should not have been called");
			}

			public void itemsRemoved(CollectionChangeEvent e)
			{
				fail("CollectionChangeListener.itemsRemoved should not have been called");
			}
		});

		sessionBroker.unManage(sessionList);
		assertTrue("No even was fired when removing a session to the broker", !result.isEmpty());
		assertTrue("Not all the sessions were removed", sessionBroker.sessionsSize() == 0);
	}

	public void testVerifyProblemSessionCount() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());
		DataSource ds = buildOracleDataSource();

		assertTrue("No session - Should have problem",
					  hasProblem(SCProblemsConstants.SESSION_BROKER_SESSION_COUNT, sessionBroker));

		sessions.addDatabaseSessionNamed("SC-EmployeeTest", noServerPlatform(), ds);
		sessionBroker.manage("SC-EmployeeTest");

		assertFalse("Contain session - Should not have problem",
			hasProblem(SCProblemsConstants.SESSION_BROKER_SESSION_COUNT, sessionBroker));
	}
}
