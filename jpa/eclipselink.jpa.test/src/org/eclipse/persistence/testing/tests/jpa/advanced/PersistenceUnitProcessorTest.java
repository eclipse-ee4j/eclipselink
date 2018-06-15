/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.tests.jpa.advanced;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.internal.jpa.deployment.ArchiveFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.jpa.ArchiveFactory;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.junit.Assert;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

public class PersistenceUnitProcessorTest extends JUnitTestCase {
    
    public static Test suite() {
        return new TestSuite(PersistenceUnitProcessorTest.class);
    }

    public static void testComputePURootURLForZipFile() throws Exception {
        // Required for allowing usage of "zip" URL protocol
        URLStreamHandler dummyZipHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return null;
            }
        };

        Assert.assertEquals(
                "file:/C:/Oracle/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar",
                PersistenceUnitProcessor.computePURootURL(
                        new URL("zip", "", -1, "/C:/Oracle/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml", dummyZipHandler),
                        "META-INF/persistence.xml"
                ).toString()
        );
        
        Assert.assertEquals(
                "file:/C:/Program Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar", 
                PersistenceUnitProcessor.computePURootURL(
                        new URL("zip", "", -1, "/C:/Program Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml", dummyZipHandler), 
                        "META-INF/persistence.xml"
                ).toString()
        );
        
        Assert.assertEquals(
                "file:/C:/Program.Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar", 
                PersistenceUnitProcessor.computePURootURL(
                        new URL("zip", "", -1, "/C:/Program.Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml", dummyZipHandler), 
                        "META-INF/persistence.xml"
                ).toString()
        );
    }

    public void testGetArchiveFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(SystemProperties.ARCHIVE_FACTORY, AF1.class.getName());
        ArchiveFactory af = PersistenceUnitProcessor.getArchiveFactory(PersistenceUnitProcessorTest.class.getClassLoader(), props);
        Assert.assertTrue("Property should be used", af instanceof AF1);
    }

    public void testGetArchiveFactoryOverride() {
        EntityManagerFactory emf = null;
        String orig = System.getProperty(SystemProperties.ARCHIVE_FACTORY, "--noval--");
        try {
            System.setProperty(SystemProperties.ARCHIVE_FACTORY, AF2.class.getName());
            Map<String, Object> props = new HashMap<>();
            props.put(SystemProperties.ARCHIVE_FACTORY, AF1.class.getName());
            ArchiveFactory af = PersistenceUnitProcessor.getArchiveFactory(PersistenceUnitProcessorTest.class.getClassLoader(), props);
            Assert.assertTrue("System property should be used", af instanceof AF2);
        } finally {
            if ("--noval--".equals(orig)) {
                System.clearProperty(SystemProperties.ARCHIVE_FACTORY);
            } else {
                System.setProperty(SystemProperties.ARCHIVE_FACTORY, orig);
            }
        }
    }

    public static class AF1 extends ArchiveFactoryImpl {}
    public static class AF2 extends ArchiveFactoryImpl {}

}
