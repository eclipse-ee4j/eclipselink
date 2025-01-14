/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.weaving;

// J2SE imports

import junit.framework.Test;
import org.eclipse.persistence.testing.framework.JUnitTestCase;
import org.eclipse.persistence.testing.framework.TestModel;

import java.util.Enumeration;

public class SimpleWeavingTestModel extends TestModel {

    public SimpleWeavingTestModel () {
        setDescription("This simple model tests TopLink's weaving functionality.");
    }

    @Override
    public void addTests() {
        junit.framework.TestSuite testsuite = (junit.framework.TestSuite)SimpleWeaverTestSuite.suite();
        for (Enumeration<Test> e = testsuite.tests(); e.hasMoreElements();) {
            junit.framework.TestCase testcase =    (junit.framework.TestCase)e.nextElement();
            addTest(new JUnitTestCase(testcase));
        }
    }

}
