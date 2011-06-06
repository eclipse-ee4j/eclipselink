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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.meta;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.ClassRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCClassRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsProperties;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsPropertiesManager;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.PluggableValidator;

/**
 * Tests {@link SCSessionsPropertiesIO}
 *
 * @version 10.1.3.0.1
 * @since 10.1.3
 * @author Pascal Filion
 */
public class SCSessionsPropertiesIOTest extends TestCase
{
	private Preferences preferences;
	private String CLASSPATH_1_ENTRY_1 = "/Development/Mapping Workbench/SC/classes";
	private String CLASSPATH_1_ENTRY_2 = "/Development/Mapping Workbench/SC Test/classes";
	private String CLASSPATH_1         = CLASSPATH_1_ENTRY_1 + File.pathSeparator + CLASSPATH_1_ENTRY_2;
	private String CLASSPATH_2         = "/Development/Mapping Workbench/Framework/classes";
	private String CLASSPATH_3_KEY     = "classpath_0";
	private String CLASSPATH_3_VALUE   = "/My Documents/XMLSchemaSessions_3.xml";
	private String CLASSPATH_4_KEY     = "classpath_1";
	private String CLASSPATH_4_VALUE   = "/dev/lib/toplink1.jar" + File.pathSeparator + "/dev/lib/toplink2.jar" + File.pathSeparator + "/dev/lib/toplink3.jar" + File.pathSeparator + "/dev/lib/toplink4.jar" + File.pathSeparator + "/dev/lib/toplink5.jar" + File.pathSeparator + "/dev/lib/toplink6.jar" + File.pathSeparator + "/dev/lib/toplink7.jar" + File.pathSeparator + "/dev/lib/toplink8.jar" + File.pathSeparator + "/dev/lib/toplink9.jar" + File.pathSeparator + "/dev/lib/toplink10.jar" + File.pathSeparator + "/dev/lib/toplink11.jar";
	private String LOCATION_1          = "/My Documents/XMLSchemaSessions_1.xml";
	private String LOCATION_2          = "/My Documents/XMLSchemaSessions_2.xml";
	private String LOCATION_3_KEY      = "location_0";
	private String LOCATION_3_VALUE    = "/My Documents/XMLSchemaSessions_3.xml";
	private String LOCATION_4_KEY      = "location_1";
	private String LOCATION_4_VALUE    = "My Documents/My Working Folder/Another folder/A very long folder name with some spaces/yet another sub-directory/my sessions files/new folder/temp/dev/new folder/temp/dev/new folder/temp/dev/new folder/temp/dev/new folder/temp/dev/new folder/temp/dev/new folder/temp/devsessions_4.xml";

	public SCSessionsPropertiesIOTest(String name)
	{
		super(name);
	}

	public static void main(String[] arguments)
	{
		TestRunner.run(SCSessionsPropertiesIOTest.class);
	}

	public static Test suite()
	{
		return new TestSuite(SCSessionsPropertiesIOTest.class, "Class Repository Test");
	}

	private void _testSessionsProperties_1(SCSessionsProperties properties) throws Exception
	{
		assertEquals(LOCATION_1, properties.getPathString());

		SCClassRepository classRepository = (SCClassRepository) properties.getClassRepository();
		assertNotNull(classRepository);

		assertEquals(2, classRepository.classpathEntriesSize());

		Iterator iter = classRepository.classpathEntries();
		assertEquals(CLASSPATH_1_ENTRY_1, (String) iter.next());
		assertEquals(CLASSPATH_1_ENTRY_2, (String) iter.next());

		String locationKey  = SCSessionsProperties.LOCATION_TAG  + properties.getIndex();
		String classpathKey = SCSessionsProperties.CLASSPATH_TAG + properties.getIndex();

		Preferences classpathPreferences = preferences.node(SCSessionsProperties.CLASSPATH_NODE_TAG);
		String keys[] = classpathPreferences.keys();
		assertFalse (CollectionTools.contains(keys, LOCATION_1));
		assertTrue  (CollectionTools.contains(keys, locationKey));
		assertTrue  (CollectionTools.contains(keys, classpathKey));
		assertEquals(properties.getPathString(), classpathPreferences.get(locationKey,  null));
		assertEquals(classRepository.entries(),  classpathPreferences.get(classpathKey, null));
	}

	private void _testSessionsProperties_2(SCSessionsProperties properties) throws Exception
	{
		assertEquals(LOCATION_2, properties.getPathString());

		SCClassRepository classRepository = (SCClassRepository) properties.getClassRepository();
		assertNotNull(classRepository);

		assertEquals(1, classRepository.classpathEntriesSize());
		assertEquals(CLASSPATH_2, classRepository.entries());

		String locationKey  = SCSessionsProperties.LOCATION_TAG  + properties.getIndex();
		String classpathKey = SCSessionsProperties.CLASSPATH_TAG + properties.getIndex();

		Preferences classpathPreferences = preferences.node(SCSessionsProperties.CLASSPATH_NODE_TAG);
		String keys[] = classpathPreferences.keys();
		assertFalse (CollectionTools.contains(keys, LOCATION_1));
		assertTrue  (CollectionTools.contains(keys, locationKey));
		assertTrue  (CollectionTools.contains(keys, classpathKey));
		assertEquals(properties.getPathString(), classpathPreferences.get(locationKey,  null));
		assertEquals(classRepository.entries(),  classpathPreferences.get(classpathKey, null));
	}

	private void _testSessionsProperties_3(SCSessionsProperties properties) throws Exception
	{
		assertEquals(LOCATION_3_VALUE, properties.getPathString());

		SCClassRepository classRepository = (SCClassRepository) properties.getClassRepository();
		assertNotNull(classRepository);

		assertEquals(1, classRepository.classpathEntriesSize());
		assertEquals(CLASSPATH_3_VALUE, classRepository.entries());

		Preferences classpathPreferences = preferences.node(SCSessionsProperties.CLASSPATH_NODE_TAG);
		String keys[] = classpathPreferences.keys();

		String locationKey  = SCSessionsProperties.LOCATION_TAG  + properties.getIndex();
		String classpathKey = SCSessionsProperties.CLASSPATH_TAG + properties.getIndex();

		assertTrue  (CollectionTools.contains(keys, locationKey));
		assertTrue  (CollectionTools.contains(keys, classpathKey));
		assertEquals(properties.getPathString(), classpathPreferences.get(locationKey,  null));
		assertEquals(classRepository.entries(),  classpathPreferences.get(classpathKey, null));
	}

	private void _testSessionsProperties_4(SCSessionsProperties properties) throws Exception
	{
		assertEquals(LOCATION_4_VALUE, properties.getPathString());

		SCClassRepository classRepository = (SCClassRepository) properties.getClassRepository();
		assertNotNull(classRepository);

		assertEquals(11, classRepository.classpathEntriesSize());
		assertEquals(CLASSPATH_4_VALUE, classRepository.entries());

		Preferences classpathPreferences = preferences.node(SCSessionsProperties.CLASSPATH_NODE_TAG);
		String keys[] = classpathPreferences.keys();

		String locationKey  = SCSessionsProperties.LOCATION_TAG  + properties.getIndex();
		String classpathKey = SCSessionsProperties.CLASSPATH_TAG + properties.getIndex();

		assertTrue  (CollectionTools.contains(keys, locationKey));
		assertTrue  (CollectionTools.contains(keys, classpathKey));
		assertEquals(properties.getPathString(), classpathPreferences.get(locationKey,  null));
		assertEquals(classRepository.entries(),  classpathPreferences.get(classpathKey, null));
	}

	private Collection sessionsProperties(SCSessionsPropertiesManager manager) throws Exception
	{
		Field sessionsPropertiesField = manager.getClass().getDeclaredField("sessionsProperties");
		sessionsPropertiesField.setAccessible(true);
		return (Collection) sessionsPropertiesField.get(manager);
	}

	protected void setUp() throws Exception
	{
		this.preferences = Preferences.userNodeForPackage(AllSCTests.class);

		// Make sure the preferences is clean
		tearDown();

		this.preferences = Preferences.userNodeForPackage(AllSCTests.class);
		Preferences classpathPreferences = this.preferences.node(SCSessionsProperties.CLASSPATH_NODE_TAG);

		// 10.1.3.0.0 format
		classpathPreferences.put(LOCATION_1, CLASSPATH_1);

		// 10.1.3.0.0 format
		classpathPreferences.put(LOCATION_2, CLASSPATH_2);

		// 10.1.3.x format
		classpathPreferences.put( LOCATION_3_KEY,  LOCATION_3_VALUE);
		classpathPreferences.put(CLASSPATH_3_KEY, CLASSPATH_3_VALUE);

		// 10.1.3.x format
		classpathPreferences.put( LOCATION_4_KEY,  LOCATION_4_VALUE);
		classpathPreferences.put(CLASSPATH_4_KEY, CLASSPATH_4_VALUE);
	}

	protected void tearDown() throws Exception
	{
		//classpath-scplugin-workbench-toplink- oracle - test
		preferences.parent().parent().parent().parent().removeNode();
	}

	public void testAddSessionsProperties() throws Exception
	{
		Preferences preferences = Preferences.userNodeForPackage(AllSCTests.class);

		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);

		File newFile = new File("/sessions.xml");
		SCSessionsProperties sessionsProperties = manager.getSessionsProperties(newFile);
		assertNotNull(sessionsProperties);
		assertEquals(newFile, sessionsProperties.getPath());
	}

	public void testChangeSessionsPropertiesPath() throws Exception
	{
		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);

		File file = new File(LOCATION_1);
		SCSessionsProperties sessionsProperties = manager.getSessionsProperties(file);
		assertNotNull(sessionsProperties);

		File newFile = new File("/XMLSchemaSessions.xml");
		sessionsProperties.saveAs(preferences, newFile);
		assertEquals(newFile, sessionsProperties.getPath());

		SCSessionsProperties temp = manager.getSessionsProperties(newFile);
		assertEquals(temp.getPath(), sessionsProperties.getPath());
	}

	public void testContent() throws Exception
	{
		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);

		Collection sessionsProperties = sessionsProperties(manager);
		Iterator iter = sessionsProperties.iterator();
		assertNotNull(iter);

		while (iter.hasNext())
		{
			SCSessionsProperties properties = (SCSessionsProperties) iter.next();
			String location = properties.getPathString();

			if (location.equals(LOCATION_1))
			{
				_testSessionsProperties_1(properties);
			}
			else if (location.equals(LOCATION_2))
			{
				_testSessionsProperties_2(properties);
			}
			else if (location.equals(LOCATION_3_VALUE))
			{
				_testSessionsProperties_3(properties);
			}
			else if (location.equals(LOCATION_4_VALUE))
			{
				_testSessionsProperties_4(properties);
			}
			else
			{
				fail("Unknown properties: " + properties.getPathString());
			}
		}
	}

	public void testLoadingFile() throws Exception
	{
		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);
		assertNotNull(manager);
	}

	public void testLoadingNonExistingFile() throws Exception
	{
		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);
		assertNotNull(manager);
	}

	public void testOverridingSessionsProperties() throws Exception
	{
		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);

		// A file location contained in the sc.xml
		File file = new File(LOCATION_1);

		// Create a new properties
		File untitledFile = manager.nextUntitledSessionsFile();
		SCSessionsProperties sessionsProperties = manager.getSessionsProperties(untitledFile);
		ClassRepository repository = sessionsProperties.getClassRepository();
		repository.setParent(new FakeParentNode());
		repository.addClasspathEntry(0, "/MyJarFile.jar");

		// Perform a save as
		sessionsProperties.saveAs(preferences, file);

		// Make sure the old one has been replaced
		sessionsProperties = manager.getSessionsProperties(file);
		repository = sessionsProperties.getClassRepository();
		assertEquals(1, repository.classpathEntriesSize());
		assertEquals(repository.classpathEntries().next(), "/MyJarFile.jar");
	}

	public void testWrite() throws Exception
	{
		SCSessionsPropertiesManager manager = new SCSessionsPropertiesManager(preferences);
		Preferences classpathPreferences = preferences.node(SCSessionsProperties.CLASSPATH_NODE_TAG);

		// Change location
		File file = new File(LOCATION_1);
		File newFile = new File("/XMLSchemaSessions.xml");
		SCSessionsProperties sessionsProperties = manager.getSessionsProperties(file);
		SCClassRepository classRepository = (SCClassRepository) sessionsProperties.getClassRepository();
		int oldIndex = sessionsProperties.getIndex();

		sessionsProperties.saveAs(preferences, newFile);
		int newIndex = sessionsProperties.getIndex();

		assertEquals(oldIndex, newIndex);
		assertEquals(sessionsProperties.getPathString(),
		             classpathPreferences.get(SCSessionsProperties.LOCATION_TAG + newIndex, null));

		assertEquals(classRepository.entries(),
		             classpathPreferences.get(SCSessionsProperties.CLASSPATH_TAG + newIndex, null));

		// Add a new one
		SCSessionsProperties newProperties = manager.getSessionsProperties(new File("/sessions.xml"));
		newProperties.getClassRepository().setParent(new FakeParentNode());
		newProperties.getClassRepository().addClasspathEntry(0, "/");
		newProperties.save(preferences);

		newIndex = newProperties.getIndex();
		assertEquals(4, newIndex);

		assertEquals(newProperties.getPathString(),
		             classpathPreferences.get(SCSessionsProperties.LOCATION_TAG + newIndex, null));

		classRepository = (SCClassRepository) newProperties.getClassRepository();
		assertEquals(classRepository.entries(),
		             classpathPreferences.get(SCSessionsProperties.CLASSPATH_TAG + newIndex, null));
	}

	private class FakeParentNode extends AbstractNodeModel
	{
		private Validator validator;

		private FakeParentNode()
		{
			super();
			setValidator(PluggableValidator.buildSynchronousValidator(this));
		}

		public String displayString()
		{
			return "";
		}

		public Validator getValidator()
		{
			return this.validator;
		}

		public void setValidator(Validator validator)
		{
			this.validator = validator;
		}
	}
}
