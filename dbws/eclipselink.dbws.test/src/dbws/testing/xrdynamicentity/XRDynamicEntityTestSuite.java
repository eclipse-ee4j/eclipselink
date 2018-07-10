/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - May 2008, created DBWS test package
package dbws.testing.xrdynamicentity;

//javase imports
import java.util.HashSet;
import java.util.Set;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.internal.xr.XRDynamicPropertiesManager;
import org.eclipse.persistence.internal.xr.XRClassWriter;
import org.eclipse.persistence.internal.xr.XRDynamicClassLoader;
import org.eclipse.persistence.internal.xr.XRDynamicEntity;

public class XRDynamicEntityTestSuite {

    static final String PACKAGE_PREFIX =
        XRDynamicEntityTestSuite.class.getPackage().getName();
    static final String TEST_CLASSNAME = PACKAGE_PREFIX + ".TestClass";
    static final String FIELD_1 = "field1";
    static final String FIELD_2 = "field2";
    static final String TEST_STRING = "this is a test";

    //test fixtures
    static XRDynamicEntity entity1 = null;
    @BeforeClass
    public static void setUp() throws NoSuchFieldException, IllegalArgumentException,
        IllegalAccessException {
        Set<String> propertyNames = new HashSet<String>();
        propertyNames.add(FIELD_1);
        propertyNames.add(FIELD_2);
        XRCustomer.DPM.setPropertyNames(propertyNames);
        entity1 = new XRCustomer();
    }

    @Test
    public void nullParent() throws Exception {
        XRDynamicClassLoader xrdcl = new XRDynamicClassLoader(null);
        assertNull(xrdcl.getParent());
    }

    @Test
    public void defaultWriter() throws Exception {
        XRDynamicClassLoader xrdcl = new XRDynamicClassLoader(null);
        assertEquals(XRClassWriter.class, xrdcl.getDefaultWriter().getClass());
    }

    @Test(expected=IllegalArgumentException.class)
    public void coreClass() throws ClassNotFoundException {
        XRDynamicClassLoader xrdcl = new XRDynamicClassLoader(null);
        xrdcl.createDynamicClass("java.lang.String");
    }

    @Test
    public void buildTestClass() throws ClassNotFoundException {
        //Needs non-null parent ClassLoader for createDynamicClass to work
        XRDynamicClassLoader xrdcl =
            new XRDynamicClassLoader(XRDynamicEntityTestSuite.class.getClassLoader());
        Class<?> testClass = xrdcl.createDynamicClass(TEST_CLASSNAME);
        assertEquals("test class wrong name", testClass.getName(), TEST_CLASSNAME);
        assertTrue("test class not assignableFrom DynamicEntity",
            DynamicEntity.class.isAssignableFrom(testClass));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void buildTestEntity() throws InstantiationException, IllegalAccessException,
        NoSuchFieldException {
        XRDynamicClassLoader xrdcl =
            new XRDynamicClassLoader(XRDynamicEntityTestSuite.class.getClassLoader());
        Class<XRDynamicEntity> testClass =
            (Class<XRDynamicEntity>)xrdcl.createDynamicClass(TEST_CLASSNAME);
        XRDynamicEntity newInstance = testClass.newInstance();
        XRDynamicPropertiesManager xrDPM = newInstance.fetchPropertiesManager();
        Set<String> propertyNames = new HashSet<String>();
        propertyNames.add(FIELD_1);
        propertyNames.add(FIELD_2);
        xrDPM.setPropertyNames(propertyNames);
        //build instance
        XRDynamicEntity newInstance2 = testClass.newInstance();
        assertNotNull(newInstance2);
    }

    @Test
    public void createTestClassTwice() throws Exception {
        XRDynamicClassLoader xrdcl =
            new XRDynamicClassLoader(XRDynamicEntityTestSuite.class.getClassLoader());
        Class<?> dynamicClass = xrdcl.createDynamicClass(TEST_CLASSNAME);
        assertNotNull(dynamicClass);
        assertEquals(TEST_CLASSNAME, dynamicClass.getName());
        Class<?> dynamicClass2 = xrdcl.createDynamicClass(TEST_CLASSNAME);
        assertSame(dynamicClass, dynamicClass2);
    }

    @Test
    public void testEntityOps() {
        // test #1
        Object field = entity1.get(FIELD_1);
        assertNull(FIELD_1 + " should be null", field);
        assertFalse(FIELD_2 + " shouldn't be set", entity1.isSet(FIELD_2));
        // test #2
        DynamicEntity e = entity1.set(FIELD_1, TEST_STRING);
        assertSame(e, entity1);
        e = entity1.set(FIELD_2, Integer.valueOf(17));
        assertSame(e, entity1);
        // test #3
        String test = entity1.<String>get(FIELD_1);
        assertEquals(FIELD_1 + " incorrect value", test, TEST_STRING);
        Integer i = entity1.<Integer>get(FIELD_2);
        assertEquals(FIELD_2 + " incorrect value", i, Integer.valueOf(17));
        // test #4
        boolean expectedExceptionOccurred = false;
        try {
            String s = entity1.<String>get("field2");
            System.identityHashCode(s);
        } catch (ClassCastException cce) {
            expectedExceptionOccurred = true;
        }
        assertTrue("The expected ClassCastException did not occur", expectedExceptionOccurred);
        // test #5
        expectedExceptionOccurred = false;
        try {
            entity1.<String>get("field3");
        } catch (DynamicException de) {
            expectedExceptionOccurred = true;
        }
        assertTrue("The expected DynamicException did not occur", expectedExceptionOccurred);
    }
}
