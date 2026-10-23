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
package org.eclipse.persistence.platform.database;

import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

public class DerbyPlatformTest {

    @Test
    public void testPlainDatePreservesTimeAndMilliseconds() {
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
        Object converted = new DerbyPlatform().convertToDatabaseType(value);
        assertInstanceOf(Timestamp.class, converted);
        assertEquals(value.getTime(), ((Timestamp) converted).getTime());
    }

    @Test
    public void testMappingConversionDoesNotRequireConnectedAccessor() {
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(new DerbyPlatform()));
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Date.class);
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        mapping.setDescriptor(descriptor);
        mapping.setFieldName("SCOPED.CREATED_AT");
        mapping.setFieldClassification(Date.class);
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());

        assertSame(value, mapping.getFieldValue(value, session));
        assertEquals(DatabaseField.NULL_SQL_TYPE, mapping.getField().getSqlType());
    }
}
