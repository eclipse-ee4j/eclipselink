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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classfile.AllModelSPIMetaClassFileTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classloader.AllModelSPIMetaClassLoaderTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * decentralize test creation code
 */
public class AllModelSPIMetaTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllModelSPIMetaTests.class));

		suite.addTest(AllModelSPIMetaClassFileTests.suite());
		suite.addTest(AllModelSPIMetaClassLoaderTests.suite());

		return suite;
	}

	/**
	 * suppress instantiation
	 */
	private AllModelSPIMetaTests() {
		super();
		throw new UnsupportedOperationException();
	}

}
