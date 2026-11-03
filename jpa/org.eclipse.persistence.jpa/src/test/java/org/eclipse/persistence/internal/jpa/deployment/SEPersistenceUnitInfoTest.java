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
import jakarta.persistence.SchemaManagementAction;
import jakarta.persistence.spi.PersistenceUnitInfo;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    void oldXmlVersionsDoNotDisableModernPackageLists() {
        for (String version : List.of("2.2", "3.1")) {
            SEPersistenceUnitInfo ordinary = new SEPersistenceUnitInfo();
            ordinary.setPersistenceXMLSchemaVersion(version);
            ordinary.setExcludeUnlistedClasses(true);
            ordinary.addManagedPackageDescriptor("example.explicit");
            assertEquals(java.util.Set.of("example.explicit"), PersistenceUnitProcessor.getPackageDescriptors(ordinary));
            assertEquals(java.util.Set.of("example.explicit"), PersistenceUnitProcessor.getPackageDescriptors(
                    ordinary, null, java.util.Map.of()));

            SEPersistenceUnitInfo container = new SEPersistenceUnitInfo() {
                @Override
                public List<String> getAllPackageDescriptors() {
                    return List.of("example.explicit", "example.container");
                }
            };
            container.setPersistenceXMLSchemaVersion(version);
            container.addManagedPackageDescriptor("example.explicit");
            assertEquals(List.of("example.explicit", "example.container"),
                    List.copyOf(PersistenceUnitProcessor.getPackageDescriptors(container)));
            assertEquals(List.of("example.explicit", "example.container"),
                    List.copyOf(PersistenceUnitProcessor.getPackageDescriptors(container, null, java.util.Map.of())));
        }
    }

    @Test
    void modernProxyPackageListsAreHonoredWithOldXmlVersion() {
        List<String> calls = new ArrayList<>();
        PersistenceUnitInfo container = (PersistenceUnitInfo) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[] {PersistenceUnitInfo.class}, (proxy, method, arguments) -> {
                    switch (method.getName()) {
                        case "getPersistenceXMLSchemaVersion":
                            return "2.2";
                        case "getManagedPackageDescriptors":
                            calls.add(method.getName());
                            return List.of("example.explicit", "example.shared");
                        case "getAllPackageDescriptors":
                            calls.add(method.getName());
                            return List.of("example.shared", "example.discovered");
                        default:
                            throw new AssertionError("Unexpected SPI method: " + method.getName());
                    }
                });
        assertEquals("2.2", container.getPersistenceXMLSchemaVersion());
        assertEquals(List.of("example.explicit", "example.shared", "example.discovered"),
                List.copyOf(PersistenceUnitProcessor.getPackageDescriptors(container)));
        assertEquals(List.of("example.explicit", "example.shared", "example.discovered"),
                List.copyOf(PersistenceUnitProcessor.getPackageDescriptors(container, null, java.util.Map.of())));
        assertEquals(List.of("getManagedPackageDescriptors", "getAllPackageDescriptors",
                "getManagedPackageDescriptors", "getAllPackageDescriptors"), calls);
    }

    @Test
    void proxyPackageListsRemainIndependentWhenOneGetterIsNullOrAbsent() {
        for (String unavailable : List.of("getManagedPackageDescriptors", "getAllPackageDescriptors")) {
            for (boolean absentSpi : List.of(false, true)) {
                PersistenceUnitInfo container = (PersistenceUnitInfo) Proxy.newProxyInstance(
                        getClass().getClassLoader(), new Class<?>[] {PersistenceUnitInfo.class}, (proxy, method, arguments) -> {
                            if (method.getName().equals("getPersistenceXMLSchemaVersion")) {
                                return "2.2";
                            }
                            if (method.getName().equals(unavailable)) {
                                if (absentSpi) {
                                    throw new AbstractMethodError("Earlier container SPI: " + unavailable);
                                }
                                return null;
                            }
                            if (method.getName().equals("getManagedPackageDescriptors")
                                    || method.getName().equals("getAllPackageDescriptors")) {
                                return List.of("example.available", "example.available");
                            }
                            throw new AssertionError("Unexpected SPI method: " + method.getName());
                        });
                assertEquals(java.util.Set.of("example.available"), PersistenceUnitProcessor.getPackageDescriptors(container));
                assertEquals(java.util.Set.of("example.available"), PersistenceUnitProcessor.getPackageDescriptors(
                        container, null, java.util.Map.of()));
            }
        }
    }

    @Test
    void unrecognizedProxyMissingMethodFailuresRetainTheirIdentity() {
        for (String failingGetter : List.of("getManagedPackageDescriptors", "getAllPackageDescriptors")) {
            NoSuchMethodException missing = new NoSuchMethodException(failingGetter);
            InvocationTargetException delegated = new InvocationTargetException(missing);
            for (Exception failure : List.of(missing, delegated)) {
                PersistenceUnitInfo container = (PersistenceUnitInfo) Proxy.newProxyInstance(
                        getClass().getClassLoader(), new Class<?>[] {PersistenceUnitInfo.class}, (proxy, method, arguments) -> {
                            if (method.getName().equals("getPersistenceXMLSchemaVersion")) {
                                return "2.2";
                            }
                            if (method.getName().equals(failingGetter)) {
                                throw failure;
                            }
                            return List.of("example.available");
                        });
                UndeclaredThrowableException direct = assertThrows(UndeclaredThrowableException.class,
                        () -> PersistenceUnitProcessor.getPackageDescriptors(container));
                assertSame(failure, direct.getUndeclaredThrowable());
                UndeclaredThrowableException bootstrap = assertThrows(UndeclaredThrowableException.class,
                        () -> PersistenceUnitProcessor.getPackageDescriptors(container, null, java.util.Map.of()));
                assertSame(failure, bootstrap.getUndeclaredThrowable());
                assertSame(missing, delegated.getTargetException());
            }
        }
    }

    @Test
    void containerAndProxyDiscoveryFailuresRetainTheirIdentity() {
        for (String failingGetter : List.of("getManagedPackageDescriptors", "getAllPackageDescriptors")) {
            IllegalStateException failure = new IllegalStateException("Container discovery failed: " + failingGetter);
            SEPersistenceUnitInfo ordinary = new SEPersistenceUnitInfo() {
                @Override
                public List<String> getManagedPackageDescriptors() {
                    if (failingGetter.equals("getManagedPackageDescriptors")) {
                        throw failure;
                    }
                    return List.of("example.explicit");
                }

                @Override
                public List<String> getAllPackageDescriptors() {
                    if (failingGetter.equals("getAllPackageDescriptors")) {
                        throw failure;
                    }
                    return List.of("example.container");
                }
            };
            ordinary.setPersistenceXMLSchemaVersion("2.2");
            PersistenceUnitInfo proxyContainer = (PersistenceUnitInfo) Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class<?>[] {PersistenceUnitInfo.class}, (proxy, method, arguments) -> {
                        if (method.getName().equals("getPersistenceXMLSchemaVersion")) {
                            return "2.2";
                        }
                        if (method.getName().equals(failingGetter)) {
                            throw failure;
                        }
                        return List.of("example.available");
                    });
            for (PersistenceUnitInfo container : List.of(ordinary, proxyContainer)) {
                assertSame(failure, assertThrows(IllegalStateException.class,
                        () -> PersistenceUnitProcessor.getPackageDescriptors(container)));
                assertSame(failure, assertThrows(IllegalStateException.class,
                        () -> PersistenceUnitProcessor.getPackageDescriptors(container, null, java.util.Map.of())));
            }
        }
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

    @ParameterizedTest
    @EnumSource(value = SchemaManagementAction.class, names = "NONE", mode = EnumSource.Mode.EXCLUDE)
    void schemaActionsAreSnapshottedAsStandardProperties(SchemaManagementAction action) {
        PersistenceConfiguration configuration = new PersistenceConfiguration("schema-actions")
                .schemaManagementDatabaseAction(action).schemaManagementScriptsAction(action);
        SEPersistenceUnitInfo first = info(configuration);
        String token = action.name().toLowerCase(Locale.ROOT).replace('_', '-');
        assertEquals(token, first.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION));
        assertEquals(token, first.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION));
        assertTrue(configuration.properties().isEmpty(), "Normalizing actions must not modify the caller");
        assertEquals(first.getConfigHash(), info(new PersistenceConfiguration("schema-actions")
                .property(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, token)
                .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, token)).getConfigHash());
        configuration.schemaManagementDatabaseAction(SchemaManagementAction.NONE);
        assertEquals(token, first.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION));
        assertNotEquals(first.getConfigHash(), info(configuration).getConfigHash());
    }

    @Test
    void explicitSchemaPropertiesOverrideActionFieldsAndTheirIdentity() {
        PersistenceConfiguration configuration = new PersistenceConfiguration("schema-actions")
                .schemaManagementDatabaseAction(SchemaManagementAction.CREATE)
                .schemaManagementScriptsAction(SchemaManagementAction.DROP)
                .property(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "none")
                .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, "create");
        SEPersistenceUnitInfo first = info(configuration);
        assertEquals("none", first.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION));
        assertEquals("create", first.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION));
        configuration.schemaManagementDatabaseAction(SchemaManagementAction.POPULATE)
                .schemaManagementScriptsAction(SchemaManagementAction.VALIDATE);
        assertEquals(first.getConfigHash(), info(configuration).getConfigHash());
    }

    @Test
    void defaultNonePreservesAbsentPropertiesAndExplicitNoneRemainsDistinct() {
        SEPersistenceUnitInfo defaults = info(new PersistenceConfiguration("schema-actions"));
        SEPersistenceUnitInfo explicitDefaults = info(new PersistenceConfiguration("schema-actions")
                .schemaManagementDatabaseAction(SchemaManagementAction.NONE)
                .schemaManagementScriptsAction(SchemaManagementAction.NONE));
        assertTrue(defaults.getProperties().isEmpty());
        assertTrue(explicitDefaults.getProperties().isEmpty());
        assertEquals(defaults.getConfigHash(), explicitDefaults.getConfigHash());
        SEPersistenceUnitInfo explicitProperty = info(new PersistenceConfiguration("schema-actions")
                .property(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "none"));
        assertEquals("none", explicitProperty.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION));
        assertNotEquals(defaults.getConfigHash(), explicitProperty.getConfigHash());
    }

    @Test
    void configuredLoaderAndMappingFilesBelongToTheSnapshot() {
        ClassLoader loader = new ClassLoader(getClass().getClassLoader()) { };
        PersistenceConfiguration configuration = new PersistenceConfiguration("schema-mapping")
                .mappingFile("first.xml").property(PersistenceUnitProperties.CLASSLOADER, loader);
        SEPersistenceUnitInfo first = info(configuration);
        configuration.mappingFile("second.xml");
        assertSame(loader, first.getClassLoader());
        assertEquals(List.of("first.xml"), first.getMappingFileNames());
        assertEquals(List.of("first.xml", "second.xml"), info(configuration).getMappingFileNames());
        assertNotEquals(first.getConfigHash(), info(configuration).getConfigHash());
    }

    private static SEPersistenceUnitInfo info(PersistenceConfiguration configuration) {
        return new SEPersistenceUnitInfo(configuration, null);
    }
}
