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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.meta;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethodParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



public class MWMethodTests extends TestCase {
	private MWRelationalProject project;
	private MWClass objectType;
	private MWClass intType;
	private MWClass stringType;
	private MWClass voidType;
	private MWClass charType;
	private MWClass fooType;

	public static Test suite() {
		return new TestSuite(MWMethodTests.class);
	}
	
	public MWMethodTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.project = this.buildProject();
		this.objectType = this.fullyPopulatedTypeFor(java.lang.Object.class);
		this.intType = this.fullyPopulatedTypeFor(int.class);
		this.stringType = this.fullyPopulatedTypeFor(java.lang.String.class);
		this.voidType = this.fullyPopulatedTypeFor(void.class);
		this.charType = this.fullyPopulatedTypeFor(char.class);
	
		this.fooType = this.project.typeNamed("foo.bar.Foo");
		this.fooType.addAttribute("objectAttr", this.objectType);
		this.fooType.addAttribute("stringAttr", this.stringType);
		this.fooType.addEjb20Attribute("ejb20Attr", this.objectType);
	}
	
	private MWRelationalProject buildProject() {
		return new MWRelationalProject(this.getClass().getName(), MappingsModelTestTools.buildSPIManager(), null);
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	// this is compiler-dependent...
	//public void testMethodIsCompilerGenerated() {
	//	Method[] methods = this.getClass().getDeclaredMethods();
	//	boolean compilerGeneratedMethodFound = false;
	//	for (int i = 0; i < methods.length; i++) {
	//		if (MWMethod.methodIsCompilerGenerated(methods[i])) {
	//			compilerGeneratedMethodFound = true;
	//		}
	//	}
	//	this.assertTrue("compiler generated method not found in: " + this.getClass().getName(), compilerGeneratedMethodFound);
	//}
	//
	public void testGetters() throws Exception {
		MWMethod method;
	
		method = this.stringType.methodWithSignature("String(char[], int, int)");
		assertEquals(this.stringType, method.getDeclaringType());
		assertEquals("String", method.getName());
		assertTrue(method.getModifier().isPublic());
		assertTrue(method.isConstructor());
		assertEquals(this.charType, method.getMethodParameter(0).getType());
		assertEquals(1, method.getMethodParameter(0).getDimensionality());
		assertEquals(this.intType, method.getMethodParameter(1).getType());
		assertEquals(0, method.getMethodParameter(1).getDimensionality());
		assertEquals(this.intType, method.getMethodParameter(2).getType());
		assertEquals(0, method.getMethodParameter(2).getDimensionality());
	
		method = this.stringType.methodWithSignature("substring(int, int)");
		assertEquals(this.stringType, method.getDeclaringType());
		assertEquals("substring", method.getName());
		assertTrue(method.getModifier().isPublic());
		assertTrue( ! method.isConstructor());
		assertEquals(this.stringType, method.getReturnType());
		assertEquals(this.intType, method.getMethodParameter(0).getType());
		assertEquals(0, method.getMethodParameter(0).getDimensionality());
		assertEquals(this.intType, method.getMethodParameter(1).getType());
		assertEquals(0, method.getMethodParameter(1).getDimensionality());
	
		method = this.objectType.methodWithSignature("clone()");
		assertEquals(this.objectType, method.getDeclaringType());
		assertEquals("clone", method.getName());
		assertTrue( ! method.getModifier().isPublic());
		assertTrue(method.getModifier().isProtected());
		assertTrue( ! method.isConstructor());
		assertEquals(this.objectType, method.getReturnType());
		assertEquals(0, method.methodParametersSize());
		MWClass exType = this.fullyPopulatedTypeFor(java.lang.CloneNotSupportedException.class);
		assertTrue(CollectionTools.contains(method.exceptionTypes(), exType));
	}
	
	public void testGetReturnTypeDeclaration() {
		MWMethod method = this.objectType.methodWithSignature("Object()");
		boolean exCaught = false;
		try {
			method.getReturnType();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("IllegalStateException not thrown", exCaught);
		assertTrue("not a constructor", method.isConstructor());
	}
	
	public void testgetMethodParameter() {
		MWMethod method = this.stringType.methodWithSignature("concat(java.lang.String)");
		MWMethodParameter td = method.getMethodParameter();
		assertEquals(this.stringType, td.getType());
		assertEquals(0, td.getDimensionality());
	
		method = this.stringType.methodWithSignature("length()");
		boolean exCaught = false;
		try {
			td = method.getMethodParameter();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("IllegalStateException not thrown", exCaught);
	
		method = this.stringType.methodWithSignature("indexOf(java.lang.String, int)");
		exCaught = false;
		try {
			td = method.getMethodParameter();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("IllegalStateException not thrown", exCaught);
	}
	
	public void testAddParameterTypeDeclaration() throws Exception {
		MWMethod method = this.fooType.addMethod("barMethod", this.objectType);
		method.addMethodParameter(this.objectType);
		method.addMethodParameter(this.stringType, 1);
		assertEquals(this.fooType, method.getDeclaringType());
		assertEquals(this.objectType, method.getReturnType());
		assertEquals(this.objectType, method.getMethodParameter(0).getType());
		assertEquals(0, method.getMethodParameter(0).getDimensionality());
		assertEquals(this.stringType, method.getMethodParameter(1).getType());
		assertEquals(1, method.getMethodParameter(1).getDimensionality());
	
		MWClass exType  = this.fullyPopulatedTypeFor(java.lang.ClassNotFoundException.class);
		method.addExceptionType(exType);
		assertTrue(CollectionTools.contains(method.exceptionTypes(), exType));
	}
	
	public void testSetConstructor() throws Exception {
		MWMethod method = this.fooType.addMethod("ctor", this.objectType);
		assertEquals(this.objectType, method.getReturnType());
		method.setConstructor(true);
		method.setConstructor(false);
		assertEquals(this.voidType, method.getReturnType());
		assertTrue(method.returnTypeIsVoid());
	}
	
	public void testIsEjb20Accessor() 
	{
		this.fooType.setInterface(true);
		MWMethod getMethod = this.fooType.addMethod("getEjb20ObjectAttr", this.objectType);
		assertTrue(getMethod.isEjb20GetMethod());
		MWMethod setMethod = this.fooType.addMethod("setEjb20ObjectAttr");
		setMethod.addMethodParameter(this.objectType);
		assertTrue(ClassTools.invokeMethod(this.fooType, "ejb20SetMethodFor", MWMethod.class, getMethod) == setMethod);
	}
	
	public void testRefresh() throws Exception {
		MWMethod method;
	
		method = this.stringType.methodWithSignature("substring(int, int)");
	
		assertTrue(method.getModifier().isPublic());
		method.getModifier().setPrivate(true);
		assertTrue( ! method.getModifier().isPublic());
		assertTrue(method.getModifier().isPrivate());
	
		assertEquals(this.stringType, method.getReturnType());
		method.setReturnType(this.voidType);
		assertEquals(this.voidType, method.getReturnType());
	
		assertEquals(0, method.exceptionTypesSize());
		MWClass exType  = this.project.typeFor(java.lang.Exception.class);
		method.addExceptionType(exType);
		assertEquals(1, method.exceptionTypesSize());
	
		this.stringType.refresh();
		assertTrue(method.getModifier().isPublic());
		assertEquals(this.stringType, method.getReturnType());
		assertEquals(0, method.exceptionTypesSize());
	}
	
	public void testIsInstanceMethod() {
		MWMethod method;
		method = this.stringType.methodWithSignature("String()");
		assertTrue( ! method.isInstanceMethod());
		method = this.stringType.methodWithSignature("valueOf(int)");
		assertTrue( ! method.isInstanceMethod());
		method = this.stringType.methodWithSignature("toString()");
		assertTrue(method.isInstanceMethod());
	}
	
	public void testIsCandidateAccessorFor() {
		MWClassAttribute attribute = this.fooType.addAttribute("foo", this.stringType);
		MWMethod getMethod = this.fooType.addMethod("getFoo", this.stringType);
		assertTrue(CollectionTools.contains(attribute.candidateGetMethods(), getMethod));
		
		getMethod.getModifier().setStatic(true);
		assertFalse(CollectionTools.contains(attribute.candidateGetMethods(), getMethod));
		
		getMethod.getModifier().setStatic(false);
		getMethod.setReturnType(this.fooType);
		assertFalse(CollectionTools.contains(attribute.candidateGetMethods(), getMethod));
		
		getMethod.setReturnType(this.stringType);
		getMethod.addMethodParameter(this.stringType);
		assertFalse(CollectionTools.contains(attribute.candidateGetMethods(), getMethod));


		MWMethod setMethod = this.fooType.addMethod("setFoo");
		setMethod.addMethodParameter(this.stringType);
		assertTrue(CollectionTools.contains(attribute.candidateSetMethods(), setMethod));
		
		setMethod.getModifier().setStatic(true);
		assertFalse(CollectionTools.contains(attribute.candidateSetMethods(), setMethod));
	}
	
	// TODO add tests for isCandidateAddMethod, *RemoveMethod, *TopLinkGetMethod, etc.
	public void testIsCandidateClassExtractionMethod() throws Exception {
		MWClass classType = this.project.typeFor(java.lang.Class.class);
		MWClass dbRowType = this.project.typeFor(org.eclipse.persistence.sessions.Record.class);
		MWMethod method;
		method = this.fooType.addMethod("extractClassFrom", classType);
		method.getModifier().setPrivate(true);
		method.getModifier().setStatic(true);
		method.addMethodParameter(dbRowType);
		assertTrue(method.isCandidateClassExtractionMethod());
	}
	
	public void testIsCandidateCloneMethod() throws Exception {
		MWMethod method;
		method = this.fooType.addMethod("cloneForTopLink", this.objectType);
		method.getModifier().setPrivate(true);
		assertTrue(method.isCandidateCloneMethod());
	
		method = this.fooType.addMethod("cloneForTopLink2", this.fooType);
		method.getModifier().setPrivate(true);
		assertTrue(method.isCandidateCloneMethod());
	}
	
	public void testIsCandidateInstantiationMethod() throws Exception {
		MWMethod method;
		method = this.fooType.addMethod("ctorForTopLink", this.objectType);
		method.getModifier().setPrivate(true);
		method.getModifier().setStatic(true);
		assertTrue(method.isCandidateInstantiationMethod());
	
		method = this.fooType.addMethod("ctorForTopLink2", this.fooType);
		method.getModifier().setPrivate(true);
		method.getModifier().setStatic(true);
		assertTrue(method.isCandidateInstantiationMethod());
	}
	
	public void testCompareTo() throws Exception {
		MWMethod method1 = this.fooType.addZeroArgumentConstructor();
		MWMethod method2 = this.fooType.addMethod("Foo");
		method2.setConstructor(true);
		method2.addMethodParameter(this.stringType);
		MWMethod method3 = this.fooType.addMethod("bar");
		MWMethod method4 = this.fooType.addMethod("foo");
	
		SortedSet ss = new TreeSet();
		ss.add(method4);
		ss.add(method3);
		ss.add(method2);
		ss.add(method1);
		Iterator stream = ss.iterator();
		assertEquals(method1, stream.next());
		assertEquals(method2, stream.next());
		assertEquals(method3, stream.next());
		assertEquals(method4, stream.next());
	}
	
	public void testSignature() {
		MWMethod method = this.objectType.methodWithSignature("equals(java.lang.Object)");
		assertEquals("equals(Object)",
				method.shortSignature());
		assertEquals("equals(java.lang.Object)",
				method.signature());
	}
	
	private MWClass fullyPopulatedTypeFor(Class javaClass) throws ExternalClassNotFoundException {
		MWClass result = this.project.typeFor(javaClass);
		result.refresh();
		return result;
	}
	
}
