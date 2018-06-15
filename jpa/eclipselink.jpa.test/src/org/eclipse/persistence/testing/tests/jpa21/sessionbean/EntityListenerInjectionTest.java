/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.jpa21.sessionbean;

import java.util.Arrays;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.entitylistener.EntityListenerTableCreator;
import org.eclipse.persistence.testing.models.jpa21.sessionbean.EntityListenerTest;

public class EntityListenerInjectionTest extends JUnitTestCase {

    protected EntityListenerTest entityListenerTest;

    public EntityListenerInjectionTest(){
        super();
    }

    public EntityListenerInjectionTest(String name){
        super(name);
    }

    public EntityListenerInjectionTest(String name, boolean shouldRunTestOnServer){
        super(name);
        this.shouldRunTestOnServer = shouldRunTestOnServer;
    }

    private static final String[] LOOKUP_STRINGS = new String[] {

    // WLS
    "java:global/eclipselink-jpa21-sessionbean-model/eclipselink-jpa21-sessionbean-model_ejb/EntityListenerTestBean",
    // WAS
    "org.eclipse.persistence.testing.models.jpa21.sessionbean.EntityListenerTest",
    // jboss
    "eclipselink-jpa21-sessionbean-model/EntityListenerTestBean/remote-org.eclipse.persistence.testing.models.jpa21.sessionbean.EntityListenerTest",
    // NetWeaver
    "JavaEE/servertest/REMOTE/EntityListenerTestBean/org.eclipse.persistence.testing.models.jpa21.sessionbean.EntityListenerTest" };

    public EntityListenerTest getEntityListenerTest() throws Exception {
        if (entityListenerTest != null) {
            return entityListenerTest;
        }

        Properties properties = new Properties();
        String url = System.getProperty("server.url");
        if (url != null) {
            properties.put("java.naming.provider.url", url);
        }
        Context context = new InitialContext(properties);

        for (String candidate : LOOKUP_STRINGS) {
            try {
                entityListenerTest = (EntityListenerTest) PortableRemoteObject.narrow(context.lookup(candidate), EntityListenerTest.class);
                return entityListenerTest;
            } catch (NamingException namingException) {
                // OK, try next
            }
        }

        throw new RuntimeException("EntityListenerTest bean could not be looked up under any of the following names:\n" + Arrays.asList(LOOKUP_STRINGS));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("SessionBeanTests");
        suite.addTest(new EntityListenerInjectionTest("testInjection", true));
        suite.addTest(new EntityListenerInjectionTest("testPreDestroy", true));

        return suite;
    }

    public void testInjection() {
        new EntityListenerTableCreator().replaceTables(JUnitTestCase.getServerSession("jpa21-sessionbean"));

        try{
            assertTrue("Injection was not triggered.", getEntityListenerTest().triggerInjection());
        } catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown testing injection " + e);
        }
    }

    public void testPreDestroy(){
        try{
            assertTrue("Predestroy was not triggered.", getEntityListenerTest().triggerPreDestroy());
        } catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown testing injection clean up " + e);
        }
    }
}
