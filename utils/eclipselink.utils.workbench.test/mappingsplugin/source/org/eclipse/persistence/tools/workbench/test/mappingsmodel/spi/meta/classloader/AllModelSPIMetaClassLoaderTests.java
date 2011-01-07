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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta.classloader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllModelSPIMetaClassLoaderTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllModelSPIMetaClassLoaderTests.class));

		suite.addTest(CLExternalClassRepositoryTests.suite());
		suite.addTest(CLExternalClassTests.suite());
		suite.addTest(CLExternalConstructorTests.suite());
		suite.addTest(CLExternalFieldTests.suite());
		suite.addTest(CLExternalMethodTests.suite());
		suite.addTest(CLExternalClassDescriptionTests.suite());

		return suite;
	}

	/**
	 * suppress instantiation
	 */
	private AllModelSPIMetaClassLoaderTests() {
		super();
		throw new UnsupportedOperationException();
	}

}
