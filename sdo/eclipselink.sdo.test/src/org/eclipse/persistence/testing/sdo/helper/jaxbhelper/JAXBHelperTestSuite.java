/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.changesummary.ChangeSummaryTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.containment.ContainmentTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.copyhelper.CopyHelperTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.datafactory.DataFactoryTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.helpercontext.HelperContextTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.identity.IdentityTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.isset.IsSetAndUnsetTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.jaxb.inverse.InverseTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.mappings.MappingsTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.oppositeproperty.OppositePropertyTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.xmlhelper.XMLHelperTestCases;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.xsdhelper.XSDHelperTestCases;

public class JAXBHelperTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("All SDODataHelper Tests");
        suite.addTestSuite(CopyHelperTestCases.class);
        suite.addTestSuite(HelperContextTestCases.class);
        suite.addTestSuite(JAXBTestCases.class);
        suite.addTestSuite(InverseTestCases.class);
        suite.addTestSuite(MappingsTestCases.class);
        suite.addTestSuite(OppositePropertyTestCases.class);
        suite.addTestSuite(XMLHelperTestCases.class);
        suite.addTestSuite(XSDHelperTestCases.class);
        suite.addTestSuite(DataFactoryTestCases.class);
        suite.addTestSuite(ContainmentTestCases.class);
        suite.addTestSuite(IsSetAndUnsetTestCases.class);
        suite.addTestSuite(IdentityTestCases.class);
        suite.addTestSuite(ChangeSummaryTestCases.class);
        return suite;
    }

}
