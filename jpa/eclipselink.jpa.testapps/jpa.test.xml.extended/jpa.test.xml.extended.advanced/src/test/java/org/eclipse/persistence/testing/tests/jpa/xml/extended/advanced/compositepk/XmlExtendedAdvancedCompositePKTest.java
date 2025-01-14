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
//     01/25/2011-2.3 Guy Pelletier
//       - 333913: @OrderBy and <order-by/> without arguments should order by primary
package org.eclipse.persistence.testing.tests.jpa.xml.extended.advanced.compositepk;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.tests.jpa.xml.advanced.compositepk.XmlAdvancedCompositePKTest;


public class XmlExtendedAdvancedCompositePKTest extends XmlAdvancedCompositePKTest {
    public XmlExtendedAdvancedCompositePKTest() {
        super();
    }

    public XmlExtendedAdvancedCompositePKTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "extended-advanced";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlExtendedAdvancedCompositePKTest - extended-advanced");

        suite.addTest(new XmlExtendedAdvancedCompositePKTest("testSetup"));
        suite.addTest(new XmlExtendedAdvancedCompositePKTest("testOrderBySetting"));
        suite.addTest(new XmlExtendedAdvancedCompositePKTest("testCreateDepartment"));
        suite.addTest(new XmlExtendedAdvancedCompositePKTest("testCreateScientists"));
        suite.addTest(new XmlExtendedAdvancedCompositePKTest("testReadDepartment"));
        suite.addTest(new XmlExtendedAdvancedCompositePKTest("testReadJuniorScientist"));
        suite.addTest(new XmlExtendedAdvancedCompositePKTest("testAnyAndAll"));
        suite.addTest(new XmlExtendedAdvancedCompositePKTest("testDepartmentAdmin"));

        return suite;
    }
}
