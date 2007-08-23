/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.schemagen;

import org.eclipse.persistence.testing.jaxb.schemagen.employee.SchemaGenEmployeeTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmltype.SchemaGenXMLTypeTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlrootelement.SchemaGenXMLRootElementTestCases;
import junit.framework.Test;
import junit.framework.TestSuite;

public class SchemaGenTestSuite extends TestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB 2.0 Schema Generation Test Suite");
        suite.addTestSuite(SchemaGenEmployeeTestCases.class);
        suite.addTestSuite(SchemaGenXMLTypeTestCases.class);
        suite.addTestSuite(SchemaGenXMLRootElementTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
