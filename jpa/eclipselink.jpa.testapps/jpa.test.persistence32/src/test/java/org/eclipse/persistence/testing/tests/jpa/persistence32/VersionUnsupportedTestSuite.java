/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;

import java.util.Properties;

/**
 * Verify jakarta.persistence 3.2 @Version attribute types (unsupported type to get exception)
 */
public class VersionUnsupportedTestSuite extends JUnitTestCase {

    public VersionUnsupportedTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("VersionUnsupportedTestSuite");
        suite.addTest(new VersionUnsupportedTestSuite("testPersistUnsupportedVersionTypeEntity"));
        return suite;
    }


    @Override
    public String getPersistenceUnitName() {
        return "persistence32_unsupported_version_type";
    }

    public void testPersistUnsupportedVersionTypeEntity() {
        //Special persistence.xml file is needed due a EclipseLink JPA agent
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence-unsupported-version-type.xml");
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory(getPersistenceUnitName(), properties)){
            fail("Attempt to continue with unsupported version type shall fail during EntityManagerFactory creation.");
        } catch (Exception ex) {
            Throwable cause = ex;
            while(cause.getCause() != null && cause.getCause() != cause) {
                cause = cause.getCause();
                if (cause instanceof ValidationException) {
                    assertTrue(cause.getMessage().contains("is not valid for a version property"));
                    return;
                }
            }
            fail("Attempt to continue with unsupported version type shall fail as ValidationException was not detected.");
        }
    }
}
