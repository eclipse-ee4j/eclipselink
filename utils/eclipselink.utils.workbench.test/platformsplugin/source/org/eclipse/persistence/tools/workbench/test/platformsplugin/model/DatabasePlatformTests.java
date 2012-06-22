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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Types;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.platformsmodel.CorruptXMLException;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeToDatabaseTypeMapping;
import org.eclipse.persistence.tools.workbench.test.platformsplugin.TestDatabasePlatformRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class DatabasePlatformTests extends TestCase {
	private DatabasePlatform fooPlatform;
	private DatabasePlatform barPlatform;
	private DatabasePlatformRepository repository;

	public static Test suite() {
		return new TestSuite(DatabasePlatformTests.class);
	}
	
	public DatabasePlatformTests(String name) {
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
		this.fooPlatform.setName("X Platform");	// no problem
		this.fooPlatform.setName("Foo Platform");	// no problem
		boolean exCaught = false;
		try {
			this.fooPlatform.setName("Bar Platform");
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("Bar Platform") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			this.fooPlatform.setName("");
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			this.fooPlatform.setName(null);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testShortFileName() throws Exception {
		this.fooPlatform.setShortFileName("x.xml");	// no problem
		this.fooPlatform.setShortFileName("fooplatform.xml");	// no problem
		boolean exCaught = false;
		try {
			this.fooPlatform.setShortFileName("barplatform.xml");
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("barplatform.xml") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			this.fooPlatform.setShortFileName("");
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			this.fooPlatform.setShortFileName(null);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testRuntimePlatformClassName() throws Exception {
		boolean exCaught = false;
		try {
			this.fooPlatform.setRuntimePlatformClassName("");
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			this.fooPlatform.setRuntimePlatformClassName(null);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testNativeSequencing() {
		assertTrue(this.barPlatform.supportsNativeSequencing());
		assertTrue(this.barPlatform.supportsIdentityClause());

		// this should cause the IDENTITY clause support to be set to false
		this.barPlatform.setSupportsNativeSequencing(false);
		assertFalse(this.barPlatform.supportsIdentityClause());
	}

	public void testRemoveType() {
		DatabaseType dateType = this.fooPlatform.databaseTypeNamed("DATE");
		JDBCTypeToDatabaseTypeMapping mapping = this.jdbcMapping(this.fooPlatform, Types.DATE);
		assertEquals(dateType, mapping.getDatabaseType());

		this.fooPlatform.removeDatabaseType(dateType);
		assertNull(mapping.getDatabaseType());
	}

	private JDBCTypeToDatabaseTypeMapping jdbcMapping(DatabasePlatform platform, int jdbcCode) {
		for (Iterator stream = platform.jdbcTypeToDatabaseTypeMappings(); stream.hasNext(); ) {
			JDBCTypeToDatabaseTypeMapping mapping = (JDBCTypeToDatabaseTypeMapping) stream.next();
			if (mapping.getJDBCType().getCode() == jdbcCode) {
				return mapping;
			}
		}
		throw new IllegalArgumentException("invalid JDBC code: " + jdbcCode);
	}

	public void testMissingDatabaseType() {
		boolean exCaught = false;
		try {
			this.fooPlatform.databaseTypeNamed("XXX");
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("XXX") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	public void testMatchingType() {
		DatabaseType fooType = this.fooPlatform.databaseTypeNamed("NUMBER");
		DatabasePlatform bazPlatform = this.repository.platformNamed("Baz Platform");
		DatabaseType bazType = bazPlatform.databaseTypeFor(fooType);
		assertEquals("INT", bazType.getName());
		bazType = bazPlatform.addDatabaseType("NUMBER");
		assertEquals(bazType, bazPlatform.databaseTypeFor(fooType));
	}

	public void testDatabaseTypeForJavaTypeDeclaration() {
		DatabaseType numberType = this.fooPlatform.databaseTypeNamed("NUMBER");
		assertEquals(numberType, this.fooPlatform.databaseTypeForJavaTypeDeclaration("int", 0));

		DatabaseType stringType = this.fooPlatform.databaseTypeNamed("STRING");
		assertEquals(stringType, this.fooPlatform.databaseTypeForJavaTypeDeclaration("java.lang.String", 0));
		assertEquals(stringType, this.fooPlatform.databaseTypeForJavaTypeDeclaration("byte", 1));	// byte[]

		DatabaseType dateType = this.fooPlatform.databaseTypeNamed("DATE");
		assertEquals(dateType, this.fooPlatform.databaseTypeForJavaTypeDeclaration("java.util.Date", 0));
	}

	public void testRemoveJDBCType() {
		JDBCType jdbcType = this.repository.getJDBCTypeRepository().jdbcTypeForCode(Types.DECIMAL);
		this.jdbcMapping(this.fooPlatform, Types.DECIMAL);
		this.repository.getJDBCTypeRepository().removeJDBCType(jdbcType);

		boolean exCaught = false;
		try {
			this.jdbcMapping(this.fooPlatform, Types.DECIMAL);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testAddJDBCType() {
		boolean exCaught = false;
		try {
			this.jdbcMapping(this.fooPlatform, 7777);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);

		JDBCType jdbcType = this.repository.getJDBCTypeRepository().addJDBCType("FOO", 7777);

		JDBCTypeToDatabaseTypeMapping mapping = this.jdbcMapping(this.fooPlatform, 7777);
		assertEquals(jdbcType, mapping.getJDBCType());
	}

	public void testCorruptXMLMissingRoot() throws Exception {
		this.verifyCorruptXML(new String[] {"<database-platform>", "<bogus>", "</database-platform>", "</bogus>"});
	}

	public void testCorruptXMLMissingName() throws Exception {
		this.verifyCorruptXML("<name>" + this.barPlatform.getName() + "</name>", "<bogus-name>" + this.barPlatform.getName() + "</bogus-name>");
	}

	public void testCorruptXMLMissingRuntimePlatformClass() throws Exception {
		this.verifyCorruptXML("<runtime-platform-class>" + this.barPlatform.getRuntimePlatformClassName() + "</runtime-platform-class>",
						"<bogus-class>" + this.barPlatform.getRuntimePlatformClassName() + "</bogus-class>");
	}

	public void testCorruptXMLInvalidIdentityClauseSupport() throws Exception {
		this.verifyCorruptXML("<supports-native-sequencing>true</supports-native-sequencing>", "<supports-native-sequencing>false</supports-native-sequencing>");
	}

	public void testCorruptXMLDuplicateType() throws Exception {
		this.verifyCorruptXML("<name>BLOB</name>", "<name>VARCHAR</name>");
	}

	public void testCorruptXMLDuplicateJDBCToJava() throws Exception {
		this.verifyCorruptXML("<jdbc-type>BIT</jdbc-type>", "<jdbc-type>VARCHAR</jdbc-type>");
	}

	public void testCorruptXMLInvalidJDBCToJava() throws Exception {
		this.verifyCorruptXML("<jdbc-mapping>\\s*<jdbc-type>DATE</jdbc-type>\\s*<database-type>TIMESTAMP</database-type>\\s*</jdbc-mapping>", "");
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
