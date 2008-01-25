/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classfile;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;

/**
 * decentralize test creation code
 */
public class AllModelSPIMetaClassFileTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllModelSPIMetaClassFileTests.class.getName()});
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllModelSPIMetaClassFileTests.class));

		suite.addTest(CFExternalClassRepositoryTests.suite());
		suite.addTest(CFExternalClassTests.suite());
		suite.addTest(CFExternalConstructorTests.suite());
		suite.addTest(CFExternalFieldTests.suite());
		suite.addTest(CFExternalMethodTests.suite());
		suite.addTest(CFExternalClassDescriptionTests.suite());

		return suite;
	}

	static File[] buildMinimumSystemClasspath() {
		return new File[] {new File(Classpath.locationFor(java.lang.Object.class))};
	}

	/**
	 * suppress instantiation
	 */
	private AllModelSPIMetaClassFileTests() {
		super();
		throw new UnsupportedOperationException();
	}

}
