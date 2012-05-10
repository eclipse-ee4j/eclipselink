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
import java.util.Iterator;
import java.util.SortedSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.platformsmodel.CorruptXMLException;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeToDatabaseTypeMapping;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeToJavaTypeDeclarationMapping;
import org.eclipse.persistence.tools.workbench.platformsmodel.JavaTypeDeclaration;
import org.eclipse.persistence.tools.workbench.platformsmodel.JavaTypeDeclarationToJDBCTypeMapping;
import org.eclipse.persistence.tools.workbench.test.platformsplugin.TestDatabasePlatformRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;


public class DatabasePlatformRepositoryTests extends TestCase {
	private DatabasePlatformRepository repository;

	public static Test suite() {
		return new TestSuite(DatabasePlatformRepositoryTests.class);
	}

	public DatabasePlatformRepositoryTests(String name) {
		super(name);
	}

	/**
	 * Hack-o-rama:
	 * Some I/O-related tests (e.g. DatabasePlatformTests#testCorruptXMLMissingRoot())
	 * fail about 50% of the time because DatabasePlatformRepository#writePlatforms()
	 * will fail trying to create the 'platforms' subdirectory. (Other tests also fail for the
	 * same reason - DatabasePlatformTests#testCorruptXMLMissingName() in particular -
	 * but the "missing root" test seems to fail the most. This might be related to the fact
	 * that the "missing root" test is the first I/O-related test in the DatabasePlatformTests
	 * suite. This is true across the suites: The first I/O-related test will fail the most often,
	 * at least when executing the suites individually, as opposed to under the AllTests
	 * umbrella suite....)
	 * 
	 * I can't, for the life of me, figure out what is causing the problem. It appears to be
	 * a timing problem where the JVM and the O/S are slightly out of synch.
	 * Anyway, waiting a second and trying again seems to fix the problem. Also, the
	 * state of the repository should be OK to repeat the write, since creating the
	 * 'platforms' directory is the first thing done in the write.
	 *     ~bjv
	 */
	static void write(DatabasePlatformRepository repository) throws Exception {
		try {
			repository.write();
		} catch (RuntimeException ex) {
			if (ex.getMessage().startsWith("unable to create platforms directory: ")) {
				Thread.sleep(1000);
				// System.out.println("write problem: " + this.getName());
				repository.write();
			}
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.repository = TestDatabasePlatformRepositoryFactory.instance().createRepository();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testName() {
		assertEquals("Test Repository", this.repository.getName());
		boolean exCaught = false;
		try {
			this.repository.setName(null);
		} catch (NullPointerException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
		assertEquals("Test Repository", this.repository.getName());
	}

	public void testFile() throws Exception {
		assertNull(this.repository.getFile());
		boolean exCaught = false;
		try {
			this.repository.setFile(null);
		} catch (NullPointerException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
		assertNull(this.repository.getFile());

		// now set the file and write out the repository
		File file = this.buildTestRepositoryFile();
		this.repository.setFile(file);
		DatabasePlatformRepositoryTests.write(this.repository);
		assertTrue(this.repository.isCleanBranch());
		assertTrue(file.exists());

		// now change the name of the file but not its location
		this.repository.setFile(this.buildAlternateTestRepositoryFile());
		assertTrue(this.repository.isDirty());
		for (Iterator stream = this.repository.platforms(); stream.hasNext(); ) {
			DatabasePlatform platform = (DatabasePlatform) stream.next();
			assertTrue(platform.isCleanBranch());
		}
		DatabasePlatformRepositoryTests.write(this.repository);
		assertTrue(this.repository.isCleanBranch());
		assertFalse(file.exists());

		// now change the location of the file but not its name
		this.repository.setFile(this.buildAlternateTestRepositoryLocationFile());
		assertTrue(this.repository.isDirty());
		for (Iterator stream = this.repository.platforms(); stream.hasNext(); ) {
			DatabasePlatform platform = (DatabasePlatform) stream.next();
			assertTrue(platform.isDirty());
		}
		DatabasePlatformRepositoryTests.write(this.repository);
		assertTrue(this.repository.isCleanBranch());
	}

	private File buildAlternateTestRepositoryLocationFile() throws Exception {
		return new File(this.buildAlternateTestRepositoryDirectory(), "alternate test repos.dpr");
	}

	private File buildAlternateTestRepositoryFile() throws Exception {
		return new File(this.buildTestRepositoryDirectory(), "alternate test repos.dpr");
	}

	private File buildTestRepositoryFile() throws Exception {
		return new File(this.buildTestRepositoryDirectory(), "test repos.dpr");
	}

	private File buildTestRepositoryDirectory() throws Exception {
		// C:/temp/DatabasePlatformRepositoryTests
		return FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName());
	}

	private File buildAlternateTestRepositoryDirectory() throws Exception {
		// C:/temp/alternate DatabasePlatformRepositoryTests
		return FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName() + "." + "alternate");
	}

	public void testPlatforms() throws Exception {
		assertEquals(3, this.repository.platformsSize());
		Iterator stream = this.repository.platforms();
		stream.next();
		stream.remove();
		assertEquals(2, this.repository.platformsSize());
	}

	public void testAddPlatform() throws Exception {
		boolean exCaught = false;
		try {
			this.repository.addPlatform("Foo Platform", "xxx");
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("Foo Platform") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			this.repository.addPlatform("Joo Platform", "fooplatform.xml");
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("fooplatform.xml") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	public void testDefaultPlatform() throws Exception {
		DatabasePlatform platform = this.repository.platformNamed("Foo Platform");
		assertEquals(platform, this.repository.getDefaultPlatform());
		this.repository.removePlatform(platform);
		assertFalse(this.repository.getDefaultPlatform().equals(platform));
		for (Iterator stream = this.repository.platforms(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
		assertNull(this.repository.getDefaultPlatform());
	}

	public void testRuntimePlatform() throws Exception {
		DatabasePlatform platform = this.repository.platformNamed("Foo Platform");
		assertEquals(platform, this.repository.platformForRuntimePlatformClassNamed("com.foo.FooPlatform"));
	}

	public void testProblems() throws Exception {
		for (Iterator stream = this.repository.platforms(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
		this.repository.validateBranch();
		assertEquals(1, this.repository.problemsSize());
	}

	public void testClonePlatform() throws Exception {
		DatabasePlatform original = this.repository.platformNamed("Foo Platform");
		DatabasePlatform clone = this.repository.clone(original);

		assertNotSame(original, clone);

		assertFalse(original.getName().equals(clone.getName()));
		assertEquals("Foo Platform2", clone.getName());

		assertFalse(original.getShortFileName().equals(clone.getShortFileName()));
		assertEquals("fooplatform2.xml", clone.getShortFileName());

		assertEquals(original.getRuntimePlatformClassName(), clone.getRuntimePlatformClassName());
		assertEquals(original.supportsNativeSequencing(), clone.supportsNativeSequencing());
		assertEquals(original.supportsIdentityClause(), clone.supportsIdentityClause());

		assertEquals(original.databaseTypesSize(), clone.databaseTypesSize());
		assertEquals(this.typeNamesFor(original.databaseTypes()), this.typeNamesFor(clone.databaseTypes()));
		for (Iterator stream = original.databaseTypes(); stream.hasNext(); ) {
			DatabaseType originalType = (DatabaseType) stream.next();
			DatabaseType cloneType = clone.databaseTypeNamed(originalType.getName());
			assertNotSame(originalType, cloneType);
			this.verifyType(originalType, cloneType);
		}

		assertEquals(original.jdbcTypeToDatabaseTypeMappingsSize(), clone.jdbcTypeToDatabaseTypeMappingsSize());
		for (Iterator stream = original.jdbcTypeToDatabaseTypeMappings(); stream.hasNext(); ) {
			JDBCTypeToDatabaseTypeMapping mapping = (JDBCTypeToDatabaseTypeMapping) stream.next();
			DatabaseType originalDatabaseType = mapping.getDatabaseType();
			DatabaseType cloneDatabaseType = clone.databaseTypeForJDBCTypeCode(mapping.getJDBCType().getCode());
			assertNotSame(originalDatabaseType, cloneDatabaseType);
			assertEquals(originalDatabaseType.getName(), cloneDatabaseType.getName());
		}
	}

	private SortedSet typeNamesFor(Iterator databaseTypes) {
		return CollectionTools.sortedSet(this.names(databaseTypes));
	}

	private Iterator names(Iterator databaseTypes) {
		return new TransformationIterator(databaseTypes) {
			protected Object transform(Object next) {
				return ((DatabaseType) next).getName();
			}
		};
	}

	private void verifyType(DatabaseType originalType, DatabaseType cloneType) {
		assertEquals(originalType.getName(), cloneType.getName());
		assertEquals(originalType.allowsSize(), cloneType.allowsSize());
		assertEquals(originalType.requiresSize(), cloneType.requiresSize());
		assertEquals(originalType.getInitialSize(), cloneType.getInitialSize());
		assertEquals(originalType.allowsSubSize(), cloneType.allowsSubSize());
		assertEquals(originalType.allowsNull(), cloneType.allowsNull());
		assertSame(originalType.getJDBCType(), cloneType.getJDBCType());
	}

	public void testIO() throws Exception {
		DatabasePlatformRepository repos1 = DatabasePlatformRepository.getDefault();
		File file = this.buildTestRepositoryFile();
		repos1.setFile(file);
		repos1.write();
		DatabasePlatformRepository repos2 = new DatabasePlatformRepository(file);
		DiffEngine diffEngine = this.buildDiffEngine();
//		diffEngine.setLog("C:/temp/diff.log");
		Diff diff = diffEngine.diff(repos1, repos2);
		assertTrue(diff.getDescription(), diff.identical());
	}

	private DiffEngine buildDiffEngine() {
		DiffEngine diffEngine = new DiffEngine();

		ReflectiveDifferentiator rd;

		rd = diffEngine.addReflectiveDifferentiator(AbstractModel.class, "changeSupport");
		rd = diffEngine.addReflectiveDifferentiator(AbstractNodeModel.class);
			rd.ignoreFieldsNamed(new String[] {"branchProblems", "dirty", "dirtyBranch", "problems"});
			rd.addReferenceFieldNamed("parent");

		rd = diffEngine.addReflectiveDifferentiator(this.classForName("org.eclipse.persistence.tools.workbench.platformsmodel.AbstractJDBCTypeToJavaTypeDeclarationMapping"));
			rd.addKeyFieldsNamed("jdbcType", "javaTypeDeclaration");
			rd.addReferenceFieldNamed("jdbcType");
		rd = diffEngine.addReflectiveDifferentiator(JavaTypeDeclarationToJDBCTypeMapping.class);
		rd = diffEngine.addReflectiveDifferentiator(JDBCTypeToJavaTypeDeclarationMapping.class);
		rd = diffEngine.addReflectiveDifferentiator(DatabasePlatform.class);
			rd.addKeyFieldNamed("name");
			rd.addCollectionFieldsNamed("databaseTypes", "jdbcTypeToDatabaseTypeMappings");
		rd = diffEngine.addReflectiveDifferentiator(DatabasePlatformRepository.class);
			rd.addKeyFieldNamed("name");
			rd.ignoreFieldsNamed("file", "originalFile", "originalPlatformShortFileNames");
			rd.addReferenceFieldNamed("defaultPlatform");
			rd.addCollectionFieldNamed("platforms");
		rd = diffEngine.addReflectiveDifferentiator(DatabaseType.class);
			rd.addKeyFieldNamed("name");
			rd.addReferenceFieldNamed("jdbcType");
		rd = diffEngine.addReflectiveDifferentiator(JavaTypeDeclaration.class);
			rd.addKeyFieldsNamed("javaClassName", "arrayDepth");
		rd = diffEngine.addReflectiveDifferentiator(JDBCType.class);
			rd.addKeyFieldNamed("name");
		rd = diffEngine.addReflectiveDifferentiator(JDBCTypeRepository.class);
			rd.addReferenceFieldNamed("defaultJDBCType");
			rd.addCollectionFieldsNamed("javaTypeDeclarationToJDBCTypeMappings", "jdbcTypes", "jdbcTypeToJavaTypeDeclarationMappings");
		rd = diffEngine.addReflectiveDifferentiator(JDBCTypeToDatabaseTypeMapping.class);
			rd.addKeyFieldNamed("jdbcType");
			rd.addReferenceFieldsNamed("jdbcType", "databaseType");

		return diffEngine;
	}

	private Class classForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void testNewPlatformWrite() throws Exception {
		File reposFile = this.buildTestRepositoryFile();
		this.repository.setFile(reposFile);
		DatabasePlatformRepositoryTests.write(this.repository);
		assertTrue(reposFile.exists());

		reposFile.delete();
		assertFalse(reposFile.exists());

		DatabasePlatform fooPlatform = this.repository.platformNamed("Foo Platform");
		File fooFile = new File(this.platformsDirectory(this.repository), fooPlatform.getShortFileName());
		fooFile.delete();
		assertFalse(fooFile.exists());

		DatabasePlatform clonePlatform = this.repository.clone(fooPlatform);

		// this should write the clone's file but not the repository's or the original's files
		DatabasePlatformRepositoryTests.write(this.repository);
		assertFalse(reposFile.exists());
		assertFalse(fooFile.exists());
		File cloneFile = new File(this.platformsDirectory(this.repository), clonePlatform.getShortFileName());
		assertTrue(cloneFile.exists());

		// this should cause the repository's file to be written
		this.repository.setDefaultPlatform(clonePlatform);
		DatabasePlatformRepositoryTests.write(this.repository);
		assertTrue(reposFile.exists());
	}

	public void testPlatformOnlyWrite() throws Exception {
		File reposFile = this.buildTestRepositoryFile();
		this.repository.setFile(reposFile);
		DatabasePlatformRepositoryTests.write(this.repository);
		assertTrue(reposFile.exists());

		reposFile.delete();
		assertFalse(reposFile.exists());

		DatabasePlatform fooPlatform = this.repository.platformNamed("Foo Platform");
		File fooFile = new File(this.platformsDirectory(this.repository), fooPlatform.getShortFileName());
		fooFile.delete();
		assertFalse(fooFile.exists());

		DatabasePlatform barPlatform = this.repository.platformNamed("Bar Platform");
		File barFile = new File(this.platformsDirectory(this.repository), barPlatform.getShortFileName());
		barFile.delete();
		assertFalse(barFile.exists());

		DatabasePlatform bazPlatform = this.repository.platformNamed("Baz Platform");
		File bazFile = new File(this.platformsDirectory(this.repository), bazPlatform.getShortFileName());
		bazFile.delete();
		assertFalse(bazFile.exists());

		// ONLY the repository file should be written out
		this.repository.setDefaultPlatform(barPlatform);
		DatabasePlatformRepositoryTests.write(this.repository);
		assertTrue(reposFile.exists());
		assertFalse(fooFile.exists());
		assertFalse(barFile.exists());
		assertFalse(bazFile.exists());
	}

	public void testPlatformFileDelete() throws Exception {
		File reposFile = this.buildTestRepositoryFile();
		this.repository.setFile(reposFile);
		DatabasePlatformRepositoryTests.write(this.repository);

		DatabasePlatform fooPlatform = this.repository.platformNamed("Foo Platform");
		File fooFile = new File(this.platformsDirectory(this.repository), fooPlatform.getShortFileName());

		DatabasePlatform barPlatform = this.repository.platformNamed("Bar Platform");
		File barFile = new File(this.platformsDirectory(this.repository), barPlatform.getShortFileName());

		DatabasePlatform bazPlatform = this.repository.platformNamed("Baz Platform");
		File bazFile = new File(this.platformsDirectory(this.repository), bazPlatform.getShortFileName());

		assertTrue(reposFile.exists());
		assertTrue(fooFile.exists());
		assertTrue(barFile.exists());
		assertTrue(bazFile.exists());

		// the baz file should be deleted
		this.repository.removePlatform(barPlatform);
		DatabasePlatformRepositoryTests.write(this.repository);
		assertTrue(reposFile.exists());
		assertTrue(fooFile.exists());
		assertFalse(barFile.exists());
		assertTrue(bazFile.exists());
	}

	public void testCorruptXMLMissingRoot() throws Exception {
		this.verifyCorruptXML(new String[] {"<platforms>", "<bogus>", "</platforms>", "</bogus>"});
	}

	public void testCorruptXMLMissingName() throws Exception {
		this.verifyCorruptXML("<name>Test Repository</name>", "<repos-name>Test Repository</repos-name>");
	}

	public void testCorruptXMLMissingDefaultPlatform() throws Exception {
		this.verifyCorruptXML("<default-platform>Foo Platform</default-platform>", "<default-xxx>Foo Platform</default-xxx>");
	}

	public void testCorruptXMLInvalidDefaultPlatform() throws Exception {
		this.verifyCorruptXML("<default-platform>Foo Platform</default-platform>", "<default-platform>XXX Platform</default-platform>");
	}

	private String platformsDirectoryName() {
		return TestDatabasePlatformRepositoryFactory.platformsDirectoryName();
	}

	private File platformsDirectory(DatabasePlatformRepository repos) {
		return new File(repos.getFile().getParentFile(), this.platformsDirectoryName());
	}

	public void testCorruptXMLDefaultPlatformButNoPlatforms() throws Exception {
		File file = this.buildTestRepositoryFile();
		this.repository.setFile(file);
		DatabasePlatformRepositoryTests.write(this.repository);
		File dir = this.platformsDirectory(this.repository);
		FileTools.deleteDirectoryContents(dir);

		boolean exCaught = false;
		try {
			DatabasePlatformRepository bogusRepository = new DatabasePlatformRepository(file);
			assertNull(bogusRepository);
		} catch (CorruptXMLException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testCorruptXMLDuplicatePlatform() throws Exception {
		File file = this.buildTestRepositoryFile();
		this.repository.setFile(file);
		DatabasePlatformRepositoryTests.write(this.repository);
		File dir = this.platformsDirectory(this.repository);
		FileTools.copyToFile(new File(dir, "fooplatform.xml"), new File(dir, "xxxplatform.xml"));

		boolean exCaught = false;
		try {
			DatabasePlatformRepository bogusRepository = new DatabasePlatformRepository(file);
			assertNull(bogusRepository);
		} catch (CorruptXMLException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	private void verifyCorruptXML(String string, String bogusString) throws Exception {
		this.verifyCorruptXML(new String[] {string, bogusString});
	}

	private void verifyCorruptXML(String[] stringPairs) throws Exception {
		File file = this.buildTestRepositoryFile();
		this.repository.setFile(file);
		DatabasePlatformRepositoryTests.write(this.repository);

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

}
