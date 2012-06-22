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

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;



public abstract class ExternalMethodTests extends TestCase {
	private ExternalClassRepository repository;


	protected ExternalMethodTests(String name) {
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

	public void testDeclaringClass() throws Exception {
		this.verifyDeclaringClass(java.lang.Object.class, "toString", new String[0]);
		this.verifyDeclaringClass(java.util.Vector.class, "addAll", new String[] {"java.util.Collection"});
	}

	private void verifyDeclaringClass(Class javaClass, String methodName, String[] parmTypeNames) throws Exception {
		Method javaMethod = SPIMetaTestTools.methodNamed(javaClass, methodName, parmTypeNames);
		ExternalMethod exMethod = SPIMetaTestTools.methodNamed(this.exClassFor(javaClass), methodName, parmTypeNames);
		assertEquals(javaMethod.getDeclaringClass().getName(), exMethod.getDeclaringClass().getName());
	}

	public void testModifiers() throws Exception {
		this.verifyModifiers(java.lang.Object.class, "toString", new String[0]);
		this.verifyModifiers(java.util.Vector.class, "ensureCapacityHelper", new String[] {"int"});
	}

	private void verifyModifiers(Class javaClass, String methodName, String[] parmTypeNames) throws Exception {
		Method javaMethod = SPIMetaTestTools.methodNamed(javaClass, methodName, parmTypeNames);
		ExternalMethod exMethod = SPIMetaTestTools.methodNamed(this.exClassFor(javaClass), methodName, parmTypeNames);
		assertEquals(javaMethod.getModifiers(), exMethod.getModifiers());
	}

	public void testExceptionTypes() throws Exception {
		this.verifyExceptionTypes(java.lang.Object.class, "clone", new String[0]);
		this.verifyExceptionTypes(java.lang.Object.class, "toString", new String[0]);
	}

	private void verifyExceptionTypes(Class javaClass, String methodName, String[] parmTypeNames) throws Exception {
		Method javaMethod = SPIMetaTestTools.methodNamed(javaClass, methodName, parmTypeNames);
		ExternalMethod exMethod = SPIMetaTestTools.methodNamed(this.exClassFor(javaClass), methodName, parmTypeNames);
		assertTrue(SPIMetaTestTools.compareClasses(javaMethod.getExceptionTypes(), exMethod.getExceptionTypes()));
	}

	public void testParameterTypes() throws Exception {
		this.verifyParameterTypes(java.lang.Object.class, "toString", new String[0]);
		this.verifyParameterTypes(java.lang.Object.class, "equals", new String[] {"java.lang.Object"});
	}

	private void verifyParameterTypes(Class javaClass, String methodName, String[] parmTypeNames) throws Exception {
		Method javaMethod = SPIMetaTestTools.methodNamed(javaClass, methodName, parmTypeNames);
		ExternalMethod exMethod = SPIMetaTestTools.methodNamed(this.exClassFor(javaClass), methodName, parmTypeNames);
		assertTrue(SPIMetaTestTools.compareClasses(javaMethod.getParameterTypes(), exMethod.getParameterTypes()));
	}

	public void testReturnType() throws Exception {
		this.verifyReturnType(java.lang.Object.class, "toString", new String[0]);
		this.verifyReturnType(java.util.Vector.class, "addAll", new String[] {"java.util.Collection"});
	}

	private void verifyReturnType(Class javaClass, String methodName, String[] parmTypeNames) throws Exception {
		Method javaMethod = SPIMetaTestTools.methodNamed(javaClass, methodName, parmTypeNames);
		ExternalMethod exMethod = SPIMetaTestTools.methodNamed(this.exClassFor(javaClass), methodName, parmTypeNames);
		assertEquals(javaMethod.getReturnType().getName(), exMethod.getReturnType().getName());
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
