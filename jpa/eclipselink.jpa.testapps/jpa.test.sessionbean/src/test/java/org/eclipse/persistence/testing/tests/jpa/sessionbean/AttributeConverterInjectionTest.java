/*
 * Copyright (c) 2018, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.jpa.sessionbean;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.attributeconverter.AttributeConverterTableCreator;
import org.eclipse.persistence.testing.models.jpa.sessionbean.InjectionTest;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Arrays;
import java.util.Properties;

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
    "java:global/org.eclipse.persistence.jpa.testapps.sessionbean/org.eclipse.persistence.jpa.testapps.sessionbean_ejb/AttributeConverterTestBean",
    // WAS
    "org.eclipse.persistence.testing.models.jpa.sessionbean.AttributeConverterTest",
    // jboss
    "org.eclipse.persistence.jpa.testapps.sessionbean/EntityListenerTestBean/remote-org.eclipse.persistence.testing.models.jpa.sessionbean.AttributeConverterTest",
    // NetWeaver
    "JavaEE/servertest/REMOTE/EntityListenerTestBean/org.eclipse.persistence.testing.models.jpa.sessionbean.AttributeConverterTest" };

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
                attributeConverterTest = (InjectionTest) context.lookup(candidate);
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
        new AttributeConverterTableCreator().replaceTables(JUnitTestCase.getServerSession("jpa-sessionbean"));

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
