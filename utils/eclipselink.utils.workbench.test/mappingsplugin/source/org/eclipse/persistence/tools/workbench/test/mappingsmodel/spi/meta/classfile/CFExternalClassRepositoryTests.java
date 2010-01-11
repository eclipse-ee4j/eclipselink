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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classfile;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile.CFExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.ExternalClassRepositoryTests;
import org.eclipse.persistence.tools.workbench.test.utility.JavaTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


public class CFExternalClassRepositoryTests
	extends ExternalClassRepositoryTests
{

	public static Test suite() {
		return new TestSuite(CFExternalClassRepositoryTests.class);
	}

	public CFExternalClassRepositoryTests(String name) {
		super(name);
	}

	protected ExternalClassRepositoryFactory buildFactory() {
		return CFExternalClassRepositoryFactory.instance();
	}

	protected ExternalClassRepository systemClasspathRepository() {
		return this.factory.buildClassRepository(this.buildSystemClasspath());
	}

	protected ExternalClassRepository systemRepositoryFor(ExternalClassRepository repository) {
		return repository;
	}

	protected void verifyArrayTypesContains(Map arrayTypes, Class arrayType) {
		assertTrue(arrayTypes.containsKey(arrayType.getName()));
	}

	protected ExternalClassRepository buildExternalClassRepository(File[] classpath) {
		File[] systemClasspath = AllModelSPIMetaClassFileTests.buildMinimumSystemClasspath();
		File[] combinedClasspath = new File[systemClasspath.length + classpath.length];
		System.arraycopy(systemClasspath, 0, combinedClasspath, 0, systemClasspath.length);
		System.arraycopy(classpath, 0, combinedClasspath, systemClasspath.length, classpath.length);
		return this.factory.buildClassRepository(combinedClasspath);
	}

	private File[] buildSystemClasspath() {
		List systemClasspath = new ArrayList();
		Classpath cp = Classpath.completeClasspath();
		Classpath.Entry[] entries = cp.getEntries();
		int len = entries.length;
		for (int i = 0; i < len; i++) {
			systemClasspath.add(entries[i].file());
		}
		systemClasspath.add(new File(Classpath.locationFor(this.getClass())));
		return (File[]) systemClasspath.toArray(new File[systemClasspath.size()]);
	}

	public void test4309188() throws Exception {
		File tempDir = this.buildTempDir();

		// first make sure we can load the classes...
		ExternalClassRepository repos = this.buildExternalClassRepository(new File[] {tempDir});

		ExternalClassDescription exClassDescription1 = repos.getClassDescription("foo.bar.TestClass1");
		ExternalClass exClass1 = exClassDescription1.getExternalClass();
		assertEquals("foo.bar.TestClass1", exClass1.getName());

		ExternalClassDescription exClassDescription2 = repos.getClassDescription("foo.bar.TestClass2");
		ExternalClass exClass2 = exClassDescription2.getExternalClass();
		assertEquals("foo.bar.TestClass2", exClass2.getName());

		// ...then delete TestClass1
		File classFile1 = new File(new File(new File(tempDir, "foo"), "bar"), "TestClass1.class");
		assertTrue(classFile1.delete());
		repos = this.buildExternalClassRepository(new File[] {tempDir});

		exClassDescription2 = repos.getClassDescription("foo.bar.TestClass2");
		exClass2 = exClassDescription2.getExternalClass();
		ExternalField[] fields2 = exClass2.getDeclaredFields();
		assertEquals(1, fields2.length);
		assertEquals("testClass1", fields2[0].getName());
		// this will trigger the creation of "stub" TestClass1, since TestClass1 is not on the classpath any more
		assertEquals("foo.bar.TestClass1", fields2[0].getType().getName());
		exClassDescription1 = repos.getClassDescription("foo.bar.TestClass1");
		assertNull(exClassDescription1);
		// this next line is what used to throw an IllegalStateException in CFExternalClassDescription#buildExternalClass()
		// exClass1 = exClassDescription1.getExternalClass();

		this.deleteDir(tempDir);
	}

	private File buildTempDir() throws Exception {
		File tempDir = FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName());
		File fooDir = new File(tempDir, "foo");
		fooDir.mkdir();
		File barDir = new File(fooDir, "bar");
		barDir.mkdir();
		this.buildClassFile1(tempDir);
		this.buildClassFile2(tempDir);
		return tempDir;
	}
	
	private void buildClassFile1(File tempDir) throws Exception {
		File sourceFile = new File(new File(new File(tempDir, "foo"), "bar"), "TestClass1.java");
		IndentingPrintWriter pw = new IndentingPrintWriter(new FileWriter(sourceFile));
		this.printSource1On(pw);
		pw.close();
		JavaTools.compile(sourceFile, tempDir.getAbsolutePath());
	}
	
	private void printSource1On(IndentingPrintWriter pw) {
		pw.println("package foo.bar;");
		pw.println("public class TestClass1 {");
		pw.indent();
			pw.println("public java.lang.String string;");
			pw.println("public TestClass1() {super();}");
		pw.undent();
		pw.println("}");
	}
	
	private void buildClassFile2(File tempDir) throws Exception {
		File sourceFile = new File(new File(new File(tempDir, "foo"), "bar"), "TestClass2.java");
		IndentingPrintWriter pw = new IndentingPrintWriter(new FileWriter(sourceFile));
		this.printSource2On(pw);
		pw.close();
		JavaTools.compile(sourceFile, tempDir.getAbsolutePath());
	}
	
	private void printSource2On(IndentingPrintWriter pw) {
		pw.println("package foo.bar;");
		pw.println("public class TestClass2 {");
		pw.indent();
			pw.println("public foo.bar.TestClass1 testClass1;");
			pw.println("public TestClass2() {super();}");
		pw.undent();
		pw.println("}");
	}
	
	private void deleteDir(File dir) {
		FileTools.deleteDirectory(dir);
	}
	
}
