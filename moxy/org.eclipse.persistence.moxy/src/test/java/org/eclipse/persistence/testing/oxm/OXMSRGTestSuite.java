/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     dtwelves - Sept 2008 creation of MOXy OXM SRG
package org.eclipse.persistence.testing.oxm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.inheritance.*;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.TypeTestSuite;
import org.eclipse.persistence.testing.oxm.inheritance.classextractor.CarClassExtractorTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshalTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLUnmarshalTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.RootElementTestSuite;
import org.eclipse.persistence.testing.oxm.xpathengine.XPathEngineTestSuite;
import org.eclipse.persistence.testing.oxm.xmllogin.XMLLoginTestSuite;

public class OXMSRGTestSuite extends TestCase {
    public OXMSRGTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.OXMSRGTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("OXM SRG Test Suite");
        suite.addTestSuite(XMLMarshalTestCases.class);
        suite.addTestSuite(XMLUnmarshalTestCases.class);
        suite.addTestSuite(InheritanceMissingDescriptorTestCases.class);
        suite.addTestSuite(InheritanceCarTestCases.class);
        suite.addTest(RootElementTestSuite.suite());
        suite.addTest(XPathEngineTestSuite.suite());
        suite.addTest(XMLLoginTestSuite.suite());
        return suite;
    }
}
