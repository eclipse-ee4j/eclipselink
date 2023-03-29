/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     bdoughan - August 18/2009 - 1.2 - Initial implementation
package org.eclipse.persistence.testing.sdo.instanceclass;

import java.io.InputStream;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import static org.eclipse.persistence.sdo.SDOSystemProperties.SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME;

public class InstanceClassTestCases extends SDOTestCase {

    private static final String XML_SCHEMA_INTEFACE_CORRECT_GETTER = "org/eclipse/persistence/testing/sdo/instanceclass/CustomerInterfaceWithCorrectGetters.xsd";
    private static final String XML_SCHEMA_INTEFACE_INCORRECT_GETTER = "org/eclipse/persistence/testing/sdo/instanceclass/CustomerInterfaceWithIncorrectGetters.xsd";
    private static final String XML_SCHEMA_CLASS_CORRECT_GETTER = "org/eclipse/persistence/testing/sdo/instanceclass/CustomerClassWithCorrectGetters.xsd";
    private static final String XML_SCHEMA_WITH_CHANGE_SUMMARY = "org/eclipse/persistence/testing/sdo/instanceclass/WithChangeSummary.xsd";

    private String strictTypeCheckingPropertyValueBackup;

    public InstanceClassTestCases(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        super.setUp();
        strictTypeCheckingPropertyValueBackup = System.getProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME);
        System.clearProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        if (strictTypeCheckingPropertyValueBackup != null) {
            System.setProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME, strictTypeCheckingPropertyValueBackup);
        } else {
            System.clearProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME);
        }
        strictTypeCheckingPropertyValueBackup = null;
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

    /**
     * Tests defining new type from XSD with with instanceClass interface perfectly matching the XSD
     * with relaxed type checking.
     *
     * This tests works only with {@link SDOHelperContext} and subclasses (otherwise tests nothing).
     */
    public void testInterfaceWithCorrectGettersAndRelaxedTypeChecking() {
        SDOHelperContext helperContext = new SDOHelperContext();
        helperContext.setStrictTypeCheckingEnabled(false);
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA_INTEFACE_CORRECT_GETTER);
        helperContext.getXSDHelper().define(xsd, null);
        Type type = helperContext.getTypeHelper().getType("urn:customer", "CustomerInterfaceWithCorrectGetters");
        assertSame(CustomerInterfaceWithCorrectGetters.class, type.getInstanceClass());

        DataObject dataObject = helperContext.getDataFactory().create(CustomerInterfaceWithCorrectGetters.class);
        assertNotNull(dataObject);
        assertTrue(dataObject instanceof CustomerInterfaceWithCorrectGetters);
    }

    /**
     * Tests defining new type from XSD with with instanceClass interface not matching the XSD
     * with relaxed type checking.
     *
     * This tests works only with {@link SDOHelperContext} and subclasses (otherwise tests nothing).
     */
    public void testInterfaceWithIncorrectGettersAndRelaxedTypeChecking() {
        SDOHelperContext helperContext = new SDOHelperContext();
        helperContext.setStrictTypeCheckingEnabled(false);
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA_INTEFACE_INCORRECT_GETTER);
        helperContext.getXSDHelper().define(xsd, null);
        Type type = helperContext.getTypeHelper().getType("urn:customer", "CustomerInterfaceWithIncorrectGetters");
        assertSame(CustomerInterfaceWithIncorrectGetters.class, type.getInstanceClass());

        DataObject dataObject = helperContext.getDataFactory().create(CustomerInterfaceWithIncorrectGetters.class);
        assertNotNull(dataObject);
        assertTrue(dataObject instanceof CustomerInterfaceWithIncorrectGetters);
    }

    /**
     * Tests defining new type from XSD with with instanceClass being a class
     * with relaxed type checking.
     *
     * This tests works only with {@link SDOHelperContext} and subclasses (otherwise tests nothing).
     */
    public void testClassWithCorrectGettersAndRelaxedTypeChecking() {
        SDOHelperContext helperContext = new SDOHelperContext();
        helperContext.setStrictTypeCheckingEnabled(false);
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA_CLASS_CORRECT_GETTER);
        helperContext.getXSDHelper().define(xsd, null);
        Type type = helperContext.getTypeHelper().getType("urn:customer", "CustomerClassWithCorrectGetters");
        assertNull(type.getInstanceClass());
    }

    /**
     * Tests defining new type from XSD with with instanceClass interface not matching the XSD
     * with relaxed type checking driven by property.
     *
     * This tests works only with {@link SDOHelperContext} and subclasses (otherwise tests nothing).
     */
    public void testInterfaceWithIncorrectGettersAndRelaxedTypeCheckingByProperty() {
        System.setProperty(SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME, "false");
        SDOHelperContext helperContext = new SDOHelperContext();
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA_INTEFACE_INCORRECT_GETTER);
        helperContext.getXSDHelper().define(xsd, null);
        Type type = helperContext.getTypeHelper().getType("urn:customer", "CustomerInterfaceWithIncorrectGetters");
        assertSame(CustomerInterfaceWithIncorrectGetters.class, type.getInstanceClass());

        DataObject dataObject = helperContext.getDataFactory().create(CustomerInterfaceWithIncorrectGetters.class);
        assertNotNull(dataObject);
        assertTrue(dataObject instanceof CustomerInterfaceWithIncorrectGetters);
    }
}
