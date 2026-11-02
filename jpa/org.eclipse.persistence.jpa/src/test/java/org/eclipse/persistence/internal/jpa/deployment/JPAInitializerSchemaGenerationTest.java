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

import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.SchemaManagementAction;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JPAInitializerSchemaGenerationTest {

    @Test
    void repeatedSchemaRequestsSnapshotCurrentActionsTargetsClassesAndMappings() throws Exception {
        JPAInitializer initializer = initializer();
        URL root = URI.create("file:/schema-export/").toURL();
        PersistenceConfiguration configuration = new PersistenceConfiguration("export")
                .schemaManagementScriptsAction(SchemaManagementAction.CREATE)
                .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "first.sql")
                .managedClass(String.class).mappingFile("first.xml");
        SEPersistenceUnitInfo first = initializer.schemaGenerationPersistenceUnitInfo(configuration, root);
        configuration.schemaManagementScriptsAction(SchemaManagementAction.DROP_AND_CREATE)
                .property(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "second.sql")
                .managedClass(Integer.class).mappingFile("second.xml");
        SEPersistenceUnitInfo second = initializer.schemaGenerationPersistenceUnitInfo(configuration, root);
        assertNotSame(first, second);
        assertEquals("create", first.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION));
        assertEquals("first.sql", first.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET));
        assertEquals("drop-and-create", second.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION));
        assertEquals("second.sql", second.getProperties().get(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET));
        assertEquals(List.of(String.class.getName()), first.getManagedClassNames());
        assertEquals(List.of(String.class.getName(), Integer.class.getName()), second.getManagedClassNames());
        assertEquals(List.of("first.xml"), first.getMappingFileNames());
        assertEquals(List.of("first.xml", "second.xml"), second.getMappingFileNames());
        assertEquals(root, second.getPersistenceUnitRootUrl());
        assertNotEquals(initializer.createUniquePersistenceUnitName(first), initializer.createUniquePersistenceUnitName(second));
    }

    @Test
    void explicitSchemaRequestsDoNotReplaceTheOrdinaryFactorySnapshot() {
        JPAInitializer initializer = initializer();
        PersistenceConfiguration configuration = new PersistenceConfiguration("retained")
                .property("example", "factory");
        SEPersistenceUnitInfo factory = initializer.customPersistenceUnitInfo(configuration, null);
        configuration.property("example", "schema");
        SEPersistenceUnitInfo schema = initializer.schemaGenerationPersistenceUnitInfo(configuration, null);
        assertNotSame(factory, schema);
        assertEquals("factory", factory.getProperties().get("example"));
        assertEquals("schema", schema.getProperties().get("example"));
        assertSame(factory, initializer.customPersistenceUnitInfo(configuration, null));
    }

    @Test
    void schemaRequestsRespectXmlNamesAndReserveTheirCustomName() {
        JPAInitializer initializer = initializer();
        initializer.initialPuInfos = new HashMap<>();
        initializer.initialPuInfos.put("xml", new SEPersistenceUnitInfo());
        assertThrows(PersistenceException.class, () -> initializer.schemaGenerationPersistenceUnitInfo(
                new PersistenceConfiguration("xml"), null));
        initializer.schemaGenerationPersistenceUnitInfo(new PersistenceConfiguration("custom"), null);
        assertThrows(PersistenceException.class, () -> initializer.findPersistenceUnitInfo("custom", Map.of()));
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
