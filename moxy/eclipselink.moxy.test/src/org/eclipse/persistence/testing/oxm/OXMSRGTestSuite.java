/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dtwelves - Sept 2008 creation of MOXy OXM SRG
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.inheritance.*;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.TypeTestSuite;
import org.eclipse.persistence.testing.oxm.inheritance.classextractor.CarClassExtractorTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshallerTestSuite;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.RootElementTestSuite;
import org.eclipse.persistence.testing.oxm.xpathengine.XPathEngineTestSuite;
import org.eclipse.persistence.testing.oxm.xmllogin.XMLLoginTestSuite;
import org.eclipse.persistence.testing.oxm.converter.*;
import org.eclipse.persistence.testing.oxm.converter.typesafeenum.TypeSafeEnumConverterTestCases;

public class OXMSRGTestSuite extends TestCase {
    public OXMSRGTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.OXMSRGTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("OXM SRG Test Suite");
        //line 40 and 55 are commented out due to test logic validation error, add individual inheritance and converter tests instead
        //suite.addTest(InheritanceTestSuite.suite());
        //suite.addTestSuite(InheritanceTestCases.class);
        suite.addTestSuite(InheritanceMissingDescriptorTestCases.class);
        suite.addTestSuite(InheritanceCarTestCases.class);
        suite.addTestSuite(InheritanceCarDiffPrefixTestCases.class);
        suite.addTestSuite(InheritanceCarNoPrefixTestCases.class);
        suite.addTestSuite(InheritanceVehicleTestCases.class);
        suite.addTestSuite(InheritanceVehicleDiffPrefixTestCases.class);
        suite.addTestSuite(InheritanceDiffPrefixNonRootTestCases.class);
        suite.addTest(TypeTestSuite.suite());
        suite.addTestSuite(CarClassExtractorTestCases.class);
        suite.addTest(XMLMarshallerTestSuite.suite()); 
        suite.addTest(RootElementTestSuite.suite());
        suite.addTest(XPathEngineTestSuite.suite());
        suite.addTest(XMLLoginTestSuite.suite());
        //suite.addTest(ConverterTestSuite.suite());
        //suite.addTestSuite(ObjectTypeConverterTestCases.class);
        suite.addTestSuite(TypeSafeEnumConverterTestCases.class);
        return suite;
    }
}