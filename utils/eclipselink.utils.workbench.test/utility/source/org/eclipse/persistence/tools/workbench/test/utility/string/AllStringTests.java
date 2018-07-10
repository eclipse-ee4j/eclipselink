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
package org.eclipse.persistence.tools.workbench.test.utility.string;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllStringTests {

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
