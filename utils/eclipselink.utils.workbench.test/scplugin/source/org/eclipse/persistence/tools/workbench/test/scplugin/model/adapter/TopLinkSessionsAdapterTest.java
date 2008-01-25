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

import java.io.File;

import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public class TopLinkSessionsAdapterTest extends AbstractAdapterTest
{
	public TopLinkSessionsAdapterTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(TopLinkSessionsAdapterTest.class, "TopLinkSessionsAdapter Test");
	}

	public void testLoadFileAlreadyOnClasspath() throws Exception
	{
		File location = FileTools.resourceFile("/SessionsXMLTestModel/XMLSchemaDatabaseSession.xml", getClass());

		TopLinkSessionsAdapter adapter = AllSCTests.loadSessions(location);
		assertTrue(adapter.sessionsSize() > 0);
	}
}