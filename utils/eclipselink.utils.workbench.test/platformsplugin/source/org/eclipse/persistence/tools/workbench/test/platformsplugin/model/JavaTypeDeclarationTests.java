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
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.JavaTypeDeclaration;
import org.eclipse.persistence.tools.workbench.platformsmodel.JavaTypeDeclarationToJDBCTypeMapping;
import org.eclipse.persistence.tools.workbench.test.platformsplugin.TestDatabasePlatformRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class JavaTypeDeclarationTests extends TestCase {
	private JDBCTypeRepository repository;
	private JDBCType jdbcType;

	public static Test suite() {
		return new TestSuite(JavaTypeDeclarationTests.class);
	}
	
	public JavaTypeDeclarationTests(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		this.repository = TestDatabasePlatformRepositoryFactory.instance().createRepository().getJDBCTypeRepository();
		this.jdbcType = this.repository.jdbcTypeForCode(Types.VARCHAR);
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testState() {
		JavaTypeDeclarationToJDBCTypeMapping mapping = this.repository.addJavaTypeDeclarationToJDBCTypeMapping(java.lang.Object.class.getName(), 0, this.jdbcType);
		JavaTypeDeclaration jtd = mapping.getJavaTypeDeclaration();
		assertEquals("java.lang.Object", jtd.getJavaClassName());
		assertEquals(0, jtd.getArrayDepth());

		mapping = this.repository.addJavaTypeDeclarationToJDBCTypeMapping(int.class.getName(), 3, this.jdbcType);
		jtd = mapping.getJavaTypeDeclaration();
		assertEquals("int", jtd.getJavaClassName());
		assertEquals(3, jtd.getArrayDepth());
	}
	
	public void testVoid() {
		JavaTypeDeclaration jtd = null;
		boolean exCaught = false;
		try {
			JavaTypeDeclarationToJDBCTypeMapping mapping = this.repository.addJavaTypeDeclarationToJDBCTypeMapping(void.class.getName(), 2, this.jdbcType);
			jtd = mapping.getJavaTypeDeclaration();
		} catch (IllegalStateException ex) {
			if (ex.getMessage().indexOf("void") != -1) {
				exCaught = true;
			}
		}
		assertTrue("bogus: " + jtd, exCaught);
	}

	public void testCorruptXMLMissingName() throws Exception {
		this.verifyCorruptXML("<java-class-name>java.lang.Object</java-class-name>", "<bogus-name>java.lang.Object</bogus-name>");
	}

	public void testCorruptXMLInvalidArrayDepth() throws Exception {
		this.verifyCorruptXML("<array-depth>1</array-depth>", "<array-depth>-1</array-depth>");
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
			rawDocument = rawDocument.replaceAll(stringPairs[i], stringPairs[i + 1]);
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
		// C:/temp/JavaTypeDeclarationTests
		return FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName());
	}

}
