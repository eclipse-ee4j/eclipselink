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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.meta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.AbstractSet;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.ClasspathTestTool;
import org.eclipse.persistence.tools.workbench.test.utility.JavaTools;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWModifier;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


public class MWClassRepositoryTests extends TestCase {
	private MWRelationalProject project;
	private MWClassRepository repository;
	String eventType;

	public static Test suite() {
		return new TestSuite(MWClassRepositoryTests.class);
	}
	
	public MWClassRepositoryTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.project = this.buildProject();
		this.repository = this.project.getRepository();
	}
	
	private MWRelationalProject buildProject() {
		return new MWRelationalProject(this.getClass().getName(), MappingsModelTestTools.buildSPIManager(), null);
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testUserTypes() {
		this.repository.addCollectionChangeListener(MWClassRepository.USER_TYPES_COLLECTION, this.buildListener());
		this.eventType = null;
		MWClass objectType = this.project.typeNamed(Object.class.getName());
		MWClass abstractSetType = this.project.typeNamed(AbstractSet.class.getName());
		assertEquals(AbstractSet.class.getName(), abstractSetType.getName());
		assertNull(this.eventType);

		this.eventType = null;
		MWClass fooType = this.project.typeNamed("com.bar.Foo");
		assertEquals("com.bar.Foo", fooType.getName());
		assertNull(this.eventType);
		fooType.setSuperclass(abstractSetType);
		assertEquals("add", this.eventType);

		this.eventType = null;
		fooType.setSuperclass(objectType);
		assertEquals("remove", this.eventType);
	}

	public void testModifierChange() {
		this.repository.addCollectionChangeListener(MWClassRepository.USER_TYPES_COLLECTION, this.buildListener());
		this.eventType = null;
		MWClass abstractSetType = this.project.typeNamed(AbstractSet.class.getName());
		assertEquals(AbstractSet.class.getName(), abstractSetType.getName());
		assertNull(this.eventType);

		this.eventType = null;
		MWClass fooType = this.project.typeNamed("com.bar.Foo");
		assertEquals("com.bar.Foo", fooType.getName());
		assertNull(this.eventType);
		fooType.getModifier().setAccessLevel(MWModifier.PROTECTED);
		assertEquals("add", this.eventType);

		this.eventType = null;
		fooType.getModifier().setAccessLevel(MWModifier.PUBLIC);
		assertEquals("remove", this.eventType);
	}

	private CollectionChangeListener buildListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				MWClassRepositoryTests.this.eventType = "add";
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				MWClassRepositoryTests.this.eventType = "remove";
			}
			public void collectionChanged(CollectionChangeEvent e) {
				throw new RuntimeException("unexpected event");
			}
		};
	}

	public void testClassLoader() throws Exception {
		File tempDir = this.buildTempDir();
		this.repository.addClasspathEntry(tempDir.getAbsolutePath());
		MWClass testType = this.fullyPopulatedTypeNamed("foo.bar.TestClass");
		assertEquals(2, testType.attributesSize());
		assertEquals(3, testType.methodsSize());
	
		MWClass intType = this.repository.typeFor(int.class);
	
		MWClassAttribute attribute = testType.attributeNamed("attribute2");
		assertEquals(intType, attribute.getType());
		assertEquals(1, attribute.getDimensionality());
	
		assertNull(testType.attributeNamed("attribute3"));
	
		MWMethod method = testType.methodWithSignature("method2(int)");
		assertEquals(intType, method.getReturnType());
		assertEquals(intType, method.getMethodParameter().getType());
	
		assertNull(testType.methodWithSignature("method3()"));
	
		this.buildClassFile(tempDir, true);
		this.repository.refreshExternalClassDescriptions();
		testType.refresh();
		assertNotNull(testType.attributeNamed("attribute3"));
		assertNotNull(testType.methodWithSignature("method3()"));
	
		this.deleteDir(tempDir);
	}
	
	private File buildTempDir() throws Exception {
		File tempDir = FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		File fooDir = new File(tempDir, "foo");
		fooDir.mkdir();
		File barDir = new File(fooDir, "bar");
		barDir.mkdir();
		this.buildClassFile(tempDir, false);
		return tempDir;
	}
	
	private void buildClassFile(File tempDir, boolean printExtraStuff) throws Exception {
		File sourceFile = new File(new File(new File(tempDir, "foo"), "bar"), "TestClass.java");
		IndentingPrintWriter pw = new IndentingPrintWriter(new FileWriter(sourceFile));
		this.printSourceOn(pw, printExtraStuff);
		pw.close();
		this.compile(sourceFile);
	}
	
	private void printSourceOn(IndentingPrintWriter pw, boolean printExtraStuff) {
		pw.println("package foo.bar;");
		pw.println("public class TestClass {");
		pw.indent();
		pw.println("private java.lang.String attribute1;");
		pw.println("private int[] attribute2;");
		if (printExtraStuff) {
			pw.println("private java.lang.Object attribute3;");
		}
		pw.println("public TestClass() {super();}");
		pw.println("public void method1() {}");
		pw.println("public int method2(int returnValue) {return returnValue;}");
		if (printExtraStuff) {
			pw.println("public void method3() {}");
		}
		pw.undent();
		pw.println("}");
	}
	
	private void compile(File sourceFile) throws IOException, InterruptedException {
		JavaTools.compile(sourceFile);
	}
	
	private void deleteDir(File dir) {
		FileTools.deleteDirectory(dir);
	}
	
	public void testCoreClassNames() throws Exception {
		assertTrue(MWClassRepository.coreClassNamesContains(java.lang.Object.class.getName()));
		assertTrue(MWClassRepository.coreClassNamesContains(java.util.List.class.getName()));
		assertTrue(MWClassRepository.coreClassNamesContains(org.eclipse.persistence.indirection.ValueHolderInterface.class.getName()));
		assertTrue(MWClassRepository.coreClassNamesContains(org.eclipse.persistence.sessions.Session.class.getName()));
	}
	
	public void testPartiallyLoadedClassNames() throws Exception {
		this.verifyPartiallyPopulatedCoreType(java.lang.Object.class);
		this.verifyPartiallyPopulatedCoreType(java.lang.String.class);

		this.verifyPartiallyPopulatedCoreType(java.util.List.class);
		assertTrue(this.project.typeFor(java.util.List.class).interfacesSize() != 0);

		this.verifyPartiallyPopulatedCoreType(org.eclipse.persistence.indirection.ValueHolderInterface.class);

		this.verifyPartiallyPopulatedCoreType(org.eclipse.persistence.indirection.ValueHolder.class);
		assertTrue(this.project.typeFor(org.eclipse.persistence.indirection.ValueHolder.class).interfacesSize() != 0);

		this.verifyPartiallyPopulatedCoreType(org.eclipse.persistence.descriptors.ClassDescriptor.class);
		assertTrue(this.project.typeFor(org.eclipse.persistence.descriptors.ClassDescriptor.class).interfacesSize() == 2);

		assertTrue(this.project.typeFor(this.getClass()).isStub());
	}

	private void verifyPartiallyPopulatedCoreType(Class javaClass) {
		MWClass type = this.project.typeFor(javaClass);
		assertFalse(type.isStub());
		assertTrue(this.typeIsPartiallyPopulatedCoreType(type));
		type.attributesSize();		// trigger complete population
		assertFalse(this.typeIsPartiallyPopulatedCoreType(type));
	}

	private boolean typeIsPartiallyPopulatedCoreType(MWClass type) {
		return ((Boolean) ClassTools.getFieldValue(type, "partiallyPopulatedCoreType")).booleanValue();
	}

	public void testChangeClasspath() throws Exception {
		ClasspathTestTool tool = new ClasspathTestTool(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		tool.setUp();

		String testClassName = ClasspathTestTool.TEST_CLASS_NAME;
		String versionMethodName1 = ClasspathTestTool.VERSION_MEMBER_PREFIX + 1;
		String versionMethodSignature1 = versionMethodName1 + "()";
		String versionMethodName2 = ClasspathTestTool.VERSION_MEMBER_PREFIX + 2;
		String versionMethodSignature2 = versionMethodName2 + "()";
	
		int index1 = this.repository.classpathEntriesSize();
		this.repository.addClasspathEntry(tool.subdir1.getAbsolutePath());
	
		// make sure the .class file was found...
		this.verifyClassNamed(testClassName);
		// ...and it is the right one
		MWClass testType = this.fullyPopulatedTypeNamed(testClassName);
		assertNotNull("method is missing: " + versionMethodSignature1,
			testType.methodWithSignature(versionMethodSignature1));
	
		// now, change the path to point to subdir2
		this.repository.removeClasspathEntry(index1);
		this.repository.addClasspathEntry(tool.subdir2.getAbsolutePath());
		testType.refresh();
		assertTrue("method should be gone: " + versionMethodSignature1,
			testType.methodWithSignature(versionMethodSignature1) == null);
		assertTrue("method is missing: " + versionMethodSignature2,
			testType.methodWithSignature(versionMethodSignature2) != null);

		tool.tearDown();
	}
	
	public void testRefreshExternalClassesDirectory() throws Exception {
		ClasspathTestTool tool = new ClasspathTestTool(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		tool.setUp();

		String testClassName = ClasspathTestTool.TEST_CLASS_NAME;
		File classFile = new File(tool.subdir1, Classpath.convertToClassFileName(testClassName));
	
		this.repository.addClasspathEntry(tool.subdir1.getAbsolutePath());
	
		this.verifyRefreshExternalClassDescriptions(testClassName, classFile);

		tool.tearDown();
	}
	
	public void testRefreshExternalClassesJar() throws Exception {
		ClasspathTestTool tool = new ClasspathTestTool(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		tool.setUp();

		this.testRefreshExternalClassesArchive(tool.jarFile1, ClasspathTestTool.TEST_CLASS_NAME);

		tool.tearDown();
	}
	
	public void testRefreshExternalClassesZip() throws Exception {
		ClasspathTestTool tool = new ClasspathTestTool(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		tool.setUp();

		this.testRefreshExternalClassesArchive(tool.zipFile1, ClasspathTestTool.TEST_CLASS_NAME);

		tool.tearDown();
	}
	
	private void testRefreshExternalClassesArchive(File archiveFile, String testClassName) {
		this.repository.addClasspathEntry(archiveFile.getAbsolutePath());
	
		this.verifyRefreshExternalClassDescriptions(testClassName, archiveFile);
	}
	
	private void verifyRefreshExternalClassDescriptions(String testClassName, File file) {
		this.verifyClassNamed(testClassName);
	
		File xFile = new File(file.getAbsolutePath() + ".x");
		file.renameTo(xFile);
		this.repository.refreshExternalClassDescriptions();
		this.verifyMissingClassNamed(testClassName);
	
		xFile.renameTo(file);
		this.repository.refreshExternalClassDescriptions();
		this.verifyClassNamed(testClassName);
	
		file.delete();
		this.repository.refreshExternalClassDescriptions();
		this.verifyMissingClassNamed(testClassName);
	}
	
	public void testRelativePath() throws Exception {
		ClasspathTestTool tool = new ClasspathTestTool(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		tool.setUp();
		this.project.setSaveDirectory(tool.workingDirectory);

		this.repository.addClasspathEntry(tool.jarFile1.getAbsolutePath().substring(tool.workingDirectory.getAbsolutePath().length() + 1));
		this.verifyClassNamed(ClasspathTestTool.TEST_CLASS_NAME);

		tool.tearDown();
	}

	// this test is not so easy to do now, so we commented it out...
	// the compiler used to generate static fields, e.g.
	//     static Class class$0;
	// to hold the class used by code generated for
	// the "com.foo.Bar.class" syntax, i.e.
	//     (class$0 != null) ? class$0 : class$0 = Class.forName("com.foo.Bar")

	// this is compiler-dependent, but seems pretty consistent across compilers...
//	public void testIsSynthetic() throws Exception {
//		this.project.getRepository().addClasspathEntry(Classpath.locationFor(this.getClass()));
//		Field[] fields = this.getClass().getDeclaredFields();
//		MWClass type = this.fullyPopulatedTypeFor(this.getClass());
//		assertTrue("compiler generated fields not found in: " + this.getClass().getName(), fields.length > type.attributesSize());
//	}
//	
	public void testCheckTypeNameNull() {
		boolean exCaught = false;
		try {
			MWClass type = this.repository.typeNamed(null);
			fail("invalid type: " + type);
		} catch (NullPointerException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testCheckTypeNameEmptyName() {
		boolean exCaught = false;
		try {
			MWClass type = this.repository.typeNamed("");
			fail("invalid type: " + type);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testCheckTypeNameArray() {
		boolean exCaught = false;
		try {
			MWClass type = this.repository.typeNamed(new Object[0].getClass().getName());
			fail("invalid type: " + type);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testCheckTypeNameCoreTypeCollision() {
		boolean exCaught = false;
		try {
			MWClass type = this.repository.typeNamed("java.lang.OBJECT");
			fail("invalid type: " + type);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testCheckTypeNameUserTypeCollision() {
		MWClass fooType = this.repository.typeNamed("xxx.Foo");
		fooType.addAttribute("attribute1");
		boolean exCaught = false;
		try {
			MWClass type = this.repository.typeNamed("XXX.Foo");
			fail("invalid type: " + type);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testTypeNamedIgnoreCaseCollision() {
		MWClass fooType = this.repository.typeNamed("xxx.Foo");
		fooType.addAttribute("attribute1");
		MWClass barType = this.repository.typeNamed("xxx.Bar");
		barType.setSuperclass(fooType);		// add a reference to Foo

		String newTypeName = "XXX.Foo";
		MWClass collision = this.repository.typeNamedIgnoreCase(newTypeName);
		assertSame(fooType, collision);

		boolean exCaught = false;
		try {
			MWClass type = this.repository.typeNamed(newTypeName);
			fail("invalid type: " + type);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testTypeNamedIgnoreCaseGarbage() {
		MWClass fooType = this.repository.typeNamed("xxx.Foo");
		fooType.addAttribute("attribute1");

		String newTypeName = "XXX.Foo";
		MWClass collision = this.repository.typeNamedIgnoreCase(newTypeName);
		// since there are no references to Foo, it should be "garbage collected"
		assertNull(collision);

		MWClass type = this.repository.typeNamed(newTypeName);
		assertNotNull(type);
	}
	
	public void testTypeRenamedNull() {
		MWClass type = this.repository.typeNamed("xxx.Foo");
		boolean exCaught = false;
		try {
			type.setName(null);
			fail("invalid type: " + type);
		} catch (NullPointerException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testTypeRenamedEmptyName() {
		MWClass type = this.repository.typeNamed("xxx.Foo");
		boolean exCaught = false;
		try {
			type.setName("");
			fail("invalid type: " + type);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testTypeRenamedArray() {
		MWClass type = this.repository.typeNamed("xxx.Foo");
		boolean exCaught = false;
		try {
			type.setName(new Object[0].getClass().getName());
			fail("invalid type: " + type);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testTypeRenamedCoreTypeCollision() {
		MWClass type = this.repository.typeNamed("xxx.Foo");
		boolean exCaught = false;
		try {
			type.setName("java.lang.OBJECT");
			fail("invalid type: " + type);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testTypeRenamedCoreType() {
		MWClass type = this.repository.typeNamed("xxx.Foo");
		boolean exCaught = false;
		try {
			type.setName(Object.class.getName());
			fail("invalid type: " + type);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testTypeRenamedUserTypeCollision() {
		MWClass fooType = this.repository.typeNamed("xxx.Foo");
		fooType.addAttribute("attribute1");
		MWClass barType = this.repository.typeNamed("xxx.Bar");
		barType.setSuperclass(fooType);
		MWClass bazType = this.repository.typeNamed("xxx.Baz");

		boolean exCaught = false;
		try {
			bazType.setName(fooType.getName());
			fail("invalid type: " + bazType);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testTypeRenamedIgnoreCaseCollision() {
		MWClass fooType = this.repository.typeNamed("xxx.Foo");
		fooType.addAttribute("attribute1");
		MWClass barType = this.repository.typeNamed("xxx.Bar");
		barType.setSuperclass(fooType);
		MWClass bazType = this.repository.typeNamed("xxx.Baz");

		boolean exCaught = false;
		try {
			bazType.setName("xxx.FOO");
			fail("invalid type: " + bazType);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testTypeRenamedIgnoreCaseGarbage() {
		MWClass fooType = this.repository.typeNamed("xxx.Foo");
		fooType.addAttribute("attribute1");
		MWClass bazType = this.repository.typeNamed("xxx.Baz");

		String newTypeName = "XXX.Foo";
		MWClass collision = this.repository.typeNamedIgnoreCase(newTypeName);
		// since there are no references to Foo, it should be "garbage collected"
		assertNull(collision);
		bazType.setName(newTypeName);
		assertEquals(newTypeName, bazType.getName());
	}
	
	public void testTypeReferences() {
		MWClass fooType = this.repository.typeNamed("xxx.Foo");
		fooType.addAttribute("attribute1");
		MWClass barType = this.repository.typeNamed("xxx.Bar");
		barType.setSuperclass(fooType);
		assertTrue(this.projectContainsAnyReferencesTo(fooType));
		assertFalse(this.projectContainsAnyReferencesTo(barType));
	}
	
	private boolean projectContainsAnyReferencesTo(Node node) {
		for (Iterator stream = this.project.branchReferences(); stream.hasNext(); ) {
			Node.Reference ref = (Node.Reference) stream.next();
			if (ref.getTarget() == node) {
				return true;
			}
		}
		return false;
	}
	
	private MWClass fullyPopulatedTypeFor(Class javaClass) throws ExternalClassNotFoundException {
		return this.fullyPopulatedTypeNamed(javaClass.getName());
	}
	
//	private void verifyClass(Class javaClass) {
//		this.verifyClassNamed(javaClass.getName());
//	}
//
	private void verifyMissingClassNamed(String className) {
		assertTrue("external class should be gone: " + className,
			! CollectionTools.contains(this.classNames(), className));
	}

	private void verifyClassNamed(String className) {
		assertTrue("missing external class: " + className,
			CollectionTools.contains(this.classNames(), className));
	}

	private Iterator classNames(Iterator externalClassDescriptions) {
		return new TransformationIterator(externalClassDescriptions) {
			protected Object transform(Object next) {
				return ((ExternalClassDescription) next).getName();
			}
		};
	}
	
	private Iterator classNames() {
		return this.classNames(this.repository.externalClassDescriptions());
	}

	private MWClass fullyPopulatedTypeNamed(String className) throws ExternalClassNotFoundException {
		MWClass result = this.project.typeNamed(className);
		result.refresh();
		return result;
	}
	
//	private void dump(Iterator stream) {
//		while (stream.hasNext()) {
//			System.out.println(stream.next().toString());
//		}
//	}
//
}
