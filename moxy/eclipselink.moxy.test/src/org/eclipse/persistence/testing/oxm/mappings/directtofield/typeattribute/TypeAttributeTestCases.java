/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute.*;

public class TypeAttributeTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("TypeAttribute Test Cases");
        suite.addTestSuite(TypeAttributeStringTestCases.class);
        suite.addTestSuite(TypeAttributeIntegerTestCases.class);
        suite.addTestSuite(TypeAttributeFloatTestCases.class);
        suite.addTestSuite(TypeAttributeDoubleTestCases.class);
        suite.addTestSuite(TypeAttributeDateTimeTestCases.class);
        suite.addTestSuite(TypeAttributeDateTestCases.class);
        suite.addTestSuite(TypeAttributeTimeTestCases.class);
        suite.addTestSuite(TypeAttributeCustomAddTestCases.class);
        suite.addTestSuite(TypeAttributeNonXsiPrefixTestCases.class);
        suite.addTestSuite(TypeAttributeNullTestCases.class);
		suite.addTestSuite(TypeAttributeUserTypeNamespaceOnChildTestCases.class);
		suite.addTestSuite(TypeAttributeUserTypeNamespaceOnParentTestCases.class);
		

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute.TypeAttributeTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
