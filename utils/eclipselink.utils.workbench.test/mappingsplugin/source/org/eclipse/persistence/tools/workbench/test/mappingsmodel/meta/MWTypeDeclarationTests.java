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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.meta;

import java.lang.reflect.Method;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;


public class MWTypeDeclarationTests extends TestCase {
	private MWRelationalProject project;
	private MWClass objectType;
	private MWClass intType;
	private MWClass stringType;
	private MWClass cloneableType;
	private MWClass serializableType;
	private MWClass fooType;
	private MWClass vhiType;
	
	public static Test suite() {
		return new TestSuite(MWTypeDeclarationTests.class);
	}
	
	public MWTypeDeclarationTests(String name) {
		super(name);
	}
	
	private MWClass fullyPopulatedTypeFor(Class javaClass) throws Exception {
		MWClass result = this.project.typeFor(javaClass);
		result.refresh();
		return result;
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.project = this.buildProject();
		this.objectType = this.fullyPopulatedTypeFor(java.lang.Object.class);
		this.intType = this.fullyPopulatedTypeFor(int.class);
		this.stringType = this.fullyPopulatedTypeFor(java.lang.String.class);
		this.cloneableType = this.fullyPopulatedTypeFor(java.lang.Cloneable.class);
		this.serializableType = this.fullyPopulatedTypeFor(java.io.Serializable.class);
		this.vhiType = this.fullyPopulatedTypeFor(org.eclipse.persistence.indirection.ValueHolderInterface.class);
	
		this.fooType = this.project.typeNamed("foo.bar.Foo");
	
		this.fooType.addAttribute("objectAttr", this.objectType);
		this.fooType.addAttribute("vhiAttr", this.vhiType);
		this.fooType.addAttribute("vhiArrayAttr", this.vhiType, 1);
	
		MWMethod method;
	
		method = this.fooType.addMethod("voidReturnZeroParm");
	
		method = this.fooType.addMethod("objectReturnZeroParm", this.objectType);
	
		method = this.fooType.addMethod("objectArrayReturnZeroParm", this.objectType, 1);
	
		method = this.fooType.addMethod("intArrayReturnZeroParm", this.intType, 1);
	
		method = this.fooType.addMethod("stringArrayReturnZeroParm", this.stringType, 1);
	
		method = this.fooType.addMethod("cloneableReturnZeroParm", this.cloneableType, 0);
	
		method = this.fooType.addMethod("serializableReturnZeroParm", this.serializableType, 0);
	
		method = this.fooType.addMethod("voidReturnObjectParm");
		method.addMethodParameter(this.objectType);
	
		method = this.fooType.addMethod("voidReturnObjectArrayParm");
		method.addMethodParameter(this.objectType, 1);
	
		method = this.fooType.addMethod("objectReturnObjectParm", this.objectType);
		method.addMethodParameter(this.objectType);
	
		method = this.fooType.addMethod("objectReturnObjectArrayParm", this.objectType);
		method.addMethodParameter(this.objectType, 1);
	
		method = this.fooType.addMethod("objectArrayReturnObjectArrayParm", this.objectType, 1);
		method.addMethodParameter(this.objectType, 1);
	}
	
	private MWRelationalProject buildProject() {
		return new MWRelationalProject(this.getClass().getName(), MappingsModelTestTools.buildSPIManager(), null);
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSetType() {
		MWMethod method = this.objectType.methodWithSignature("clone()");
	
		method.setReturnTypeDimensionality(3);
		assertEquals("invalid dimensionality: " + method, 3, method.getReturnTypeDimensionality());
		method.setReturnType(this.project.typeFor(void.class));
		assertEquals("invalid dimensionality: " + method, 0, method.getReturnTypeDimensionality());
	}
	
	public void testSetDimensionality() throws Exception {
		MWMethod method = this.objectType.methodWithSignature("clone()");
		method.setReturnTypeDimensionality(3);
		MWTypeDeclaration returnType = getReturnTypeDeclarationFrom(method);
		assertEquals("invalid dimensionality: " + method, "java.lang.Object[][][]", returnType.displayString());
	
		method = this.objectType.methodWithSignature("wait()");
		boolean exCaught = false;
		try {
			method.setReturnTypeDimensionality(3);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue("IllegalArgumentException not thrown", exCaught);
	}
	
	public void testIsValueHolder() {
		MWClassAttribute ca;
		ca = this.fooType.attributeNamed("vhiAttr");
		assertTrue(ca.isValueHolder());
		ca = this.fooType.attributeNamed("vhiArrayAttr");
		assertTrue( ! ca.isValueHolder());
		ca = this.fooType.attributeNamed("objectAttr");
		assertTrue( ! ca.isValueHolder());
	}
	
	public void testDisplayString() throws Exception {
		MWMethod mwMethod;
		mwMethod = this.fooType.methodWithSignature("objectReturnZeroParm()");
		
		MWTypeDeclaration typeDeclaration = getReturnTypeDeclarationFrom(mwMethod);
	
		assertEquals("java.lang.Object", typeDeclaration.displayString());
		
		mwMethod = this.fooType.methodWithSignature("objectArrayReturnZeroParm()");
		typeDeclaration = getReturnTypeDeclarationFrom(mwMethod);
		assertEquals("java.lang.Object[]", typeDeclaration.displayString());
	}
	
	private MWTypeDeclaration getReturnTypeDeclarationFrom(MWMethod mwMethod) throws Exception {
		Method method = null;
		method = MWMethod.class.getDeclaredMethod("getReturnTypeDeclaration", (Class[])null);
		method.setAccessible(true);

		MWTypeDeclaration typeDeclaration = null;
		typeDeclaration = (MWTypeDeclaration) method.invoke(mwMethod, (Object[])null);
		
		return typeDeclaration;
	}

}
