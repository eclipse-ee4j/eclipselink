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
*     bdoughan - August 18/2009 - 1.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.instanceclass;

import java.io.InputStream;

import commonj.sdo.Type;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class InstanceClassTestCases extends SDOTestCase {

    private static final String XML_SCHEMA_INTEFACE_CORRECT_GETTER = "org/eclipse/persistence/testing/sdo/instanceclass/CustomerInterfaceWithCorrectGetters.xsd";
    private static final String XML_SCHEMA_INTEFACE_INCORRECT_GETTER = "org/eclipse/persistence/testing/sdo/instanceclass/CustomerInterfaceWithIncorrectGetters.xsd";
    private static final String XML_SCHEMA_CLASS_CORRECT_GETTER = "org/eclipse/persistence/testing/sdo/instanceclass/CustomerClassWithCorrectGetters.xsd";
    private static final String XML_SCHEMA_WITH_CHANGE_SUMMARY = "org/eclipse/persistence/testing/sdo/instanceclass/WithChangeSummary.xsd";

    public InstanceClassTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
    }

    public void testInterfaceWithCorrectGetters() {
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA_INTEFACE_CORRECT_GETTER);
        aHelperContext.getXSDHelper().define(xsd, null);
        Type type = typeHelper.getType("urn:customer", "CustomerInterfaceWithCorrectGetters");
        assertSame(CustomerInterfaceWithCorrectGetters.class, type.getInstanceClass());
    }

    public void testInterfaceWithIncorrectGetters() {
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA_INTEFACE_INCORRECT_GETTER);
        aHelperContext.getXSDHelper().define(xsd, null);
        Type type = typeHelper.getType("urn:customer", "CustomerInterfaceWithIncorrectGetters");
        assertNull(type.getInstanceClass());
    }

    public void testClassWithCorrectGetters() {
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA_CLASS_CORRECT_GETTER);
        aHelperContext.getXSDHelper().define(xsd, null);
        Type type = typeHelper.getType("urn:customer", "CustomerClassWithCorrectGetters");
        assertNull(type.getInstanceClass());
    }

    /**
     * Test for: 
     * Bug 325164 - Exclude getChangeSummary check from getInstanceClass method
     * http://bugs.eclipse.org/325164
     */
    public void testIterfaceWithoutChangeSummaryGetter() {
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA_WITH_CHANGE_SUMMARY);
        aHelperContext.getXSDHelper().define(xsd, null);
        Type type = typeHelper.getType("urn:customer", "CustomerInterfaceWithCorrectGetters");
        assertSame(CustomerInterfaceWithCorrectGetters.class, type.getInstanceClass());
    }

}