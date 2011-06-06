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
* mmacivor - January 09, 2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ContainerAccessorTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("ContainerAcessorTestSuite");
        suite.addTestSuite(ContainerInstanceVariableAccessorTestCases.class);
        suite.addTestSuite(ContainerMethodAccessorTestCases.class);
        suite.addTestSuite(ContainerInvalidAttributeTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.containeracessor.ContainerAccessorTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
