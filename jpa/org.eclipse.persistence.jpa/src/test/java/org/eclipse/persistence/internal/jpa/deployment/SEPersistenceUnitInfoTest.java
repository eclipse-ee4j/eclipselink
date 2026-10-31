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

import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SEPersistenceUnitInfoTest {

    @Test
    void packageDescriptorListsAreEmptyWhenAbsent() {
        assertTrue(new SEPersistenceUnitInfo().getManagedPackageDescriptors().isEmpty());
        assertTrue(new SEPersistenceUnitInfo().getAllPackageDescriptors().isEmpty());
        assertTrue(info(new PersistenceConfiguration("packages")).getAllPackageDescriptors().isEmpty());
    }

    @Test
    void packageConfigurationIsSnapshottedAndIncludedInIdentity() {
        PersistenceConfiguration configuration = new PersistenceConfiguration("packages")
                .managedPackageDescriptor("example.first")
                .managedPackageDescriptor("example.first");
        SEPersistenceUnitInfo first = info(configuration);
        String originalHash = first.getConfigHash();
        configuration.managedPackageDescriptor("example.second");
        SEPersistenceUnitInfo second = info(configuration);
        assertEquals("4.0", first.getPersistenceXMLSchemaVersion());
        assertEquals(List.of("example.first"), first.getManagedPackageDescriptors());
        assertEquals(List.of("example.first"), first.getAllPackageDescriptors());
        assertEquals(List.of("example.first", "example.second"), second.getManagedPackageDescriptors());
        assertEquals(originalHash, first.getConfigHash());
        assertNotEquals(first.getConfigHash(), second.getConfigHash());
        assertEquals(first.getConfigHash(), info(new PersistenceConfiguration("packages")
                .managedPackageDescriptor("example.first")).getConfigHash());
        assertTrue(first.getManagedClassNames().isEmpty(), "A package declaration does not register its classes");
    }

    @Test
    void packageSetterCopiesInputAndRetainsExplicitUnannotatedNames() {
        List<String> names = new ArrayList<>(List.of("example.unannotated", "example.unannotated"));
        SEPersistenceUnitInfo unit = new SEPersistenceUnitInfo();
        unit.setManagedPackageDescriptors(names);
        names.add("example.notRegistered");
        unit.addManagedPackageDescriptor("example.unannotated");
        assertEquals(List.of("example.unannotated"), unit.getManagedPackageDescriptors());
        assertEquals(unit.getManagedPackageDescriptors(), unit.getAllPackageDescriptors());
        unit.addManagedPackageDescriptor("example.second");
        assertEquals(List.of("example.unannotated", "example.second"), unit.getAllPackageDescriptors());
    }

    @Test
    void containerCompletePackageListDoesNotRequireExplicitEntries() {
        SEPersistenceUnitInfo container = new SEPersistenceUnitInfo() {
            @Override
            public List<String> getManagedPackageDescriptors() {
                return null;
            }

            @Override
            public List<String> getAllPackageDescriptors() {
                return List.of("example.container", "example.container");
            }
        };
        assertEquals(java.util.Set.of("example.container"), PersistenceUnitProcessor.getPackageDescriptors(container));
        assertEquals(java.util.Set.of("example.container"), PersistenceUnitProcessor.getPackageDescriptors(
                container, null, java.util.Map.of()));
    }

    @Test
    void legacyNullCompleteListDoesNotDiscardExplicitPackages() {
        SEPersistenceUnitInfo container = new SEPersistenceUnitInfo() {
            @Override
            public List<String> getAllPackageDescriptors() {
                return null;
            }
        };
        container.addManagedPackageDescriptor("example.explicit");
        assertEquals(java.util.Set.of("example.explicit"), PersistenceUnitProcessor.getPackageDescriptors(container));
    }

    @Test
    void earlierContainerSpiWithoutPackageMethodsIsTolerated() {
        SEPersistenceUnitInfo container = new SEPersistenceUnitInfo() {
            @Override
            public List<String> getManagedPackageDescriptors() {
                throw new AbstractMethodError("Earlier container SPI");
            }

            @Override
            public List<String> getAllPackageDescriptors() {
                throw new AbstractMethodError("Earlier container SPI");
            }
        };
        assertTrue(PersistenceUnitProcessor.getPackageDescriptors(container).isEmpty());
    }

    @Test
    void absentCompleteSpiDoesNotEraseDeclaredDescriptors() {
        SEPersistenceUnitInfo container = new SEPersistenceUnitInfo() {
            @Override
            public List<String> getAllPackageDescriptors() {
                throw new AbstractMethodError("Complete discovery SPI not implemented");
            }
        };
        container.addManagedPackageDescriptor("example.explicit");
        assertEquals(java.util.Set.of("example.explicit"), PersistenceUnitProcessor.getPackageDescriptors(container));
    }

    @Test
    void modernContainerFailuresAreNotSwallowed() {
        SEPersistenceUnitInfo container = new SEPersistenceUnitInfo() {
            @Override
            public List<String> getAllPackageDescriptors() {
                throw new IllegalStateException("Container discovery failed");
            }
        };
        assertThrows(IllegalStateException.class, () -> PersistenceUnitProcessor.getPackageDescriptors(container));
    }

    @Test
    void omittedDefaultRemainsEager() {
        assertEquals(FetchType.EAGER, new SEPersistenceUnitInfo().getDefaultToOneFetchType());
        assertEquals(FetchType.EAGER, info(new PersistenceConfiguration("defaults")).getDefaultToOneFetchType());
    }

    @ParameterizedTest
    @EnumSource(value = FetchType.class, names = {"LAZY", "EAGER"})
    void configuredDefaultIsSnapshotted(FetchType fetchType) {
        PersistenceConfiguration configuration = new PersistenceConfiguration("defaults").defaultToOneFetchType(fetchType);
        SEPersistenceUnitInfo first = info(configuration);
        String originalHash = first.getConfigHash();
        FetchType opposite = fetchType == FetchType.LAZY ? FetchType.EAGER : FetchType.LAZY;
        configuration.defaultToOneFetchType(opposite);
        SEPersistenceUnitInfo second = info(configuration);
        assertEquals(fetchType, first.getDefaultToOneFetchType());
        assertEquals(opposite, second.getDefaultToOneFetchType());
        assertEquals(originalHash, first.getConfigHash());
        assertNotEquals(first.getConfigHash(), second.getConfigHash());
    }

    @Test
    void omittedAndExplicitEagerHaveSameIdentity() {
        assertEquals(info(new PersistenceConfiguration("defaults")).getConfigHash(),
                info(new PersistenceConfiguration("defaults").defaultToOneFetchType(FetchType.EAGER)).getConfigHash());
    }

    private static SEPersistenceUnitInfo info(PersistenceConfiguration configuration) {
        return new SEPersistenceUnitInfo(configuration, null);
    }
}
