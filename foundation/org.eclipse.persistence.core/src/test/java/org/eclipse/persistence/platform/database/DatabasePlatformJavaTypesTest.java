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
 *
 * This file was generated with assistance from OpenAI Codex.
 */

package org.eclipse.persistence.platform.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.persistence.internal.databaseaccess.DatabaseJsonPlatform;
import org.eclipse.persistence.tools.schemaframework.DDLPlatform;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DatabasePlatformJavaTypesTest {

    static Stream<Arguments> platforms() {
        return Stream.of(
                Arguments.of("Base", (Supplier<DatabasePlatform>) DatabasePlatform::new),
                Arguments.of("H2", (Supplier<DatabasePlatform>) H2Platform::new),
                Arguments.of("Derby", (Supplier<DatabasePlatform>) DerbyPlatform::new),
                Arguments.of("Oracle", (Supplier<DatabasePlatform>) OraclePlatform::new),
                Arguments.of("MySQL", (Supplier<DatabasePlatform>) MySQLPlatform::new),
                Arguments.of("DB2", (Supplier<DatabasePlatform>) DB2Platform::new));
    }

    @ParameterizedTest(name = "{0}: defaults, cached identity and public lookup")
    @MethodSource("platforms")
    void javaTypesRetainDefaultsAndAreCachedPerInstance(String platformName, Supplier<DatabasePlatform> factory) {
        DatabasePlatform first = factory.get();
        DatabasePlatform second = factory.get();
        Map<String, Class<?>> firstTypes = first.getJavaTypes();
        Map<String, Class<?>> secondTypes = second.getJavaTypes();

        assertEquals(DDLPlatform.CLASS_TYPES, firstTypes, platformName);
        assertEquals(DDLPlatform.CLASS_TYPES, secondTypes, platformName);
        assertSame(firstTypes, first.getJavaTypes());
        assertSame(secondTypes, second.getJavaTypes());
        assertNotSame(firstTypes, secondTypes);
        assertNotSame(DDLPlatform.CLASS_TYPES, firstTypes);
        assertNotSame(DDLPlatform.CLASS_TYPES, secondTypes);
        assertSame(first.getDatabaseType(String.class), first.getDatabaseType("VARCHAR"));
        assertEquals("UNMAPPED_VENDOR_TYPE", first.getDatabaseType("UNMAPPED_VENDOR_TYPE").name());
        assertSame(firstTypes, first.getJavaTypes());
        assertEquals(DDLPlatform.CLASS_TYPES, firstTypes);
    }

    @ParameterizedTest(name = "{0}: put, replace and remove isolation")
    @MethodSource("platforms")
    void putReplaceAndRemoveDoNotChangeOtherInstancesOrDefaults(String platformName, Supplier<DatabasePlatform> factory) {
        DatabasePlatform first = factory.get();
        DatabasePlatform second = factory.get();
        Map<String, Class<?>> firstTypes = first.getJavaTypes();
        Map<String, Class<?>> secondTypes = second.getJavaTypes();
        Map<String, Class<?>> defaults = Map.copyOf(DDLPlatform.CLASS_TYPES);

        assertNull(firstTypes.put("CUSTOM_TYPE", Object.class));
        assertEquals(String.class, firstTypes.replace("VARCHAR", CharSequence.class));
        assertEquals(Timestamp.class, firstTypes.remove("TIMESTAMP"));

        assertEquals(Object.class, first.getJavaTypes().get("CUSTOM_TYPE"));
        assertEquals(CharSequence.class, first.getJavaTypes().get("VARCHAR"));
        assertFalse(first.getJavaTypes().containsKey("TIMESTAMP"));
        assertSame(firstTypes, first.getJavaTypes());
        assertSame(secondTypes, second.getJavaTypes());
        assertEquals(defaults, secondTypes, platformName);
        assertEquals(defaults, factory.get().getJavaTypes(), platformName);
        assertEquals(defaults, new DatabasePlatform().getJavaTypes());
        assertEquals(defaults, DDLPlatform.CLASS_TYPES);
    }

    @Test
    void defaultJavaTypesRemainImmutable() {
        assertThrows(UnsupportedOperationException.class,
                () -> DDLPlatform.CLASS_TYPES.put("CUSTOM_TYPE", Object.class));
        assertThrows(UnsupportedOperationException.class,
                () -> DDLPlatform.CLASS_TYPES.replace("VARCHAR", CharSequence.class));
        assertThrows(UnsupportedOperationException.class,
                () -> DDLPlatform.CLASS_TYPES.remove("TIMESTAMP"));
    }

    @Test
    void subclassCanCustomizeSuperJavaTypes() {
        DatabasePlatform platform = new CustomJavaTypesPlatform();
        Map<String, Class<?>> types = platform.getJavaTypes();

        assertEquals(Object.class, types.get("CUSTOM_TYPE"));
        assertEquals(CharSequence.class, types.get("VARCHAR"));
        assertFalse(types.containsKey("TIMESTAMP"));
        assertSame(types, platform.getJavaTypes());
        assertEquals(DDLPlatform.CLASS_TYPES, new DatabasePlatform().getJavaTypes());
    }

    @Test
    void sqlServerAddsDateTime2WithoutChangingDefaults() {
        SQLServerPlatform platform = new SQLServerPlatform();
        Map<String, Class<?>> types = platform.getJavaTypes();

        assertEquals(Timestamp.class, types.get("DATETIME2"));
        assertEquals(Timestamp.class, types.get("TIMESTAMP"));
        assertSame(types, platform.getJavaTypes());
        assertFalse(new DatabasePlatform().getJavaTypes().containsKey("DATETIME2"));
        assertFalse(DDLPlatform.CLASS_TYPES.containsKey("DATETIME2"));
        Map<String, Class<?>> expected = new HashMap<>(DDLPlatform.CLASS_TYPES);
        expected.put("DATETIME2", Timestamp.class);
        assertEquals(expected, types);
        assertSame(platform.getDatabaseType(Timestamp.class), platform.getDatabaseType("DATETIME2"));
        assertSame(platform.getDatabaseType(String.class), platform.getDatabaseType("VARCHAR"));
        assertEquals("UNMAPPED_VENDOR_TYPE", platform.getDatabaseType("UNMAPPED_VENDOR_TYPE").name());
    }

    @Test
    void postgreSql10RetainsJsonTypeCustomizationAndIsolation() {
        PostgreSQL10Platform first = new CustomJsonPostgreSQL10Platform();
        PostgreSQL10Platform second = new CustomJsonPostgreSQL10Platform();
        Map<String, Class<?>> firstTypes = first.getJavaTypes();
        Map<String, Class<?>> secondTypes = second.getJavaTypes();

        Map<String, Class<?>> expected = new HashMap<>(DDLPlatform.CLASS_TYPES);
        expected.put("JSONB", JsonType.class);
        assertEquals(expected, firstTypes);
        assertEquals(expected, secondTypes);
        assertNotSame(firstTypes, secondTypes);
        assertSame(firstTypes, first.getJavaTypes());
        firstTypes.remove("JSONB");
        assertFalse(first.getJavaTypes().containsKey("JSONB"));
        assertEquals(JsonType.class, second.getJavaTypes().get("JSONB"));
        assertFalse(new DatabasePlatform().getJavaTypes().containsKey("JSONB"));
        assertFalse(DDLPlatform.CLASS_TYPES.containsKey("JSONB"));
    }

    private static class CustomJavaTypesPlatform extends DatabasePlatform {
        @Override
        protected Map<String, Class<?>> buildJavaTypes() {
            Map<String, Class<?>> types = super.buildJavaTypes();
            types.put("CUSTOM_TYPE", Object.class);
            types.replace("VARCHAR", CharSequence.class);
            types.remove("TIMESTAMP");
            return types;
        }
    }

    private static class CustomJsonPostgreSQL10Platform extends PostgreSQL10Platform {
        @Override
        public DatabaseJsonPlatform getJsonPlatform() {
            return new DatabaseJsonPlatform() {
                @Override
                public void updateClassTypes(Map<String, Class<?>> types) {
                    types.put("JSONB", JsonType.class);
                }
            };
        }
    }

    private static class JsonType {
    }
}
