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
 *     rbarkhouse - 2009-10-15 13:10:33  - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.converter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CompositeObjectSelfConverterTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Composite Object Self Mapping with Converter Test Suite");
        suite.addTestSuite(CompositeObjectSelfConverterTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.CompositeObjectSelfConverterTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

    public String getName() {
        String nameSoFar = super.getName();
        return nameSoFar + Boolean.getBoolean("useDeploymentXML");
    }

}