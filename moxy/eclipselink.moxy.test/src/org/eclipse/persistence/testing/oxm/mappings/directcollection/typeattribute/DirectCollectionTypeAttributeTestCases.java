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
        junit.textui.TestRunner.main(arguments);
    }
}
