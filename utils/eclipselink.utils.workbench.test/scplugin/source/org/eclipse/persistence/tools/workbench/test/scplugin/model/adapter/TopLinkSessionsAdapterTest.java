/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
