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

import java.sql.SQLException;
import java.util.stream.Stream;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniqueConstraintViolationTest {
    static Stream<Arguments> platforms() {
        return Stream.of(
                Arguments.of(new DatabasePlatform(), 0),
                Arguments.of(new H2Platform(), 0),
                Arguments.of(new PostgreSQLPlatform(), 0),
                Arguments.of(new DerbyPlatform(), 0),
                Arguments.of(new OraclePlatform(), 1),
                Arguments.of(new MySQLPlatform(), 1062),
                Arguments.of(new SQLServerPlatform(), 2601),
                Arguments.of(new DB2Platform(), -803),
                Arguments.of(new DatabasePlatform() { }, 0));
    }

    @ParameterizedTest
    @MethodSource("platforms")
    void preciseStateAndPlatformQualifiedCodes(DatabasePlatform platform, int vendorCode) {
        assertTrue(matches(platform, new SQLException("unique", "23505", 0)));
        assertFalse(matches(platform, new SQLException("foreign key", "23503", 0)));
        assertFalse(matches(platform, new SQLException("not null", "23502", 0)));
        assertFalse(matches(platform, new SQLException("check", "23513", 0)));
        assertFalse(matches(platform, new SQLException("integrity", "23000", 0)));
        assertFalse(matches(platform, new SQLException("unknown", null, 0)));
        for (int code : new int[]{1, 1062, 2601, 2627, -803}) {
            boolean duplicate = code == vendorCode || platform instanceof SQLServerPlatform && code == 2627;
            SQLException failure = new SQLException("vendor code without a state", null, code);
            if (duplicate) {
                assertTrue(matches(platform, failure), platform + ": " + code);
            } else {
                assertFalse(matches(platform, failure), platform + ": " + code);
            }
        }
    }

    @Test
    void sqlServerXopenStatesMayReportSyntaxClassForDuplicateCodes() {
        assertTrue(matches(new SQLServerPlatform(), new SQLException("duplicate index", "42000", 2601)));
        assertTrue(matches(new SQLServerPlatform(), new SQLException("duplicate constraint", "42000", 2627)));
        assertFalse(matches(new SQLServerPlatform(), new SQLException("syntax", "42000", 102)));
    }

    @Test
    void wrapperCausesAndNextExceptionsAreTraversed() {
        SQLException batch = new SQLException("batch", "HY000");
        SQLException wrapper = new SQLException("wrapper", new IllegalStateException("wrapped", new SQLException("unique", "23505")));
        batch.setNextException(wrapper);
        assertTrue(matches(new DatabasePlatform(), batch));
        DatabaseException database = DatabaseException.sqlException(batch);
        database.setInternalException(new IllegalStateException("outer", batch));
        assertTrue(new DatabasePlatform().isUniqueConstraintViolation(database));
    }

    @Test
    void causeAndNextCyclesTerminateAndStillFindReachableDuplicate() {
        SQLException first = new SQLException("first");
        SQLException second = new SQLException("second");
        first.initCause(second);
        second.initCause(first);
        first.setNextException(second);
        assertFalse(matches(new DatabasePlatform(), first));
        second.setNextException(new SQLException("unique", "23505"));
        assertTrue(matches(new DatabasePlatform(), first));
        SQLException nextCycle = new SQLException("next cycle");
        nextCycle.setNextException(nextCycle);
        assertFalse(matches(new DatabasePlatform(), nextCycle));
    }

    @Test
    void suppressedCleanupDoesNotReclassifyPrimaryOperation() {
        SQLException primary = new SQLException("connection", "08006", 0);
        primary.addSuppressed(new SQLException("cleanup duplicate", "23505", 0));
        DatabaseException database = DatabaseException.sqlException(primary);
        database.addSuppressed(new SQLException("outer cleanup duplicate", "23505", 0));
        assertFalse(new DatabasePlatform().isUniqueConstraintViolation(database));
    }

    private static boolean matches(DatabasePlatform platform, SQLException exception) {
        return platform.isUniqueConstraintViolation(DatabaseException.sqlException(exception));
    }
}
