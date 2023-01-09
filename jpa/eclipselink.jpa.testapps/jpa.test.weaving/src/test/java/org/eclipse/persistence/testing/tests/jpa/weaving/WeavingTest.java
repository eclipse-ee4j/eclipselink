/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.weaving;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.tools.weaving.jpa.StaticWeaveProcessor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public class WeavingTest extends JUnitTestCase {

    private final String WEAVING_ENTITY_CLASS = "org.eclipse.persistence.testing.models.jpa.weaving.WeavingEntity";
    private final String SOURCE_JAR = "source.jar";
    private final String TARGET_JAR = "target" + System.getProperty(SystemProperties.ASM_SERVICE, "") + ".jar";

    public WeavingTest() {
        super();
    }

    public WeavingTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("WeavingTest");
        suite.addTest(new WeavingTest("testWeaving"));
        return suite;
    }

    public void testWeaving() throws IOException, URISyntaxException, ClassNotFoundException, NoSuchMethodException {
        URL source =  Thread.currentThread().getContextClassLoader().getResource(SOURCE_JAR);
        URL target = new URL(source.toString().replace(SOURCE_JAR, TARGET_JAR));

        StaticWeaveProcessor staticWeaverProcessor= new StaticWeaveProcessor(source,target);
        staticWeaverProcessor.performWeaving();

        ClassLoader classLoader = new URLClassLoader(new URL[]{target});
        Class weavingEntityClass = classLoader.loadClass(WEAVING_ENTITY_CLASS);
        //Check existence of various methods added to entity by weaving
        assertNotNull(weavingEntityClass.getMethod("_persistence_post_clone"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_shallow_clone"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_getId"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_getCacheKey"));
        assertNotNull(weavingEntityClass.getMethod("_persistence_getPropertyChangeListener"));
    }
}
