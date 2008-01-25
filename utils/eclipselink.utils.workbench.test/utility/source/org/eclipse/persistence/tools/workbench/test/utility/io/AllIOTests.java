/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility.io;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllIOTests {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", AllIOTests.class.getName()});
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllIOTests.class));

		suite.addTest(FileToolsTests.suite());
		suite.addTest(IndentingPrintWriterTests.suite());
		suite.addTest(InvalidInputStreamTests.suite());
		suite.addTest(InvalidOutputStreamTests.suite());
		suite.addTest(InvalidReaderTests.suite());
		suite.addTest(InvalidWriterTests.suite());
		suite.addTest(NullInputStreamTests.suite());
		suite.addTest(NullOutputStreamTests.suite());
		suite.addTest(NullReaderTests.suite());
		suite.addTest(NullWriterTests.suite());
		suite.addTest(PipeTests.suite());
		suite.addTest(StringBufferWriterTests.suite());
		suite.addTest(TeeOutputStreamTests.suite());
		suite.addTest(TeeWriterTests.suite());
		suite.addTest(TokenTests.suite());

		return suite;
	}

	private AllIOTests() {
		super();
		throw new UnsupportedOperationException();
	}
	
}
