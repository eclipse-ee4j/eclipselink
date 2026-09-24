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

package org.eclipse.persistence.platform.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Types;
import java.util.UUID;
import java.util.stream.Stream;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PostgreSQLPlatformTest {

    static Stream<PostgreSQLPlatform> platforms() {
        return Stream.of(new PostgreSQLPlatform(), new PostgreSQL10Platform());
    }

    @ParameterizedTest
    @MethodSource("platforms")
    void nullUuidUsesOtherJdbcType(PostgreSQLPlatform platform) {
        DatabaseField field = new DatabaseField("OPTIONAL_UUID");
        field.setType(UUID.class);

        // PostgreSQL rejects a VARCHAR-typed null for a native UUID column (issue #2717).
        assertEquals(Types.OTHER, platform.getJDBCTypeForSetNull(field));
    }

    @ParameterizedTest
    @MethodSource("platforms")
    void nullUuidPreservesExplicitJdbcType(PostgreSQLPlatform platform) {
        DatabaseField field = new DatabaseField("OPTIONAL_UUID");
        field.setType(UUID.class);
        field.setSqlType(Types.VARCHAR);

        assertEquals(Types.VARCHAR, platform.getJDBCTypeForSetNull(field));

        field.setSqlType(Types.BINARY);
        assertEquals(Types.BINARY, platform.getJDBCTypeForSetNull(field));
    }

    @ParameterizedTest
    @MethodSource("platforms")
    void otherNullTypesKeepDefaultMapping(PostgreSQLPlatform platform) {
        DatabaseField field = new DatabaseField("VALUE");

        assertEquals(Types.VARCHAR, platform.getJDBCTypeForSetNull(null));
        assertEquals(Types.VARCHAR, platform.getJDBCTypeForSetNull(field));

        field.setType(String.class);
        assertEquals(Types.VARCHAR, platform.getJDBCTypeForSetNull(field));

        field.setType(Integer.class);
        assertEquals(Types.INTEGER, platform.getJDBCTypeForSetNull(field));
    }
}
