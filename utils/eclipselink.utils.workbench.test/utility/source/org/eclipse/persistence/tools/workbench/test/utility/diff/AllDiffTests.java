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
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class AllDiffTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllDiffTests.class));

		suite.addTest(ArrayDiffTests.suite());
		suite.addTest(CollectionDiffTests.suite());
		suite.addTest(DiffableCircularReferenceDiffTests.suite());
		suite.addTest(DiffEngineCircularReferenceDiffTests.suite());
		suite.addTest(EqualityDiffTests.suite());
		suite.addTest(IdentityDiffTests.suite());
		suite.addTest(InheritanceReflectiveDiffTests.suite());
		suite.addTest(ListDiffTests.suite());
		suite.addTest(MapDiffTests.suite());
		suite.addTest(MultiClassReflectiveDiffTests.suite());
		suite.addTest(NullDiffTests.suite());
		suite.addTest(SimpleReflectiveDiffTests.suite());
		suite.addTest(UnorderedArrayDiffTests.suite());
	
		return suite;
	}
	
	private AllDiffTests() {
		super();
		throw new UnsupportedOperationException();
	}
	
}
