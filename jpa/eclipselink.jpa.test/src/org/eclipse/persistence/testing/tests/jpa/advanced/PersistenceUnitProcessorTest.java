/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.jpa.advanced;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.junit.Assert;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

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
                        new URL("zip", "", -1, "C:/Oracle/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml", dummyZipHandler),
                        "META-INF/persistence.xml"
                ).toString()
        );
        
        Assert.assertEquals(
                "file:/C:/Program Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar", 
                PersistenceUnitProcessor.computePURootURL(
                        new URL("zip", "", -1, "C:/Program Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml", dummyZipHandler), 
                        "META-INF/persistence.xml"
                ).toString()
        );
        
        Assert.assertEquals(
                "file:/C:/Program.Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar", 
                PersistenceUnitProcessor.computePURootURL(
                        new URL("zip", "", -1, "C:/Program.Files/Middleware/Oracle_Home/wlserver/samples/domains/mydomain/servers/myserver/tmp/_WL_user/eclipselink-advanced-model/y986my/eclipselink-advanced-model_ejb.jar!/META-INF/persistence.xml", dummyZipHandler), 
                        "META-INF/persistence.xml"
                ).toString()
        );
    }
    
}
