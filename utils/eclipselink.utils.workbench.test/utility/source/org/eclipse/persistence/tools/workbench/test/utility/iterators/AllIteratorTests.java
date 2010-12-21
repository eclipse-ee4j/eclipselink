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
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class AllIteratorTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllIteratorTests.class));
	
		suite.addTest(ArrayIteratorTests.suite());
		suite.addTest(ArrayListIteratorTests.suite());
		suite.addTest(ChainIteratorTests.suite());
		suite.addTest(CloneIteratorTests.suite());
		suite.addTest(CloneListIteratorTests.suite());
		suite.addTest(CompositeIteratorTests.suite());
		suite.addTest(CompositeListIteratorTests.suite());
		suite.addTest(EnumerationIteratorTests.suite());
		suite.addTest(FilteringIteratorTests.suite());
		suite.addTest(GraphIteratorTests.suite());
		suite.addTest(IteratorEnumerationTests.suite());
		suite.addTest(NullEnumerationTests.suite());
		suite.addTest(NullIteratorTests.suite());
		suite.addTest(NullListIteratorTests.suite());
		suite.addTest(PeekableIteratorTests.suite());
		suite.addTest(ReadOnlyIteratorTests.suite());
		suite.addTest(ReadOnlyListIteratorTests.suite());
		suite.addTest(SingleElementIteratorTests.suite());
		suite.addTest(SingleElementListIteratorTests.suite());
		suite.addTest(TransformationIteratorTests.suite());
		suite.addTest(TransformationListIteratorTests.suite());
		suite.addTest(TreeIteratorTests.suite());
	
		return suite;
	}
	
	private AllIteratorTests() {
		super();
	}
	
}
