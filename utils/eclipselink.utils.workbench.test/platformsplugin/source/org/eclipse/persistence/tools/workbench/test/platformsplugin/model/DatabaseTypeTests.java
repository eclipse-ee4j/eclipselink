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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Types;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.platformsmodel.CorruptXMLException;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCType;
import org.eclipse.persistence.tools.workbench.test.platformsplugin.TestDatabasePlatformRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class DatabaseTypeTests extends TestCase {
	private DatabasePlatform fooPlatform;
	private DatabasePlatform barPlatform;
	private DatabasePlatformRepository repository;

	public static Test suite() {
		return new TestSuite(DatabaseTypeTests.class);
	}
	
	public DatabaseTypeTests(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		this.repository = TestDatabasePlatformRepositoryFactory.instance().createRepository();
		this.fooPlatform = this.repository.platformNamed("Foo Platform");
		this.barPlatform = this.repository.platformNamed("Bar Platform");
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testName() throws Exception {
		DatabaseType numberType = this.fooPlatform.databaseTypeNamed("NUMBER");
		numberType.setName("FRED");	// no problem
		numberType.setName("NUMBER");	// no problem
		boolean exCaught = false;
		try {
			numberType.setName("DATE");
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("DATE") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			numberType.setName("");
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			numberType.setName(null);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testAllowsSize() {
		DatabaseType fooType = this.fooPlatform.addDatabaseType("FOO");
		assertTrue(fooType.allowsSize());	// default
		assertFalse(fooType.allowsSubSize());	// default
		assertFalse(fooType.requiresSize());	// default

		fooType.setAllowsSubSize(true);
		fooType.setRequiresSize(true);
		fooType.setAllowsSize(false);
		assertFalse(fooType.allowsSubSize());
		assertFalse(fooType.requiresSize());
	}

	public void testRequiresSize() {
		DatabaseType fooType = this.fooPlatform.addDatabaseType("FOO");
		assertTrue(fooType.allowsSize());	// default
		assertFalse(fooType.allowsSubSize());	// default
		assertFalse(fooType.requiresSize());	// default
		assertEquals(0, fooType.getInitialSize());	// default

		fooType.setAllowsSize(false);
		fooType.setRequiresSize(true);
		fooType.setInitialSize(20);
		assertTrue(fooType.allowsSize());

		fooType.setRequiresSize(false);
		assertEquals(0, fooType.getInitialSize());
	}

	public void testInitialSize() {
		DatabaseType fooType = this.fooPlatform.addDatabaseType("FOO");
		assertTrue(fooType.allowsSize());	// default
		assertFalse(fooType.allowsSubSize());	// default
		assertFalse(fooType.requiresSize());	// default
		assertEquals(0, fooType.getInitialSize());	// default

		fooType.setAllowsSize(false);
		fooType.setInitialSize(20);
		assertTrue(fooType.allowsSize());
		assertTrue(fooType.requiresSize());
	}

	public void testRemoveJDBCType() {
		JDBCType jdbcType = this.repository.getJDBCTypeRepository().jdbcTypeForCode(Types.DECIMAL);
		JDBCType defaultJDBCType = this.repository.getJDBCTypeRepository().getDefaultJDBCType();
		assertTrue(jdbcType != defaultJDBCType);
		DatabaseType numberType = this.fooPlatform.databaseTypeNamed("NUMBER");
		assertEquals(jdbcType, numberType.getJDBCType());

		this.repository.getJDBCTypeRepository().removeJDBCType(jdbcType);
		assertEquals(defaultJDBCType, numberType.getJDBCType());
	}

	public void testCorruptXMLMissingName() throws Exception {
		this.verifyCorruptXML("<name>BLOB</name>", "<bogus-name>BLOB</bogus-name>");
	}

	public void testCorruptXMLInvalidJDBCType() throws Exception {
		this.verifyCorruptXML("<database-type>\\s*<name>DECIMAL</name>\\s*<jdbc-type>DECIMAL</jdbc-type>",
				"<database-type><name>DECIMAL</name><jdbc-type>XXXX</jdbc-type>");
	}

	public void testCorruptXMLInvalidRequiresSize() throws Exception {
		this.verifyCorruptXML("<database-type>\\s*<name>TIMESTAMP</name>\\s*<jdbc-type>DATE</jdbc-type>\\s*<allows-null>true</allows-null>\\s*</database-type>",
				"<database-type><name>TIMESTAMP</name><jdbc-type>DATE</jdbc-type><allows-null>true</allows-null><requires-size>true</requires-size></database-type>");
	}

	public void testCorruptXMLInvalidDefaultSize() throws Exception {
		this.verifyCorruptXML("<database-type>\\s*<name>TIMESTAMP</name>\\s*<jdbc-type>DATE</jdbc-type>\\s*<allows-null>true</allows-null>\\s*</database-type>",
				"<database-type><name>TIMESTAMP</name><jdbc-type>DATE</jdbc-type><allows-null>true</allows-null><initial-size>77</initial-size></database-type>");
	}

	public void testCorruptXMLInvalidAllowsSubSize() throws Exception {
		this.verifyCorruptXML("<database-type>\\s*<name>TIMESTAMP</name>\\s*<jdbc-type>DATE</jdbc-type>\\s*<allows-null>true</allows-null>\\s*</database-type>",
				"<database-type><name>TIMESTAMP</name><jdbc-type>DATE</jdbc-type><allows-null>true</allows-null><allows-sub-size>true</allows-sub-size></database-type>");
	}

	private void verifyCorruptXML(String string, String bogusString) throws Exception {
		this.verifyCorruptXML(new String[] {string, bogusString});
	}

	private void verifyCorruptXML(String[] stringPairs) throws Exception {
		File reposFile = this.buildTestRepositoryFile();
		this.repository.setFile(reposFile);
		// see comment at DPRTests.write()
		DatabasePlatformRepositoryTests.write(this.repository);
		File dir = reposFile.getParentFile();
		dir = new File(dir, this.platformsDirectoryName());
		File platformFile = new File(dir, this.barPlatform.getShortFileName());

		InputStream inStream = new BufferedInputStream(new FileInputStream(platformFile));
		int fileSize = inStream.available();
		byte[] buf = new byte[fileSize];
		inStream.read(buf);
		inStream.close();

		String rawDocument = new String(buf);
		for (int i = 0; i < stringPairs.length; ) {
			rawDocument = rawDocument.replaceAll(stringPairs[i], stringPairs[i + 1]);
			i += 2;
		}
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(platformFile), 2048);
		outStream.write(rawDocument.getBytes());
		outStream.close();
		boolean exCaught = false;
		try {
			DatabasePlatformRepository bogusRepository = new DatabasePlatformRepository(reposFile);
			assertNull(bogusRepository);
		} catch (CorruptXMLException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	private File buildTestRepositoryFile() throws Exception {
		return new File(this.buildTestRepositoryDirectory(), "test repos.dpr");
	}

	private File buildTestRepositoryDirectory() throws Exception {
		// C:/temp/DatabasePlatformTests
		return FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName());
	}

	private String platformsDirectoryName() {
		return TestDatabasePlatformRepositoryFactory.platformsDirectoryName();
	}

}
