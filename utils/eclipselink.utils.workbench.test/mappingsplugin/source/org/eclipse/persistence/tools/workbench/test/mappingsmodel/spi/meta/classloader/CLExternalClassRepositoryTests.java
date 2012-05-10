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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classloader;

import java.io.File;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader.CLExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.ExternalClassRepositoryTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


public class CLExternalClassRepositoryTests
	extends ExternalClassRepositoryTests
{

	public static Test suite() {
		return new TestSuite(CLExternalClassRepositoryTests.class);
	}

	public CLExternalClassRepositoryTests(String name) {
		super(name);
	}

	protected ExternalClassRepositoryFactory buildFactory() {
		return CLExternalClassRepositoryFactory.instance();
	}

	protected ExternalClassRepository systemClasspathRepository() throws ClassNotFoundException {
		return this.factory.buildClassRepository(new File[0]);
	}

	protected ExternalClassRepository systemRepositoryFor(ExternalClassRepository repository) throws ClassNotFoundException {
		Class systemReposClass = this.systemRepositoryClass();
		return (ExternalClassRepository) ClassTools.getStaticFieldValue(systemReposClass, "INSTANCE");
	}

	protected void verifyArrayTypesContains(Map arrayTypes, Class arrayType) {
		assertTrue(arrayTypes.containsKey(arrayType));
	}

	protected ExternalClassRepository buildExternalClassRepository(File[] classpath) {
		return this.factory.buildClassRepository(classpath);
	}

//	public void testPerformance() throws Exception {
//		// clear out the cached external class descriptions
//		this.clearSystemRepository();
//		super.testPerformance();
//	}
//
	public void testSystemArrayTypes() throws Exception {
		// clear out the cached external class descriptions
		this.clearSystemRepository();
		super.testSystemArrayTypes();
	}

	public void testProjectArrayTypes() throws Exception {
		// clear out the cached external class descriptions
		this.clearSystemRepository();
		super.testProjectArrayTypes();
	}

	private void clearSystemRepository() throws ClassNotFoundException {
		Class systemReposClass = this.systemRepositoryClass();
		ClassTools.setStaticFieldValue(systemReposClass, "INSTANCE", null);
	}

	private Class systemRepositoryClass() throws ClassNotFoundException {
		return Class.forName("org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader.SystemCLExternalClassRepository");
	}

}
