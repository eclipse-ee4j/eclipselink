/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.jpa22.sessionbean;

import java.util.Arrays;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa22.attributeconverter.AttributeConverterTableCreator;
import org.eclipse.persistence.testing.models.jpa22.sessionbean.InjectionTest;

public class AttributeConverterInjectionTest extends JUnitTestCase {

    protected InjectionTest attributeConverterTest;

    public AttributeConverterInjectionTest(){
        super();
    }

    public AttributeConverterInjectionTest(String name){
        super(name);
    }

    public AttributeConverterInjectionTest(String name, boolean shouldRunTestOnServer){
        super(name);
        this.shouldRunTestOnServer = shouldRunTestOnServer;
    }

    private static final String[] LOOKUP_STRINGS = new String[] {

    // WLS
    "java:global/eclipselink-jpa22-sessionbean-model/eclipselink-jpa22-sessionbean-model_ejb/AttributeConverterTestBean",
    // WAS
    "org.eclipse.persistence.testing.models.jpa22.sessionbean.AttributeConverterTest",
    // jboss
    "eclipselink-jpa22-sessionbean-model/EntityListenerTestBean/remote-org.eclipse.persistence.testing.models.jpa22.sessionbean.AttributeConverterTest",
    // NetWeaver
    "JavaEE/servertest/REMOTE/EntityListenerTestBean/org.eclipse.persistence.testing.models.jpa22.sessionbean.AttributeConverterTest" };

    public InjectionTest getAttributeConverterTest() throws Exception {
        if (attributeConverterTest != null) {
            return attributeConverterTest;
        }

        Properties properties = new Properties();
        String url = System.getProperty("server.url");
        if (url != null) {
            properties.put("java.naming.provider.url", url);
        }
        Context context = new InitialContext(properties);

        for (String candidate : LOOKUP_STRINGS) {
            try {
                attributeConverterTest = (InjectionTest) PortableRemoteObject.narrow(context.lookup(candidate), InjectionTest.class);
                return attributeConverterTest;
            } catch (NamingException namingException) {
                // OK, try next
            }
        }

        throw new RuntimeException("AttributeConverterTest bean could not be looked up under any of the following names:\n" + Arrays.asList(LOOKUP_STRINGS));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("AttributeConverterInjectionTests");
        suite.addTest(new AttributeConverterInjectionTest("testInjection", true));
        suite.addTest(new AttributeConverterInjectionTest("testPreDestroy", true));

        return suite;
    }

    public void testInjection() {
        new AttributeConverterTableCreator().replaceTables(JUnitTestCase.getServerSession("jpa22-sessionbean"));

        try{
            assertTrue("Injection was not triggered.", getAttributeConverterTest().triggerInjection());
        } catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown testing injection " + e);
        }
    }

    public void testPreDestroy(){
        try{
            assertTrue("Predestroy was not triggered.", getAttributeConverterTest().triggerPreDestroy());
        } catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown testing injection clean up " + e);
        }
    }
}
