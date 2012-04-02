/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement;

import org.eclipse.persistence.testing.jaxb.xmlelement.model.EmptyCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.model.EmptyJSONArrayTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.model.FullTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.model.FullTestCasesNS;
import org.eclipse.persistence.testing.jaxb.xmlelement.model.SpecialCharacterTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.order.ElementOrderingTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XmlElementTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlElement Test Suite");

        suite.addTestSuite(XmlElementNamespaceTestCases.class);
        suite.addTestSuite(XmlElementNoNamespaceTestCases.class);
        suite.addTestSuite(XmlElementCollectionTestCases.class);
        suite.addTestSuite(XmlElementNillableTestCases.class);
        suite.addTestSuite(FullTestCases.class);
        suite.addTestSuite(FullTestCasesNS.class);
        suite.addTestSuite(EmptyCollectionTestCases.class);
        suite.addTestSuite(EmptyJSONArrayTestCases.class);
        suite.addTestSuite(SpecialCharacterTestCases.class);
        suite.addTestSuite(ElementOrderingTestCases.class);
        return suite;
    }

}