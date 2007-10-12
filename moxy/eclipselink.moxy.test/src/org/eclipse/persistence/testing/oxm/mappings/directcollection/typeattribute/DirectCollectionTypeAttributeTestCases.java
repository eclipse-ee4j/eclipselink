/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute.identifiedbyname.withgroupingelement.*;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute.identifiedbyname.withoutgroupingelement.*;

public class DirectCollectionTypeAttributeTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("DirectCollection  Type Attribute Test Cases");

        suite.addTestSuite(WithGroupingElementIdentifiedByNameIntegerTestCases.class);
        suite.addTestSuite(WithGroupingElementIdentifiedByNameTestCases.class);        

        suite.addTestSuite(WithoutGroupingElementIdentifiedByNameIntegerTestCases.class);
        suite.addTestSuite(WithoutGroupingElementIdentifiedByNameTestCases.class);        

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute.DirectCollectionTypeAttributeTestCases" };
        junit.swingui.TestRunner.main(arguments);
    }
}