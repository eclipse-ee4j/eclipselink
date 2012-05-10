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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;



public abstract class ExternalClassTests extends TestCase {
	protected ExternalClassRepository repository;


	protected ExternalClassTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.repository = this.buildRepository();
	}

	protected abstract ExternalClassRepository buildRepository();
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testDeclaredClasses() throws Exception {
		this.verifyDeclaredClasses(java.lang.Object.class);
		this.verifyDeclaredClasses(int.class);
		this.verifyDeclaredClasses(void.class);
		this.verifyDeclaredClasses(java.util.Map.class);
	}

	private void verifyDeclaredClasses(Class javaClass) throws Exception {
		ExternalClass exClass = this.exClassFor(javaClass);
		assertTrue(SPIMetaTestTools.compareClasses(javaClass.getDeclaredClasses(), exClass.getDeclaredClasses()));
	}

	public void testDeclaredConstructors() throws Exception {
		this.verifyDeclaredConstructors(java.lang.Object.class);
		this.verifyDeclaredConstructors(int.class);
		this.verifyDeclaredConstructors(void.class);
		this.verifyDeclaredConstructors(java.util.Vector.class);
	}

	private void verifyDeclaredConstructors(Class javaClass) throws Exception {
		ExternalClass exClass = this.exClassFor(javaClass);
		assertTrue(SPIMetaTestTools.compareMembers(javaClass.getDeclaredConstructors(), exClass.getDeclaredConstructors()));
	}

	public void testDeclaredFields() throws Exception {
		this.verifyDeclaredFields(java.lang.Object.class);
		this.verifyDeclaredFields(int.class);
		this.verifyDeclaredFields(void.class);
		this.verifyDeclaredFields(java.util.Vector.class);
	}

	private void verifyDeclaredFields(Class javaClass) throws Exception {
		ExternalClass exClass = this.exClassFor(javaClass);
		assertTrue(SPIMetaTestTools.compareMembers(javaClass.getDeclaredFields(), exClass.getDeclaredFields()));
	}

	public void testDeclaredMethods() throws Exception {
		this.verifyDeclaredMethods(java.lang.Object.class);
		this.verifyDeclaredMethods(int.class);
		this.verifyDeclaredMethods(void.class);
		this.verifyDeclaredMethods(java.util.Vector.class);
	}

	private void verifyDeclaredMethods(Class javaClass) throws Exception {
		ExternalClass exClass = this.exClassFor(javaClass);
		assertTrue(SPIMetaTestTools.compareMembers(javaClass.getDeclaredMethods(), exClass.getDeclaredMethods()));
	}

	public void testDeclaringClass() throws Exception {
		this.verifyDeclaringClass(java.lang.Object.class);
		this.verifyDeclaringClass(int.class);
		this.verifyDeclaringClass(void.class);
		this.verifyDeclaringClass(java.util.Map.Entry.class);
	}

	private void verifyDeclaringClass(Class javaClass) throws Exception {
		Class javaDeclaringClass = javaClass.getDeclaringClass();
		ExternalClass exClass = this.exClassFor(javaClass);
		if (javaDeclaringClass == null) {
			assertNull(exClass.getDeclaringClass());
		} else {
			assertEquals(javaDeclaringClass.getName(), exClass.getDeclaringClass().getName());
		}
	}

	public void testInterfaces() throws Exception {
		this.verifyInterfaces(java.lang.Object.class);
		this.verifyInterfaces(int.class);
		this.verifyInterfaces(void.class);
		this.verifyInterfaces(java.util.Vector.class);
	}

	private void verifyInterfaces(Class javaClass) throws Exception {
		ExternalClass exClass = this.exClassFor(javaClass);
		assertTrue(SPIMetaTestTools.compareClasses(javaClass.getInterfaces(), exClass.getInterfaces()));
	}

	public void testModifiers() throws Exception {
		this.verifyModifiers(java.lang.Object.class);
		this.verifyModifiers(int.class);
		this.verifyModifiers(void.class);
		this.verifyModifiers(java.lang.String.class);
	}

	private void verifyModifiers(Class javaClass) throws Exception {
		ExternalClass exClass = this.exClassFor(javaClass);
		assertEquals(javaClass.getModifiers(), exClass.getModifiers());
	}

	public void testSuperclass() throws Exception {
		this.verifySuperClass(java.lang.Object.class);
		this.verifySuperClass(int.class);
		this.verifySuperClass(void.class);
		this.verifySuperClass(java.util.Map.Entry.class);
	}

	private void verifySuperClass(Class javaClass) throws Exception {
		Class javaSuperClass = javaClass.getSuperclass();
		ExternalClass exClass = this.exClassFor(javaClass);
		if (javaSuperClass == null) {
			assertNull(exClass.getSuperclass());
		} else {
			assertEquals(javaSuperClass.getName(), exClass.getSuperclass().getName());
		}
	}

	public void testInterface() throws Exception {
		this.verifyInterface(java.lang.Object.class);
		this.verifyInterface(int.class);
		this.verifyInterface(void.class);
		this.verifyInterface(java.lang.String.class);
	}

	private void verifyInterface(Class javaClass) throws Exception {
		ExternalClass exClass = this.exClassFor(javaClass);
		assertEquals(javaClass.isInterface(), exClass.isInterface());
	}

	public void testPrimitive() throws Exception {
		this.verifyPrimitive(java.lang.Object.class);
		this.verifyPrimitive(int.class);
		this.verifyPrimitive(void.class);
		this.verifyPrimitive(java.lang.String.class);
	}

	private void verifyPrimitive(Class javaClass) throws Exception {
		ExternalClass exClass = this.exClassFor(javaClass);
		assertEquals(javaClass.isPrimitive(), exClass.isPrimitive());
	}

	private ExternalClass exClassFor(Class javaClass) throws Exception {
		return this.descriptionFor(javaClass).getExternalClass();
	}

	private ExternalClassDescription descriptionFor(Class javaClass) {
		return this.descriptionForClassNamed(javaClass.getName());
	}

	private ExternalClassDescription descriptionForClassNamed(String className) {
		ExternalClassDescription[] descriptions = this.repository.getClassDescriptions();
		for (int i = descriptions.length; i-- > 0; ) {
			if (descriptions[i].getName().equals(className)) {
				return descriptions[i];
			}
		}
		throw new IllegalArgumentException(className);
	}

}
