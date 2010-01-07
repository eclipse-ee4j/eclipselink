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
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.platformsmodel.CorruptXMLException;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeRepository;
import org.eclipse.persistence.tools.workbench.test.platformsplugin.TestDatabasePlatformRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class JDBCTypeRepositoryTests extends TestCase {
	private JDBCTypeRepository repository;

	public static Test suite() {
		return new TestSuite(JDBCTypeRepositoryTests.class);
	}
	
	public JDBCTypeRepositoryTests(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		this.repository = TestDatabasePlatformRepositoryFactory.instance().createRepository().getJDBCTypeRepository();
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testAddJDBCToJavaMapping() {
		boolean exCaught = false;
		try {
			this.repository.addJDBCTypeToJavaTypeDeclarationMapping(this.repository.jdbcTypeForCode(Types.VARCHAR), "java.lang.Foo", 0);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testAddJavaToJDBCMapping() {
		// should be OK
		this.repository.addJavaTypeDeclarationToJDBCTypeMapping("java.lang.Foo", 0, this.repository.jdbcTypeForCode(Types.VARCHAR));
		boolean exCaught = false;
		try {
			this.repository.addJavaTypeDeclarationToJDBCTypeMapping("java.lang.String", 0, this.repository.jdbcTypeForCode(Types.VARCHAR));
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testAddJDBCType() {
		// should be OK
		this.repository.addJDBCType("NEW_JDBC_TYPE", 5555);

		boolean exCaught = false;
		try {
			this.repository.addJDBCType("NEW_JDBC_TYPE", 6666);	// duplicate name
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}

		exCaught = false;
		try {
			this.repository.addJDBCType("VARCHAR", 6666);	// duplicate name
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}

		exCaught = false;
		try {
			this.repository.addJDBCType("ANOTHER_JDBC_TYPE", 5555);	// duplicate code
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			this.repository.addJDBCType("ANOTHER_JDBC_TYPE", Types.VARCHAR);	// duplicate code
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);

		// should be OK
		this.repository.addJDBCType("ANOTHER_JDBC_TYPE", 6666);
	}
	
	public void testRemoveJDBCType() {
		// should be OK
		this.repository.removeJDBCType(this.repository.jdbcTypeForCode(Types.VARBINARY));

		boolean exCaught = false;
		try {
			this.repository.removeJDBCType(this.repository.jdbcTypeForCode(Types.VARCHAR));
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testJDBCTypeForCode() {
		JDBCType jt = this.repository.jdbcTypeForCode(Types.VARCHAR);
		assertEquals("VARCHAR", jt.getName());

		boolean exCaught = false;
		try {
			jt = this.repository.jdbcTypeForCode(666);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testCorruptXMLEmptyRepository() throws Exception {
		this.verifyCorruptXML("<jdbc-types>.*</jdbc-types>", "<jdbc-types></jdbc-types>");
	}

	public void testCorruptXMLInvalidDefaultJDBCType() throws Exception {
		this.verifyCorruptXML("<default-jdbc-type>VARCHAR</default-jdbc-type>", "<default-jdbc-type>XXXX</default-jdbc-type>");
	}

	public void testCorruptXMLDuplicateJDBCType() throws Exception {
		this.verifyCorruptXML("<jdbc-type>\\s*<name>VARCHAR</name>\\s*<code>12</code>\\s*</jdbc-type>",
				"<jdbc-type><name>VARCHAR</name><code>12</code></jdbc-type>" +
				"<jdbc-type><name>VARCHAR</name><code>12</code></jdbc-type>");
	}

	public void testCorruptXMLMissingJDBCToJavaMapping() throws Exception {
		this.verifyCorruptXML("<jdbc-type-to-java-type-declaration-mapping>\\s*<jdbc-type>VARCHAR</jdbc-type>\\s*<java-type-declaration>\\s*<java-class-name>java.lang.String</java-class-name>\\s*</java-type-declaration>\\s*</jdbc-type-to-java-type-declaration-mapping>", "");
	}

	public void testCorruptXMLDuplicateJDBCToJavaMapping() throws Exception {
		this.verifyCorruptXML("<jdbc-type-to-java-type-declaration-mapping>\\s*<jdbc-type>VARCHAR</jdbc-type>\\s*<java-type-declaration>\\s*<java-class-name>java.lang.String</java-class-name>\\s*</java-type-declaration>\\s*</jdbc-type-to-java-type-declaration-mapping>",
				"<jdbc-type-to-java-type-declaration-mapping><jdbc-type>VARCHAR</jdbc-type><java-type-declaration><java-class-name>java.lang.String</java-class-name></java-type-declaration></jdbc-type-to-java-type-declaration-mapping>" +
				"<jdbc-type-to-java-type-declaration-mapping><jdbc-type>VARCHAR</jdbc-type><java-type-declaration><java-class-name>java.lang.String</java-class-name></java-type-declaration></jdbc-type-to-java-type-declaration-mapping>");
	}

	public void testCorruptXMLDuplicateJavaToJDBCMapping() throws Exception {
		this.verifyCorruptXML("<java-type-declaration-to-jdbc-type-mapping>\\s*<java-type-declaration>\\s*<java-class-name>java.lang.String</java-class-name>\\s*</java-type-declaration>\\s*<jdbc-type>VARCHAR</jdbc-type>\\s*</java-type-declaration-to-jdbc-type-mapping>",
				"<java-type-declaration-to-jdbc-type-mapping><java-type-declaration><java-class-name>java.lang.String</java-class-name></java-type-declaration><jdbc-type>VARCHAR</jdbc-type></java-type-declaration-to-jdbc-type-mapping>" +
				"<java-type-declaration-to-jdbc-type-mapping><java-type-declaration><java-class-name>java.lang.String</java-class-name></java-type-declaration><jdbc-type>VARCHAR</jdbc-type></java-type-declaration-to-jdbc-type-mapping>");
	}

	private void verifyCorruptXML(String string, String bogusString) throws Exception {
		this.verifyCorruptXML(new String[] {string, bogusString});
	}

	private void verifyCorruptXML(String[] stringPairs) throws Exception {
		File file = this.buildTestRepositoryFile();
		((DatabasePlatformRepository) this.repository.getParent()).setFile(file);
		// see comment at DPRTests.write()
		DatabasePlatformRepositoryTests.write((DatabasePlatformRepository) this.repository.getParent());

		InputStream inStream = new BufferedInputStream(new FileInputStream(file));
		int fileSize = inStream.available();
		byte[] buf = new byte[fileSize];
		inStream.read(buf);
		inStream.close();

		String rawDocument = new String(buf);
		for (int i = 0; i < stringPairs.length; ) {
//			rawDocument = rawDocument.replaceAll(stringPairs[i], stringPairs[i + 1]);
			// DOTALL allows the '.' to match CR and LF
			rawDocument = Pattern.compile(stringPairs[i], Pattern.DOTALL).matcher(rawDocument).replaceAll(stringPairs[i + 1]);
			i += 2;
		}
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(file), 2048);
		outStream.write(rawDocument.getBytes());
		outStream.close();
		boolean exCaught = false;
		try {
			DatabasePlatformRepository bogusRepository = new DatabasePlatformRepository(file);
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
		// C:/temp/JDBCTypeRepositoryTests
		return FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName());
	}

}
