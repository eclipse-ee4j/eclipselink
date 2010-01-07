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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;



public abstract class ExternalConstructorTests extends TestCase {
	private ExternalClassRepository repository;


	protected ExternalConstructorTests(String name) {
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
		this.verifyDeclaringClass(java.lang.Object.class, new String[0]);
		this.verifyDeclaringClass(java.util.Vector.class, new String[] {"java.util.Collection"});
	}

	private void verifyDeclaringClass(Class javaClass, String[] parmTypeNames) throws Exception {
		Constructor javaCtor = SPIMetaTestTools.constructor(javaClass, parmTypeNames);
		ExternalConstructor exCtor = SPIMetaTestTools.constructor(this.exClassFor(javaClass), parmTypeNames);
		assertEquals(javaCtor.getDeclaringClass().getName(), exCtor.getDeclaringClass().getName());
	}

	public void testModifiers() throws Exception {
		this.verifyModifiers(java.lang.Object.class, new String[0]);
		this.verifyModifiers(java.util.Vector.class, new String[] {"java.util.Collection"});
	}

	private void verifyModifiers(Class javaClass, String[] parmTypeNames) throws Exception {
		Constructor javaCtor = SPIMetaTestTools.constructor(javaClass, parmTypeNames);
		ExternalConstructor exCtor = SPIMetaTestTools.constructor(this.exClassFor(javaClass), parmTypeNames);
		assertEquals(javaCtor.getModifiers(), exCtor.getModifiers());
	}

	public void testExceptionTypes() throws Exception {
		this.verifyExceptionTypes(java.lang.Object.class, new String[0]);

		// now, verify a constructor that actually throws an exception
		this.verifyExceptionTypes(FileInputStream.class, new String[] {"java.io.File"});
	}

	private void verifyExceptionTypes(Class javaClass, String[] parmTypeNames) throws Exception {
		Constructor javaCtor = SPIMetaTestTools.constructor(javaClass, parmTypeNames);
		ExternalConstructor exCtor = SPIMetaTestTools.constructor(this.exClassFor(javaClass), parmTypeNames);
		assertTrue(SPIMetaTestTools.compareClasses(javaCtor.getExceptionTypes(), exCtor.getExceptionTypes()));
	}

	public void testParameterTypes() throws Exception {
		this.verifyParameterTypes(java.lang.Object.class, new String[0]);
		this.verifyParameterTypes(java.util.HashMap.class, new String[] {"int", "float"});
	}

	private void verifyParameterTypes(Class javaClass, String[] parmTypeNames) throws Exception {
		Constructor javaCtor = SPIMetaTestTools.constructor(javaClass, parmTypeNames);
		ExternalConstructor exCtor = SPIMetaTestTools.constructor(this.exClassFor(javaClass), parmTypeNames);
		assertTrue(SPIMetaTestTools.compareClasses(javaCtor.getParameterTypes(), exCtor.getParameterTypes()));
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
