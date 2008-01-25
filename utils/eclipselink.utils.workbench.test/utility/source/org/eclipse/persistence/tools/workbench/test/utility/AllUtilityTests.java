/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility;

import org.eclipse.persistence.tools.workbench.test.utility.classfile.AllClassFileTests;
import org.eclipse.persistence.tools.workbench.test.utility.diff.AllDiffTests;
import org.eclipse.persistence.tools.workbench.test.utility.events.AllEventsTests;
import org.eclipse.persistence.tools.workbench.test.utility.filters.AllFilterTests;
import org.eclipse.persistence.tools.workbench.test.utility.io.AllIOTests;
import org.eclipse.persistence.tools.workbench.test.utility.iterators.AllIteratorTests;
import org.eclipse.persistence.tools.workbench.test.utility.node.AllNodeTests;
import org.eclipse.persistence.tools.workbench.test.utility.string.AllStringTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * decentralize test creation code
 */
public class AllUtilityTests {
	
	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllUtilityTests.class.getName()});
	}

	public static Test suite() {
		return suite(true);
	}

	public static Test suite(boolean all) {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllUtilityTests.class));
	
		suite.addTest(AllClassFileTests.suite());
		suite.addTest(AllDiffTests.suite());
		suite.addTest(AllEventsTests.suite());
		suite.addTest(AllFilterTests.suite());
		suite.addTest(AllIOTests.suite());
		suite.addTest(AllIteratorTests.suite());
		suite.addTest(AllNodeTests.suite());
		suite.addTest(AllStringTests.suite());

		suite.addTest(AbstractModelTests.suite());
		suite.addTest(ClasspathTests.suite());
		suite.addTest(ClassToolsTests.suite());
		suite.addTest(CollectionToolsTests.suite());
		suite.addTest(CounterTests.suite());
		suite.addTest(HashBagTests.suite());
		suite.addTest(IdentityHashBagTests.suite());
		suite.addTest(NameToolsTests.suite());
		suite.addTest(RangeTests.suite());
		suite.addTest(ReverseComparatorTests.suite());
		suite.addTest(SimpleAssociationTests.suite());
		suite.addTest(SimpleExceptionBroadcasterTests.suite());
		suite.addTest(SimpleStackTests.suite());
		suite.addTest(SynchronizedBooleanTests.suite());
		suite.addTest(SynchronizedObjectTests.suite());
		suite.addTest(SynchronizedStackTests.suite());
		suite.addTest(TriStateBooleanTests.suite());
		suite.addTest(XMLToolsReadTests.suite());
		suite.addTest(XMLToolsWriteTests.suite());

		return suite;
	}
	
	private AllUtilityTests() {
		super();
		throw new UnsupportedOperationException();
	}
	
}
