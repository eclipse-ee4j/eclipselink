/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.sdo.helper.xsdhelper;

import org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.XSDHelperDefineTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.XSDHelperGenerateTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOXSDHelperTestSuite {
    public SDOXSDHelperTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XSDHelper Tests");
        suite.addTest(new XSDHelperDefineTestSuite().suite());
        suite.addTest(new XSDHelperGenerateTestSuite().suite());
        suite.addTestSuite(SDOXSDHelperExceptionTestCases.class);
        return suite;
    }
}
