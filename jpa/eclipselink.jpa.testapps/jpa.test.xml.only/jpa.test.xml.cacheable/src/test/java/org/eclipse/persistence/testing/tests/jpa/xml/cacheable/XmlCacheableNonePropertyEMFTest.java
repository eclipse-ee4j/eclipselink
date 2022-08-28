/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.jpa.xml.cacheable;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;

import java.util.HashMap;
import java.util.Map;

public class XmlCacheableNonePropertyEMFTest extends XmlCacheableTestBase {

    public XmlCacheableNonePropertyEMFTest() {
        super();
    }

    public XmlCacheableNonePropertyEMFTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "CacheUnlisted";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("XmlCacheableNonePropertyEMFTest");

        suite.addTest(new XmlCacheableNonePropertyEMFTest("testSetup"));
        suite.addTest(new XmlCacheableNonePropertyEMFTest("testCachingOnNONEPropertyEMF"));
        return suite;
    }

    /**
     * Verifies the cacheable settings when passing the jakarta.persistence.sharedCache.mode property
     * as NONE when creating an EntityManagerFactory.
     */
    public void testCachingOnNONEPropertyEMF() {
        final JpaEntityManagerFactory factory = getEntityManagerFactory().unwrap(JpaEntityManagerFactory.class);
        Map<String, Object> properties = new HashMap<>(factory.getProperties());
        properties.put(PersistenceUnitProperties.SHARED_CACHE_MODE, "NONE");
        factory.refreshMetadata(properties);
        assertCachingOnNONE(factory.getServerSession());
    }

}
