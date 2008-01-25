/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility.string;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllStringTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllStringTests.class.getName()});
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllStringTests.class));

		suite.addTest(BestFirstPartialStringComparatorEngineTests.suite());
		suite.addTest(CaseInsensitivePartialStringComparatorTests.suite());
		suite.addTest(InversePartialStringComparatorTests.suite());
		suite.addTest(PrefixStrippingPartialStringComparatorEngineTests.suite());
		suite.addTest(RegularExpressionStringMatcherAdapterTests.suite());
		suite.addTest(ExhaustivePartialStringComparatorEngineTests.suite());
		suite.addTest(SimpleStringMatcherTests.suite());
		suite.addTest(StringToolsTests.suite());
		suite.addTest(SuffixStrippingPartialStringComparatorEngineTests.suite());
		suite.addTest(XMLStringEncoderTests.suite());

		return suite;
	}

	private AllStringTests() {
		super();
		throw new UnsupportedOperationException();
	}
	
}
