/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.deployment;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JPAInitializerFetchDefaultsTest {

    @ParameterizedTest
    @EnumSource(value = FetchType.class, names = {"LAZY", "EAGER"})
    void oppositeDefaultsSelectIndependentSnapshots(FetchType fetchType) throws Exception {
        JPAInitializer initializer = initializer();
        URL root = URI.create("file:/fetch-defaults/").toURL();
        PersistenceConfiguration configuration = new PersistenceConfiguration("defaults").defaultToOneFetchType(fetchType);
        SEPersistenceUnitInfo first = initializer.customPersistenceUnitInfo(configuration, root);
        String firstIdentity = initializer.createUniquePersistenceUnitName(first);
        FetchType opposite = fetchType == FetchType.LAZY ? FetchType.EAGER : FetchType.LAZY;
        configuration.defaultToOneFetchType(opposite);
        SEPersistenceUnitInfo second = initializer.customPersistenceUnitInfo(configuration, root);
        assertNotSame(first, second);
        assertEquals(fetchType, first.getDefaultToOneFetchType());
        assertEquals(opposite, second.getDefaultToOneFetchType());
        assertEquals(firstIdentity, initializer.createUniquePersistenceUnitName(first));
        assertNotEquals(firstIdentity, initializer.createUniquePersistenceUnitName(second));
        assertSame(second, initializer.customPersistenceUnitInfo(configuration, root));
        configuration.defaultToOneFetchType(fetchType);
        assertEquals(firstIdentity, initializer.createUniquePersistenceUnitName(initializer.customPersistenceUnitInfo(configuration, root)));
    }

    @Test
    void sameEffectiveDefaultReusesSnapshot() {
        JPAInitializer initializer = initializer();
        SEPersistenceUnitInfo first = initializer.customPersistenceUnitInfo(new PersistenceConfiguration("defaults"), null);
        assertSame(first, initializer.customPersistenceUnitInfo(
                new PersistenceConfiguration("defaults").defaultToOneFetchType(FetchType.EAGER), null));
    }

    @Test
    void xmlNameCannotBeUsedProgrammatically() {
        JPAInitializer initializer = initializer();
        initializer.initialPuInfos = new HashMap<>();
        initializer.initialPuInfos.put("defaults", new SEPersistenceUnitInfo());
        assertThrows(PersistenceException.class, () -> initializer.customPersistenceUnitInfo(
                new PersistenceConfiguration("defaults").defaultToOneFetchType(FetchType.LAZY), null));
    }

    @Test
    void programmaticNameCannotBeUsedForXmlLookup() {
        JPAInitializer initializer = initializer();
        initializer.customPersistenceUnitInfo(new PersistenceConfiguration("defaults"), null);
        assertThrows(PersistenceException.class, () -> initializer.findPersistenceUnitInfo("defaults", Map.of()));
    }

    @ParameterizedTest
    @EnumSource(value = FetchType.class, names = {"LAZY", "EAGER"})
    void programmaticIdentityIncludesConfigurationWithAgent(FetchType fetchType) {
        JPAInitializer initializer = nameOnlyInitializer(true);
        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo(
                new PersistenceConfiguration("defaults").defaultToOneFetchType(fetchType), null);
        assertTrue(initializer.isPersistenceUnitUniquelyDefinedByName());
        assertFalse(initializer.isPersistenceUnitUniquelyDefinedByName(info));
    }

    @Test
    void xmlIdentityRetainsAgentNameOnlyBehavior() {
        assertTrue(nameOnlyInitializer(true).isPersistenceUnitUniquelyDefinedByName(new SEPersistenceUnitInfo()));
    }

    @Test
    void nonAgentIdentityRemainsConfigurationSensitive() {
        JPAInitializer initializer = nameOnlyInitializer(false);
        assertFalse(initializer.isPersistenceUnitUniquelyDefinedByName(new SEPersistenceUnitInfo()));
        assertFalse(initializer.isPersistenceUnitUniquelyDefinedByName(
                new SEPersistenceUnitInfo(new PersistenceConfiguration("defaults"), null)));
    }

    private static JPAInitializer nameOnlyInitializer(boolean nameOnly) {
        return new JavaSECMPInitializer() {
            @Override
            public boolean isPersistenceUnitUniquelyDefinedByName() {
                return nameOnly;
            }
        };
    }

    private static JPAInitializer initializer() {
        return new JavaSECMPInitializer() {
            @Override
            protected SEPersistenceUnitInfo findPersistenceUnitInfoInArchives(String name, Map<?, ?> properties) {
                return null;
            }
        };
    }
}
