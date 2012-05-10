/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.attributes;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOAttributeXSDTestSuite {
    public SDOAttributeXSDTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
    *  Inherited suite mthod for generating all test cases.
    * @return
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("All XSDAttribute Tests");
        suite.addTest(new TestSuite(XSDHelperAttributeTestCases.class));
        return suite;
    }
}
