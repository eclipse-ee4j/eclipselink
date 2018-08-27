/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.advanced;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.exceptions.ValidationException;
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

    // Stub handler for nonstandard schemes (zip: and wsjar:).
    private static URLStreamHandler dummyHandler =
        new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return null;
            }
        };

    private static void checkPURootSimple(
        String inputUrl,
        String expectedOutput
    ) throws Exception {
        Assert.assertEquals(
            expectedOutput,
            PersistenceUnitProcessor.computePURootURL(
                new URL(inputUrl),
                "META-INF/persistence.xml"
            ).toString()
        );
    }

    private static void checkPURootCustom(
        String inputScheme,
        String inputFile,
        String expectedOutput
    ) throws Exception {
        Assert.assertEquals(
            expectedOutput,
            PersistenceUnitProcessor.computePURootURL(
                new URL(inputScheme, "", -1, inputFile, dummyHandler),
                "META-INF/persistence.xml"
            ).toString()
        );
    }

    private static void checkPURootFailsCustom(
        String inputScheme,
        String inputFile
    ) throws Exception {
        try {
            PersistenceUnitProcessor.computePURootURL(
                new URL(inputScheme, "", -1, inputFile, dummyHandler),
                "META-INF/persistence.xml"
            );
            fail("ValidationException should be thrown.");
        } catch (ValidationException e) {}
    }

    private static void checkPURootFailsSimple(
        String input
    ) throws Exception {
        try {
            PersistenceUnitProcessor.computePURootURL(
                new URL(input),
                "META-INF/persistence.xml"
            );
            fail("ValidationException should be thrown.");
        } catch (ValidationException e) {}
    }

    public void testComputePURootURLForZipFile() throws Exception {

        // Test cases for expected behavior.

        checkPURootCustom(
            "zip", "/foo/bar.jar!/META-INF/persistence.xml",
            "file:/foo/bar.jar"
        );

        // WAR files have a special location available.
        checkPURootCustom(
            "zip", "/foo/bar.war!/WEB-INF/classes/META-INF/persistence.xml",
            "jar:file:/foo/bar.war!/WEB-INF/classes/"
        );

        // Same as the previous one, but not a WAR!
        checkPURootFailsCustom(
            "zip", "/foo/bar.jar!/WEB-INF/classes/META-INF/persistence.xml"
        );

        // META-INF in some other directory (not conforming to JPA spec!).
        checkPURootFailsCustom(
            "zip", "/foo/bar.jar!/foo/META-INF/persistence.xml"
        );

        // Test cases for specific issues.

        // 463571

        checkPURootCustom(
            "zip", "/C:/Oracle/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml",
            "file:/C:/Oracle/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar"
        );

        checkPURootCustom(
            "zip", "/C:/Program Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml",
            "file:/C:/Program Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar"
        );

        checkPURootCustom(
            "zip", "/C:/Program.Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml",
            "file:/C:/Program.Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar"
        );
    }

    public void testComputePURootURLForJarFile() throws Exception {
        // Test cases for expected behavior.

        checkPURootSimple(
            "jar:file:/foo/bar.jar!/META-INF/persistence.xml",
            "file:/foo/bar.jar"
        );

        checkPURootSimple(
            "jar:file:/foo/bar.war!/WEB-INF/classes/META-INF/persistence.xml",
            "jar:file:/foo/bar.war!/WEB-INF/classes/"
        );

        checkPURootFailsSimple(
            "jar:file:/foo/bar.jar!/WEB-INF/classes/META-INF/persistence.xml"
        );

        checkPURootFailsSimple(
            "jar:file:/foo/bar.jar!/foo/META-INF/persistence.xml"
        );
    }

    public void testComputePURootURLForWsjarFile() throws Exception {
        // Test cases for expected behavior.
        // The results differ slightly from the other archive URLs!

        checkPURootCustom(
            "wsjar", "file:/foo/bar.jar!/META-INF/persistence.xml",
            "jar:file:/foo/bar.jar!/"
        );

        checkPURootCustom(
            "wsjar", "file:/foo/bar.war!/WEB-INF/classes/META-INF/persistence.xml",
            "jar:file:/foo/bar.war!/WEB-INF/classes/"
        );

        checkPURootFailsCustom(
            "wsjar", "file:/foo/bar.jar!/WEB-INF/classes/META-INF/persistence.xml"
        );

        checkPURootFailsCustom(
            "wsjar", "file:/foo/bar.jar!/foo/META-INF/persistence.xml"
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
