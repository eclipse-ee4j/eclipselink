/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dtwelves - Sept 2008 Creation of JAXB SRG Test Suite
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBDOMTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBSAXTestSuite;

public class JAXBSRGTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB SRG Test Suite");
        suite.addTest(SchemaGenTestSuite.suite()); 
        suite.addTest(JAXBSAXTestSuite.suite()); 
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNoNamespaceTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementNamespaceTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementCollectionTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeNamespaceTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeNoNamespaceTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelements.XmlElementsComplexTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelementref.EmployeeCollectionTestCases.class);
 
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBSRGTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
