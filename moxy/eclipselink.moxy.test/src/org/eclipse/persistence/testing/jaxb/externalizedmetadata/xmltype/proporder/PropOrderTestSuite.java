/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.proporder;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PropOrderTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("propOrder Test Suite");
        suite.addTestSuite(DefaultTestCases.class);
        suite.addTestSuite(AllTestCases.class);
        suite.addTestSuite(SequenceTestCases.class);
        suite.addTestSuite(OverrideSequenceTestCases.class);
        suite.addTestSuite(OverrideAllTestCases.class);
        return suite;
    }

}
