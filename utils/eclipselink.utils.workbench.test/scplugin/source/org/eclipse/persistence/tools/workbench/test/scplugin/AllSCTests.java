/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.scplugin;

// JDK
import java.io.File;
import java.net.URL;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsProperties;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsPropertiesManager;
import org.eclipse.persistence.tools.workbench.test.scplugin.model.adapter.AllSCAdapterTests;
import org.eclipse.persistence.tools.workbench.test.scplugin.model.meta.SCSessionsPropertiesIOTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.model.read.AllSCReadTests;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.AllSCUITests;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.tools.AllSCUIToolsTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * This test class contains all the Session Configuration JUnit tests.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class AllSCTests
{
	private AllSCTests()
	{
		super();
		throw new UnsupportedOperationException();
	}

	public static TopLinkSessionsAdapter createNewSessions() throws Exception
	{
		Preferences preferences = Preferences.userNodeForPackage(AllSCTests.class);

		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);
		SCSessionsProperties properties = manager.getSessionsProperties(new File("/sessions.xml"));

		return new TopLinkSessionsAdapter(properties, preferences, true);
	}

	public static TopLinkSessionsAdapter loadSessions(String path, Class senderClass) throws Exception
	{
		File scXmlLocation = FileTools.resourceFile(path, senderClass);
		return loadSessions(scXmlLocation);
	}
	
	public static TopLinkSessionsAdapter loadSessions(File location) throws Exception
	{
		Preferences preferences = Preferences.userNodeForPackage(AllSCTests.class);

		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);
		SCSessionsProperties properties = manager.getSessionsProperties(location);

		return new TopLinkSessionsAdapter(properties, preferences, false);
	}

	public static TopLinkSessionsAdapter loadSessions(URL location) throws Exception
	{
		return loadSessions(FileTools.buildFile(location));
	}

	public static Test suite()
	{
		return suite(true);
	}

	public static Test suite(boolean all)
	{
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllSCTests.class));

		suite.addTest(SCSessionsPropertiesIOTest.suite());
		suite.addTest(AllSCAdapterTests.suite());
		suite.addTest(AllSCReadTests.suite());
		suite.addTest(AllSCUIToolsTests.suite());

		// Set whether the UI tests can be performed
		boolean executeUITests = Boolean.valueOf(System.getProperty("uitest", "false")).booleanValue();

		if (executeUITests) {
			suite.addTest(AllSCUITests.suite());
		}

		return suite;
	}

}
