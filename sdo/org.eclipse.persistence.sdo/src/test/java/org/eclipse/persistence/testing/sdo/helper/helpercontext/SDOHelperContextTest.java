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
//     Oracle - initial API and implementation from Oracle TopLink
/*
   DESCRIPTION
    Test multi HelperContext scenarios in a single JVM, single classloader, single thread.
    Here we are instantiating a dynamic and a static context in the same JVM.
 */

package org.eclipse.persistence.testing.sdo.helper.helpercontext;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import junit.textui.TestRunner;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.identitymaps.CacheIdentityMap;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import commonj.sdo.impl.HelperProviderImpl;
import org.junit.Assert;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationListener;


public class SDOHelperContextTest extends SDOHelperContextTestCases {

    public SDOHelperContextTest(String name) {
        super(name);//, (HelperContext)SDOHelperContext.getInstance());
        aNonStaticHelperContext1 = new SDOHelperContext();
        aNonStaticHelperContext2 = new SDOHelperContext();
        aStaticHelperContext = HelperProviderImpl.getDefaultContext();
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.helpercontext.SDOHelperContextTest" };
        TestRunner.main(arguments);
    }

    private void setUpContext(String xsdPath, String xmlPath, HelperContext aContext) {
        FileInputStream inStream = null;
        // clear defined schemas
        super.setUp();
        try {
            // HelperContext1
            // load in the schema
            String xsdString = getXSDString(xsdPath);

            // Define Types so that processing attributes completes
            aContext.getXSDHelper().define(xsdString);

            // first we set up root data object
            inStream = new FileInputStream(xmlPath);

            //XMLDocument document = xmlHelper.load(inStream);
            //root = (SDODataObject)document.getRootObject();
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("SDOHelperContextTestCases.setup() failed to load DataObject");
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Override
    public void setUp() {
        // define 1st type
        setUpContext(CONTEXT1_DATAOBJECT_XSD_PATH, CONTEXT1_DATAOBJECT_XML_PATH, aNonStaticHelperContext1);
        // define 2nd same type in other context
        setUpContext(CONTEXT2_DATAOBJECT_XSD_PATH, CONTEXT2_DATAOBJECT_XML_PATH, aNonStaticHelperContext2);
        // define 3rd same type in static context
        setUpContext(STATIC_CONTEXT_DATAOBJECT_XSD_PATH, STATIC_CONTEXT_DATAOBJECT_XML_PATH, aStaticHelperContext);
        // unmarshall xml to object
        aNonStaticHelperContext1DataObject = load(CONTEXT1_DATAOBJECT_XML_PATH, aNonStaticHelperContext1);
        aNonStaticHelperContext2DataObject = load(CONTEXT2_DATAOBJECT_XML_PATH, aNonStaticHelperContext2);
        aStaticHelperContextDataObject = load(STATIC_CONTEXT_DATAOBJECT_XML_PATH, aStaticHelperContext);
    }

    public void testHelperContextsAreDistinct() {
        assertNotNull(aNonStaticHelperContext1);
        assertNotNull(aNonStaticHelperContext2);
        assertNotNull(aStaticHelperContext);
        // all are not equal
        assertNotSame(aNonStaticHelperContext1, aNonStaticHelperContext2);
        assertNotSame(aNonStaticHelperContext1, aStaticHelperContext);
        assertNotSame(aNonStaticHelperContext2, aStaticHelperContext);
        // HelperContext
        assertTrue(aNonStaticHelperContext1 instanceof SDOHelperContext);
        assertTrue(aNonStaticHelperContext2 instanceof SDOHelperContext);
        // static DefaultHelperContext
        assertEquals(aStaticHelperContext, HelperProvider.getDefaultContext());
    }

    public void testMultipleInstancesOfContexts() {
        SDOProperty property1 = aNonStaticHelperContext1DataObject.getInstanceProperty("address");
        SDOProperty property2 = aNonStaticHelperContext2DataObject.getInstanceProperty("address");
        SDOProperty property3 = aStaticHelperContextDataObject.getInstanceProperty("address");
        SDOType type1 = aNonStaticHelperContext1DataObject.getInstanceProperty("address").getType();
        SDOType type2 = aNonStaticHelperContext2DataObject.getInstanceProperty("address").getType();
        SDOType type3 = aStaticHelperContextDataObject.getInstanceProperty("address").getType();

        // verify types do not xref
        assertNotSame(type1, type2);
        assertNotSame(type1, type3);
        assertNotSame(type2, type3);

        // modify types in one context
        assertFalse(property1.isMany());
        assertFalse(property2.isMany());
        assertFalse(property3.isMany());
        // change one type from bounded to unbounded
        property1.setMany(true);

        // verify modified types do not affect other context
        assertTrue(property1.isMany());
        assertFalse(property2.isMany());
        assertFalse(property3.isMany());
    }

    /**
     * Test API for helper context alias'
     *
     * Positive test.
     */
    public void testAlias() {
        SDOHelperContext defaultCtx = new SDOHelperContext("defaultContext");
        defaultCtx.setProperty("isDefault", true);
        SDOHelperContext.putHelperContext(defaultCtx);
        assertTrue("No helper context with id [defaultContext] found", SDOHelperContext.hasHelperContext("defaultContext"));
        assertFalse("SDOHelperContext.hasHelperContext('myEnterpriseId') returned true unexpectedly", SDOHelperContext.hasHelperContext("myEnterpriseId"));
        // associate the alias 'myEnterpriseId' with the default context identifier
        SDOHelperContext.addAlias("defaultContext", "myEnterpriseId");
        assertTrue("No helper context found with alias [myEnterpriseId]", SDOHelperContext.hasHelperContext("myEnterpriseId"));
        // at this point, if we look up alias 'myEnterpriseId' we should get the default context
        SDOHelperContext hCtx = (SDOHelperContext) SDOHelperContext.getHelperContext("myEnterpriseId");
        Boolean isDefault = false;
        try {
            isDefault = (Boolean) hCtx.getProperty("isDefault");
        } catch (Exception ex) {
            fail("Attempt to lookup property 'isDefault' failed: " + ex.getMessage());
        }
        assertTrue("Lookup of alias 'myEnterpriseId' did not return the default context as expected", isDefault);
        // now create a new context using the alias as the id
        SDOHelperContext newCtx = new SDOHelperContext("myEnterpriseId");
        newCtx.setProperty("isDefault", false);
        SDOHelperContext.putHelperContext(newCtx);
        assertTrue("Helper context with id [defaultContext] was removed unexpectedly", SDOHelperContext.hasHelperContext("defaultContext"));
        assertTrue("Helper context with id [myEnterpriseId] was not added correctly", SDOHelperContext.hasHelperContext("myEnterpriseId"));
        // make sure the old alias value was removed during the previous put op
        hCtx = (SDOHelperContext) SDOHelperContext.getHelperContext("myEnterpriseId");
        try {
            isDefault = (Boolean) hCtx.getProperty("isDefault");
        } catch (Exception ex) {
            fail("Attempt to lookup property 'isDefault' failed: " + ex.getMessage());
        }
        assertFalse("Lookup of alias 'myEnterpriseId' returned the default context unexpectedly", isDefault);
    }

    @SuppressWarnings("unchecked")
    public void testNotificationListenerWLS() throws Exception {
        final String applicationName = "App1";
        Class<? extends NotificationListener> listenerClass = (Class<? extends NotificationListener>) Class.forName("org.eclipse.persistence.sdo.helper.SDOHelperContext$MyNotificationListener");
        Constructor<? extends NotificationListener> constructor = listenerClass.getDeclaredConstructor(String.class, int.class);
        Field helperContextsField = SDOHelperContext.class.getDeclaredField("helperContexts");
        Field appNameToClassLoaderMapField = SDOHelperContext.class.getDeclaredField("appNameToClassLoaderMap");
        ConcurrentHashMap<String, HelperContext> contextMap = new ConcurrentHashMap<>();

        constructor.setAccessible(true);
        helperContextsField.setAccessible(true);
        appNameToClassLoaderMapField.setAccessible(true);
        try {
            CacheIdentityMap helperContexts = (CacheIdentityMap) helperContextsField.get(SDOHelperContext.class);
            ConcurrentHashMap appNameToClassLoaderMap = (ConcurrentHashMap) appNameToClassLoaderMapField.get(SDOHelperContext.class);
            final Integer originalHelperContextsSize = helperContexts.getSize();
            final Integer originalAppNameToClassLoaderMapSize = appNameToClassLoaderMap.size();

            ClassLoader classLoader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
            helperContexts.put(applicationName, contextMap, false, 0);
            helperContexts.put(classLoader, contextMap, false, 0);
            appNameToClassLoaderMap.put(applicationName, classLoader);

            Assert.assertTrue("App1 entry was not added to helperContexts map", helperContexts.containsKey(applicationName));
            Assert.assertTrue("ClassLoader entry was not added to helperContexts map", helperContexts.containsKey(classLoader));
            Assert.assertTrue("App1 entry was not added to appNameToClassLoaderMapContains map", appNameToClassLoaderMap.containsKey(applicationName));

            NotificationListener listener = constructor.newInstance(applicationName, 0);
            Notification notification = new AttributeChangeNotification(
                    new Object(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    "Some message",
                    "ActiveVersionState",
                    "Some type",
                    2, // Old value
                    0  // New value
            );
            listener.handleNotification(notification, new Object());

            Assert.assertFalse("App1 entry was not added to helperContexts map", helperContexts.containsKey(applicationName));
            Assert.assertFalse("ClassLoader entry was not added to helperContexts map", helperContexts.containsKey(classLoader));
            Assert.assertFalse("App1 entry was not added to appNameToClassLoaderMapContains map", appNameToClassLoaderMap.containsKey(applicationName));
            Assert.assertEquals("helperContexts map size not restored to original",
                    originalHelperContextsSize.intValue(), helperContexts.getSize());
            Assert.assertEquals("appNameToClassLoaderMap size not restored to original",
                    originalAppNameToClassLoaderMapSize.intValue(), appNameToClassLoaderMap.size());
        }
        finally {
            constructor.setAccessible(false);
            helperContextsField.setAccessible(false);
            appNameToClassLoaderMapField.setAccessible(false);
        }
    }

/*
this    SDOHelperContextTest  (id=17)
    aHelperContext    SDOHelperContext  (id=30)
        copyHelper    SDOCopyHelper  (id=55)
        dataFactory    SDODataFactory  (id=42)
        dataHelper    SDODataHelper  (id=45)
        equalityHelper    SDOEqualityHelper  (id=48)
        typeHelper    SDOTypeHelper  (id=50)
        xmlHelper    SDOXMLHelper  (id=60)
        xsdHelper    SDOXSDHelper  (id=52)
    aNonStaticHelperContext1    SDOHelperContext  (id=28)
        copyHelper    SDOCopyHelper  (id=69)
        dataFactory    SDODataFactory  (id=63)
        dataHelper    SDODataHelper  (id=64)
        equalityHelper    SDOEqualityHelper  (id=65)
        typeHelper    SDOTypeHelper  (id=66)
        xmlHelper    SDOXMLHelper  (id=67)
        xsdHelper    SDOXSDHelper  (id=68)
    aNonStaticHelperContext2    SDOHelperContext  (id=32)
        copyHelper    SDOCopyHelper  (id=76)
        dataFactory    SDODataFactory  (id=70)
        dataHelper    SDODataHelper  (id=71)
        equalityHelper    SDOEqualityHelper  (id=72)
        typeHelper    SDOTypeHelper  (id=73)
        xmlHelper    SDOXMLHelper  (id=74)
        xsdHelper    SDOXSDHelper  (id=75)
    aStaticHelperContext    HelperProvider$DefaultHelperContext  (id=33)
 */
}
