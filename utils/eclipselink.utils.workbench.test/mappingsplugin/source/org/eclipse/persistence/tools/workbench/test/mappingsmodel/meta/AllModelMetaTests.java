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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllModelMetaTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllModelMetaTests.class));

		suite.addTest(MWModifierTests.suite());
		suite.addTest(MWTypeDeclarationTests.suite());
		suite.addTest(MWMethodTests.suite());
		suite.addTest(MWClassAttributeTests.suite());
		suite.addTest(MWClassTests.suite());

		suite.addTest(MWClassRepositoryTests.suite());

		return suite;
	}

	/**
	 * suppress instantiation
	 */
	private AllModelMetaTests() {
		super();
	}

}
