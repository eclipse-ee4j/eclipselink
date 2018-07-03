/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.utility.io;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllIOTests {

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
