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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;



public abstract class ExternalClassRepositoryTests extends TestCase {
	protected ExternalClassRepositoryFactory factory;


	protected ExternalClassRepositoryTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.factory = this.buildFactory();
	}

	protected abstract ExternalClassRepositoryFactory buildFactory();

	protected abstract ExternalClassRepository systemClasspathRepository() throws Exception;

	protected abstract ExternalClassRepository buildExternalClassRepository(File[] classpath);

	protected abstract ExternalClassRepository systemRepositoryFor(ExternalClassRepository repository) throws Exception;
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

//	public void testPerformance() throws Exception {
//		ExternalClassRepository repository = this.systemClasspathRepository();
//	
//		long start, finish;
//		start = new Date().getTime();
//		repository.getExternalClassDescriptions();
//		finish = new Date().getTime();
//		long actual = finish - start;
//		// should take more than half a second; or we missed something...
//		long expected = 500;
//		assertTrue("Probably too fast - expected: " + expected + " actual: " + actual, actual > expected);
//		// should take less than 8 seconds
//		expected = 8000;
//		assertTrue("Possibly too slow - expected: " + expected + " actual: " + actual, actual < expected);
//	
//		start = new Date().getTime();
//		repository.getExternalClassDescriptions();
//		finish = new Date().getTime();
//		actual = finish - start;
//		// now it should take less than 0.005 seconds
//		expected = 50;
//		assertTrue("Possibly too slow - expected: " + expected + " actual: " + actual, actual < expected);
//	}
//
	public void testSystemClasspath() throws Exception {
		ExternalClassRepository repository = this.systemClasspathRepository();
		ExternalClassDescription[] externalClassDescriptions = repository.getClassDescriptions();

		// jdk 1.4.2 has over 9000 classes in rt.jar...	
		assertTrue("missing system entries", externalClassDescriptions.length > 5000);
	
		this.verifyClass(externalClassDescriptions, int.class);
		this.verifyClass(externalClassDescriptions, void.class);
		this.verifyClass(externalClassDescriptions, java.lang.Object.class);
		this.verifyClass(externalClassDescriptions, java.lang.Class.class);
	}

	public void testTypeIdentity() throws Exception {
		ExternalClassRepository repository = this.systemClasspathRepository();

		ExternalClassDescription type1 = this.descriptionFor(repository.getClassDescriptions(), java.lang.String.class);
		ExternalClassDescription type2 = this.descriptionFor(repository.getClassDescriptions(), java.lang.String.class);

		assertTrue("bungled type identity", type1 == type2);
	}

	public void testSystemArrayTypes() throws Exception {
		ExternalClassRepository repository = this.systemClasspathRepository();
		ExternalClassDescription[] externalClassDescriptions = repository.getClassDescriptions();
		ExternalClassRepository systemRepository = this.systemRepositoryFor(repository);

		Class arrayType = (new byte[0]).getClass();

		// array types should not be returned directly
		this.verifyInvalidType(externalClassDescriptions, arrayType);
		// the array type comes from the "system" repository
		Map arrayClassDescriptions = (Map) ClassTools.getFieldValue(systemRepository, "arrayClassDescriptions");
		assertTrue("internal attribute test", arrayClassDescriptions.isEmpty());
		arrayClassDescriptions = (Map) ClassTools.getFieldValue(repository, "arrayClassDescriptions");
		assertTrue("internal attribute test", arrayClassDescriptions.isEmpty());

		ExternalClass exClass = this.descriptionFor(externalClassDescriptions, java.lang.String.class).getExternalClass();
		ExternalMethod exMethod = SPIMetaTestTools.zeroArgumentMethodNamed(exClass, "getBytes");
		ExternalClassDescription type1 = exMethod.getReturnType();

		exMethod = SPIMetaTestTools.oneArgumentMethodNamed(exClass, "getBytes", "java.lang.String");
		ExternalClassDescription type2 = exMethod.getReturnType();

		assertTrue("bungled array type identity", type1 == type2);

		this.verifyInvalidType(externalClassDescriptions, arrayType);
		// the array type comes from the "system" repository
		arrayClassDescriptions = (Map) ClassTools.getFieldValue(systemRepository, "arrayClassDescriptions");
		assertTrue("internal attribute test", ! arrayClassDescriptions.isEmpty());
		this.verifyArrayTypesContains(arrayClassDescriptions, arrayType);
		// hacking... maybe test should be moved to subclasses...
		if (systemRepository != repository) {
			arrayClassDescriptions = (Map) ClassTools.getFieldValue(repository, "arrayClassDescriptions");
			assertTrue("internal attribute test", arrayClassDescriptions.isEmpty());
		}
	}

	public void testProjectArrayTypes() throws Exception {
		// add an array instance variable to the source code
		final String testArrayName = "testArray";
		final Class testArrayType = (new Object[0]).getClass();
		ClasspathTestTool tool = new ClasspathTestTool(ClassTools.shortClassNameForObject(this) + "." + this.getName()) {
			protected SourceExtender buildSourceExtender() {
				return new SourceExtender() {
					public void extendSourceOn(IndentingPrintWriter pw, int version) {
						pw.print("public " + testArrayType.getComponentType().getName() + "[] " + testArrayName + ";");
					}
				};
			}

		};
		tool.setUp();

		ExternalClassRepository repository = this.buildExternalClassRepository(new File[] {tool.subdir1});
		ExternalClassDescription[] externalClassDescriptions = repository.getClassDescriptions();
		ExternalClassRepository systemRepository = this.systemRepositoryFor(repository);

		// array types should not be returned directly
		this.verifyInvalidType(externalClassDescriptions, testArrayType);
		// the array comes from the "project" repository
		Map arrayClassDescriptions = (Map) ClassTools.getFieldValue(systemRepository, "arrayClassDescriptions");
		assertTrue("internal attribute test", arrayClassDescriptions.isEmpty());
		arrayClassDescriptions = (Map) ClassTools.getFieldValue(repository, "arrayClassDescriptions");
		assertTrue("internal attribute test", arrayClassDescriptions.isEmpty());

		ExternalClass exClass = this.descriptionForClassNamed(externalClassDescriptions, ClasspathTestTool.TEST_CLASS_NAME).getExternalClass();
		ExternalField exField = SPIMetaTestTools.fieldNamed(exClass, testArrayName);
		ExternalClassDescription type1 = exField.getType();

		exField = SPIMetaTestTools.fieldNamed(exClass, testArrayName);
		ExternalClassDescription type2 = exField.getType();

		assertTrue("bungled array type identity", type1 == type2);

		this.verifyInvalidType(externalClassDescriptions, testArrayType);
		// hacking... maybe test should be moved to subclasses...
		if (systemRepository != repository) {
			// the array comes from the "project" repository
			arrayClassDescriptions = (Map) ClassTools.getFieldValue(systemRepository, "arrayClassDescriptions");
			assertTrue("internal attribute test", arrayClassDescriptions.isEmpty());
		}
		arrayClassDescriptions = (Map) ClassTools.getFieldValue(repository, "arrayClassDescriptions");
		assertTrue("internal attribute test", ! arrayClassDescriptions.isEmpty());
		this.verifyArrayTypesContains(arrayClassDescriptions, testArrayType);

		tool.tearDown();
	}

	public void testProjectClasspathOrder() throws Exception {
		ClasspathTestTool tool = new ClasspathTestTool(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		tool.setUp();

		ExternalClassRepository repository = this.buildExternalClassRepository(new File[] {tool.subdir1, tool.subdir2});

		ExternalClass exClass = repository.getClassDescription(ClasspathTestTool.TEST_CLASS_NAME).getExternalClass();
		ExternalField exField = SPIMetaTestTools.fieldNamed(exClass, ClasspathTestTool.VERSION_MEMBER_PREFIX + "1");
		assertNotNull("wrong class loaded", exField);

		// now, swap the subdirs
		repository = this.buildExternalClassRepository(new File[] {tool.subdir2, tool.subdir1});

		exClass = repository.getClassDescription(ClasspathTestTool.TEST_CLASS_NAME).getExternalClass();
		exField = SPIMetaTestTools.fieldNamed(exClass, ClasspathTestTool.VERSION_MEMBER_PREFIX + "2");
		assertNotNull("wrong class loaded", exField);

		tool.tearDown();
	}

	public void testProjectClasspath() throws Exception {
		ClasspathTestTool tool = new ClasspathTestTool(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		tool.setUp();

		this.verifyProjectClasspathEntry(tool.subdir1, ClasspathTestTool.TEST_CLASS_NAME);
		this.verifyProjectClasspathEntry(tool.subdir2, ClasspathTestTool.TEST_CLASS_NAME);
		this.verifyProjectClasspathEntry(tool.jarFile1, ClasspathTestTool.TEST_CLASS_NAME);
		this.verifyProjectClasspathEntry(tool.jarFile1, ClasspathTestTool.TEST_CLASS_NAME);
		this.verifyProjectClasspathEntry(tool.zipFile1, ClasspathTestTool.TEST_CLASS_NAME);
		this.verifyProjectClasspathEntry(tool.zipFile1, ClasspathTestTool.TEST_CLASS_NAME);

		tool.tearDown();
	}

	protected abstract void verifyArrayTypesContains(Map arrayClassDescriptions, Class arrayType);

	private void verifyInvalidType(ExternalClassDescription[] externalClassDescriptions, Class invalidJavaClass) {
		boolean exCaught = false;
		try {
			this.descriptionFor(externalClassDescriptions, invalidJavaClass);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue("IllegalArgumentException not thrown", exCaught);
	}

	private void verifyProjectClasspathEntry(File classpathEntry, String className) {
		ExternalClassRepository repository = this.buildExternalClassRepository(new File[] {classpathEntry});
		ExternalClassDescription[] externalClassDescriptions = repository.getClassDescriptions();
		// first verify that the system entries are also OK
		this.verifyClass(externalClassDescriptions, java.lang.Object.class);
		// then check the project entry
		this.verifyClassNamed(externalClassDescriptions, className);
	}

	private void verifyClass(ExternalClassDescription[] externalClassDescriptions, Class javaClass) {
		this.verifyClassNamed(externalClassDescriptions, javaClass.getName());
	}

	private void verifyClassNamed(ExternalClassDescription[] externalClassDescriptions, String className) {
		assertTrue("missing external class: " + className,
			CollectionTools.contains(this.classNames(externalClassDescriptions), className));
	}

	private Iterator classNames(Iterator externalClassDescriptions) {
		return new TransformationIterator(externalClassDescriptions) {
			protected Object transform(Object next) {
				return ((ExternalClassDescription) next).getName();
			}
		};
	}
	
	private Iterator classNames(ExternalClassDescription[] externalClassDescriptions) {
		return this.classNames(CollectionTools.iterator(externalClassDescriptions));
	}

	protected ExternalClassDescription descriptionFor(ExternalClassDescription[] externalClassDescriptions, Class javaClass) {
		return this.descriptionForClassNamed(externalClassDescriptions, javaClass.getName());
	}

	protected ExternalClassDescription descriptionForClassNamed(ExternalClassDescription[] externalClassDescriptions, String className) {
		for (int i = externalClassDescriptions.length; i-- > 0; ) {
			if (externalClassDescriptions[i].getName().equals(className)) {
				return externalClassDescriptions[i];
			}
		}
		throw new IllegalArgumentException(className);
	}

}
