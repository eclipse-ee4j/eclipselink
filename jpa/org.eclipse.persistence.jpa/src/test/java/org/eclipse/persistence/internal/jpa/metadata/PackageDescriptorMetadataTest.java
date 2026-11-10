/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.internal.jpa.metadata;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.PackageAccessor;
import org.eclipse.persistence.internal.jpa.metadata.packagedescriptor.PackageDescriptorEntity;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackageDescriptorMetadataTest {
    private static final String PACKAGE = PackageDescriptorEntity.class.getPackageName();

    @Test
    void explicitPackageContributesMetadataWithoutEnrollingItsClasses() {
        MetadataProject project = process(unit("4.0", List.of(PACKAGE), List.of()));

        assertPackageMetadata(project);
        assertTrue(project.getEntityAccessors().isEmpty());
        assertTrue(project.getWeavableClassNames().isEmpty());
    }

    @Test
    void completeContainerPackageListWorksWithoutExplicitEntriesOrRootAccess() {
        MetadataProject project = process(unit("4.0", List.of(), List.of(PACKAGE)));

        assertPackageMetadata(project);
        assertTrue(project.getWeavableClassNames().isEmpty());
    }

    @Test
    void duplicateExplicitAndDiscoveredDescriptorsAreProcessedOnce() {
        MetadataProject project = process(unit("4.0", List.of(PACKAGE, PACKAGE), List.of(PACKAGE, PACKAGE)));

        assertPackageMetadata(project);
    }

    @Test
    void legacyContainerNullExplicitListDoesNotHideCompletePackageList() {
        MetadataProject project = process(unit("4.0", null, List.of(PACKAGE)));

        assertPackageMetadata(project);
    }

    @Test
    void ordinaryManagedClassDoesNotRegisterItsPackageInVersionFour() {
        SEPersistenceUnitInfo unit = unit("4.0", List.of(), List.of());
        unit.getManagedClassNames().add(PackageDescriptorEntity.OrdinaryType.class.getName());

        assertTrue(process(unit).getPackageAccessors().isEmpty());
    }

    @Test
    void packageDescriptorInOrdinaryClassListDoesNotRegisterItsPackageInVersionFour() {
        SEPersistenceUnitInfo unit = unit("4.0", List.of(), List.of());
        unit.getManagedClassNames().add(PACKAGE + ".package-info");

        MetadataProject project = process(unit);
        assertTrue(project.getPackageAccessors().isEmpty());
        assertTrue(project.getWeavableClassNames().isEmpty());
    }

    @Test
    void versionThreeRetainsPackageMetadataInferredFromOrdinaryClasses() {
        SEPersistenceUnitInfo unit = unit("3.2", List.of(), List.of());
        unit.getManagedClassNames().add(PackageDescriptorEntity.OrdinaryType.class.getName());

        assertPackageMetadata(process(unit));
    }

    private static SEPersistenceUnitInfo unit(String version, List<String> declared, List<String> all) {
        SEPersistenceUnitInfo unit = new SEPersistenceUnitInfo() {
            @Override
            public List<String> getManagedPackageDescriptors() {
                return declared;
            }

            @Override
            public List<String> getAllPackageDescriptors() {
                return all;
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                throw new AssertionError("A supplied container package list must not cause root scanning");
            }
        };
        unit.setPersistenceUnitName("package-descriptor-metadata");
        unit.setPersistenceXMLSchemaVersion(version);
        unit.setClassLoader(PackageDescriptorMetadataTest.class.getClassLoader());
        unit.setExcludeUnlistedClasses(true);
        return unit;
    }

    private static MetadataProject process(SEPersistenceUnitInfo unit) {
        MetadataProcessor processor = new MetadataProcessor(unit, new ServerSession(), unit.getClassLoader(),
                true, false, false, false, false, Map.of(), null);
        processor.processEntityMappings(PersistenceUnitProcessor.Mode.ALL);
        return processor.getProject();
    }

    private static void assertPackageMetadata(MetadataProject project) {
        assertEquals(1, project.getPackageAccessors().size());
        PackageAccessor accessor = project.getPackageAccessors().iterator().next();
        assertEquals(PACKAGE + ".package-info", accessor.getName());
        assertEquals(Set.of("package-descriptor-metadata-query"), project.getNamedQueries(accessor).stream()
                .map(query -> query.getName()).collect(Collectors.toSet()));
    }
}
