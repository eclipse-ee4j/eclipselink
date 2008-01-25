/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classfile.AllModelSPIMetaClassFileTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classloader.AllModelSPIMetaClassLoaderTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * decentralize test creation code
 */
public class AllModelSPIMetaTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllModelSPIMetaTests.class.getName()});
	}

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
