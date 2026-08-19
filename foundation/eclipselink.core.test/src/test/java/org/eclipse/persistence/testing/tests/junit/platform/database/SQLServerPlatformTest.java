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
package org.eclipse.persistence.testing.tests.junit.platform.database;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.platform.database.SQLServerPlatform;
import org.junit.Assert;
import org.junit.Test;

public class SQLServerPlatformTest {

    @Test
    public void testNullOffsetTemporalUsesTimestamp() {
        SQLServerPlatform platform = new SQLServerPlatform();
        platform.setDriverSupportsOffsetDateTime(false);

        Assert.assertEquals(Types.TIMESTAMP, platform.getJDBCTypeForSetNull(fieldWithType(OffsetDateTime.class)));
        Assert.assertEquals(Types.TIMESTAMP, platform.getJDBCTypeForSetNull(fieldWithType(OffsetTime.class)));
    }

    @Test
    public void testNullOffsetTemporalUsesTimezone() {
        SQLServerPlatform platform = new SQLServerPlatform();
        platform.setDriverSupportsOffsetDateTime(true);

        Assert.assertEquals(Types.TIMESTAMP_WITH_TIMEZONE,
                platform.getJDBCTypeForSetNull(fieldWithType(OffsetDateTime.class)));
        Assert.assertEquals(Types.TIME_WITH_TIMEZONE,
                platform.getJDBCTypeForSetNull(fieldWithType(OffsetTime.class)));
    }

    @Test
    public void testExplicitNullSqlTypeIsPreserved() {
        SQLServerPlatform platform = new SQLServerPlatform();
        platform.setDriverSupportsOffsetDateTime(false);

        DatabaseField field = fieldWithType(OffsetDateTime.class);
        field.setSqlType(Types.OTHER);

        Assert.assertEquals(Types.OTHER, platform.getJDBCTypeForSetNull(field));
    }

    private DatabaseField fieldWithType(Class<?> type) {
        DatabaseField field = new DatabaseField("OFFSET_DATE_TIME");
        field.setType(type);
        return field;
    }
}
