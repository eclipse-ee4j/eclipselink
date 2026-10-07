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

// Contributors:
//     IBM - Fix for issue 2903: H2 SQL type mismatch when comparing boolean attribute to integer
package org.eclipse.persistence.testing.tests.junit.platform.database;

import java.io.StringWriter;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link H2Platform} covering the fix for issue 2903:
 * EclipseLink generated {@code REDUCED = 1} for boolean comparisons in H2,
 * which caused a {@code JdbcSQLSyntaxErrorException} because H2's strict
 * type system does not allow comparing BOOLEAN to INTEGER.
 * The fix overrides {@code appendBoolean} to emit {@code TRUE}/{@code FALSE}
 * instead of {@code 1}/{@code 0}.
 */
public class H2PlatformTest {

    private final H2Platform h2Platform = new H2Platform();

    /**
     * Reproduces the root cause of issue 2903:
     * H2Platform must write {@code TRUE} for a {@code true} boolean, NOT {@code 1}.
     * Writing {@code 1} caused H2 to throw:
     * "Values of types BOOLEAN and INTEGER are not comparable".
     */
    @Test
    public void testAppendTrueWritesTRUE() throws Exception {
        StringWriter writer = new StringWriter();
        h2Platform.appendParameter(new SQLCall(), writer, Boolean.TRUE);
        Assert.assertEquals(
                "H2Platform should write TRUE for boolean true (not 1)",
                "TRUE", writer.toString());
    }

    /**
     * H2Platform must write {@code FALSE} for a {@code false} boolean, NOT {@code 0}.
     */
    @Test
    public void testAppendFalseWritesFALSE() throws Exception {
        StringWriter writer = new StringWriter();
        h2Platform.appendParameter(new SQLCall(), writer, Boolean.FALSE);
        Assert.assertEquals(
                "H2Platform should write FALSE for boolean false (not 0)",
                "FALSE", writer.toString());
    }

    /**
     * Verifies the base {@link DatabasePlatform} still writes the legacy integer
     * representation, confirming that H2Platform's override is actually needed
     * and that the base behaviour has not changed.
     */
    @Test
    public void testBasePlatformStillWritesIntegerForBoolean() throws Exception {
        DatabasePlatform basePlatform = new DatabasePlatform();

        StringWriter trueWriter = new StringWriter();
        basePlatform.appendParameter(new SQLCall(), trueWriter, Boolean.TRUE);
        Assert.assertEquals(
                "Base DatabasePlatform should still write 1 for boolean true",
                "1", trueWriter.toString());

        StringWriter falseWriter = new StringWriter();
        basePlatform.appendParameter(new SQLCall(), falseWriter, Boolean.FALSE);
        Assert.assertEquals(
                "Base DatabasePlatform should still write 0 for boolean false",
                "0", falseWriter.toString());
    }

    @Test
    public void testIsH2() {
        Assert.assertTrue("H2Platform.isH2() must return true", h2Platform.isH2());
    }
}
