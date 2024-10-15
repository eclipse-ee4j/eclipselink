/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa;

import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.ProviderUtil;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PersistenceProviderTest {

    @Test
    void testCheckForProviderPropertyWithoutProperty() {
        Map<String, Object> properties = Map.of();
        boolean result = PersistenceProvider.checkForProviderProperty(properties);
        assertTrue(result);
    }

    @Test
    void testCheckForProviderPropertyWithProviderClass() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, PersistenceProvider.class
        );
        boolean result = PersistenceProvider.checkForProviderProperty(properties);
        assertTrue(result);
    }

    @Test
    void testCheckForProviderPropertyWithProviderString() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, PersistenceProvider.class.getName()
        );
        boolean result = PersistenceProvider.checkForProviderProperty(properties);
        assertTrue(result);
    }

    @Test
    void testCheckForProviderPropertyWithEMFProviderClass() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, EntityManagerFactoryProvider.class
        );
        boolean result = PersistenceProvider.checkForProviderProperty(properties);
        assertTrue(result);
    }

    @Test
    void testCheckForProviderPropertyWithEMFProviderString() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, EntityManagerFactoryProvider.class.getName()
        );
        boolean result = PersistenceProvider.checkForProviderProperty(properties);
        assertTrue(result);
    }

    @Test
    void testCheckForProviderPropertyWithUnsupportedClass() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, Unsupported.class
        );
        boolean result = PersistenceProvider.checkForProviderProperty(properties);
        assertFalse(result);
    }

    @Test
    void testCheckForProviderPropertyWithUnsupportedString() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, Unsupported.class.getName()
        );
        boolean result = PersistenceProvider.checkForProviderProperty(properties);
        assertFalse(result);
    }

    @Test
    void testIsProviderEclipseLinkWithoutProvider() {
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertTrue(result);
    }

    @Test
    void testIsProviderEclipseLinkWithProvider() {
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        configuration.provider(PersistenceProvider.class.getName());
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertTrue(result);
    }

    @Test
    void testIsProviderEclipseLinkWithEMFProvider() {
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        configuration.provider(EntityManagerFactoryProvider.class.getName());
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertTrue(result);
    }

    @Test
    void testIsProviderEclipseLinkWithUnsupportedProvider() {
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        configuration.provider(Unsupported.class.getName());
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertFalse(result);
    }

    @Test
    void testIsProviderEclipseLinkWithProviderStringProperty() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, PersistenceProvider.class.getName()
        );
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        configuration.properties(properties);
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertTrue(result);
    }

    @Test
    void testIsProviderEclipseLinkWithEMFProviderStringProperty() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, EntityManagerFactoryProvider.class.getName()
        );
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        configuration.properties(properties);
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertTrue(result);
    }

    @Test
    void testIsProviderEclipseLinkWithUnsupportedProviderStringProperty() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, Unsupported.class.getName()
        );
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        configuration.properties(properties);
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertFalse(result);
    }

    @Test
    void testIsProviderEclipseLinkWithProviderClassProperty() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, PersistenceProvider.class
        );
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        configuration.properties(properties);
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertTrue(result);
    }

    @Test
    void testIsProviderEclipseLinkWithEMFProviderClassProperty() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, EntityManagerFactoryProvider.class
        );
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        configuration.properties(properties);
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertTrue(result);
    }

    @Test
    void testIsProviderEclipseLinkWithUnsupportedProviderClassProperty() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, Unsupported.class
        );
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        configuration.properties(properties);
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertFalse(result);
    }


    @Test
    void testIsProviderEclipseLinkWithProviderStringPropertyOverride() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, PersistenceProvider.class.getName()
        );
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        // Provider is set as unsupported, but supported property must override it
        configuration.provider(Unsupported.class.getName());
        configuration.properties(properties);
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertTrue(result);
    }

    @Test
    void testIsProviderEclipseLinkWithEMFProviderStringPropertyOverride() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, EntityManagerFactoryProvider.class.getName()
        );
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        // Provider is set as unsupported, but supported property must override it
        configuration.provider(Unsupported.class.getName());
        configuration.properties(properties);
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertTrue(result);
    }

    @Test
    void testIsProviderEclipseLinkWithUnsupportedProviderStringPropertyOverride() {
        Map<String, Object> properties = Map.of(
                PersistenceUnitProperties.PROVIDER, Unsupported.class.getName()
        );
        PersistenceConfiguration configuration = new PersistenceConfiguration("Test");
        // Provider is set as supported, but unsupported property must override it
        configuration.provider(PersistenceProvider.class.getName());
        configuration.properties(properties);
        boolean result = PersistenceProvider.isProviderEclipseLink(configuration);
        assertFalse(result);
    }

    // PersistenceProvider that shall fail the check
    private static final class Unsupported implements jakarta.persistence.spi.PersistenceProvider {

        @Override
        public EntityManagerFactory createEntityManagerFactory(String emName, Map<?, ?> map) {
            return null;
        }

        @Override
        public EntityManagerFactory createEntityManagerFactory(PersistenceConfiguration configuration) {
            return null;
        }

        @Override
        public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map<?, ?> map) {
            return null;
        }

        @Override
        public void generateSchema(PersistenceUnitInfo info, Map<?, ?> map) {

        }

        @Override
        public boolean generateSchema(String persistenceUnitName, Map<?, ?> map) {
            return false;
        }

        @Override
        public ProviderUtil getProviderUtil() {
            return null;
        }

    }

}
