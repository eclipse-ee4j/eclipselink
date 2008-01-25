/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.meta;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllModelMetaTests {

	public static void main(String[] args) {
		junit.swingui.TestRunner.main(new String[] {"-c", AllModelMetaTests.class.getName()});
	}

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
