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
package org.eclipse.persistence.tools.workbench.test.utility.classfile;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.ClassFile;



public class ClassDependencyTests extends TestCase {
	private ClassFile classFile;

	public static Test suite() {
		return new TestSuite(ClassDependencyTests.class);
	}

	public ClassDependencyTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.classFile = ClassFile.forClass(ClassDependencyTestClass.class);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSuperclass() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.util.Vector.class);
	}

	public void testInterface() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.lang.Comparable.class);
	}

	public void testFields() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.sql.Connection.class);
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.util.jar.JarFile.class);
	}

	public void testStaticInitialization() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.util.Date.class);
	}

	public void testInstanceInitialization() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.sql.Timestamp.class);
	}

	public void testMethodReturnType() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.util.SortedSet.class);
	}

	public void testMethodParameterType() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.util.Set.class);
	}

	public void testMethodException() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.rmi.AccessException.class);
	}

	public void testCodeCaughtException() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.io.FileNotFoundException.class);
	}

	public void testCodeLocalVariableType() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), javax.naming.Name.class);
	}

//	see comment at DependencyTestClass#testClass(Object)
//	public void testCodeClassName() throws Exception {
//		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.util.ArrayList.class);
//	}
//
	public void testCodeClassCast() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.io.Reader.class);
	}

	public void testCodeObjectArray() throws Exception {
		// test bug fix: we were adding the name "[Ljava.lang.Object;" to the list
		this.verifyClassNamesDoesNotContain(this.classFile.referencedClassNames(), (new java.lang.Object[0]).getClass());
	}

	public void testCodeColorArray() throws Exception {
		this.verifyClassNamesDoesNotContain(this.classFile.referencedClassNames(), (new java.awt.Color[0]).getClass());
	}

	public void testCodeIntArray() throws Exception {
		this.verifyClassNamesDoesNotContain(this.classFile.referencedClassNames(), (new int[0]).getClass());
	}

	public void testCodeCascadingMessages() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.util.Iterator.class);
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.util.List.class);
	}

	public void testCodeFieldAccess() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.io.PrintStream.class);
		this.verifyClassNamesDoesNotContain(this.classFile.referencedClassNames(), (new java.awt.Button[0]).getClass());
	}

	public void testCodeCtor() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.math.BigDecimal.class);
	}

	public void testCodeThrownException() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.lang.IllegalArgumentException.class);
	}

	public void testCodeInstanceof() throws Exception {
		this.verifyClassNamesContains(this.classFile.referencedClassNames(), java.math.BigInteger.class);
	}


	private void verifyClassNamesContains(String[] classNames, Class javaClass) {
		if ( ! CollectionTools.contains(classNames, javaClass.getName())) {
			fail("The class \"" + javaClass.getName()  + "\" is not in the array: " + Arrays.asList(classNames));
		}
	}

	private void verifyClassNamesDoesNotContain(String[] classNames, Class javaClass) {
		if (CollectionTools.contains(classNames, javaClass.getName())) {
			fail("The class \"" + javaClass.getName()  + "\" is in the array: " + Arrays.asList(classNames));
		}
	}

}
