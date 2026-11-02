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

package org.eclipse.persistence.queries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.sql.Types;

import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.serializers.JavaSerializer;
import org.junit.jupiter.api.Test;

class DatabaseQueryArgumentFieldTest {

    @Test
    void rowFromArgumentsKeepsMoreSpecificFieldMetadataForDuplicateArgumentNames() {
        DatabaseQuery query = new DataReadQuery();
        query.addArgument("value", Object.class);
        query.addArgument("value", Long.class);

        AbstractRecord row = query.rowFromArguments(Arrays.asList(null, null), null);

        assertEquals(Long.class, row.getField(new DatabaseField("value")).getType());
    }

    @Test
    void rowFromArgumentsDoesNotDowngradeExistingSpecificFieldMetadata() {
        DatabaseQuery query = new DataReadQuery();
        query.addArgument("value", Long.class);
        query.addArgument("value", Object.class);

        AbstractRecord row = query.rowFromArguments(Arrays.asList(null, null), null);

        assertEquals(Long.class, row.getField(new DatabaseField("value")).getType());
    }

    @Test
    void typedNullIsAnOwnedFieldAndLogicalNullBeforeEveryRowConsumer() {
        DatabaseQuery query = new DataReadQuery();
        query.addArgument("value", Object.class);
        query.argumentFields = query.buildArgumentFields();
        query.setIsPrepared(true);
        DatabaseField field = new DatabaseField("value");
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        for (Class<?> type : List.of(Integer.class, String.class, Object.class, java.util.Collection.class)) {
            AbstractRecord row = query.rowFromArguments(List.of(new ParameterExpression.TypedNull(type)), session);
            assertNull(row.get(field));
            assertEquals(type, row.getField(field).getType());
            assertEquals(Object.class, query.argumentFields.get(0).getType());
            assertNotSame(query.argumentFields.get(0), row.getField(field));
            AbstractRecord restored = (AbstractRecord) JavaSerializer.instance.deserialize(
                    JavaSerializer.instance.serialize(row, session), session);
            assertNull(restored.get(field));
            assertEquals(type, restored.getField(field).getType());
        }
        assertEquals(7, query.rowFromArguments(List.of(7), session).get(field));
        assertNull(query.rowFromArguments(Arrays.asList((Object) null), session).get(field));
        assertEquals(Object.class, query.argumentFields.get(0).getType());
    }

    @Test
    void explicitNullTypesOverrideOwnedInferredMetadataWithoutChangingTheTemplate() {
        DatabaseQuery query = new DataReadQuery();
        query.addArgument("value", String.class);
        query.addArgument("value", Object.class);
        query.setIsPrepared(true);
        query.argumentFields = query.buildArgumentFields();
        query.argumentFields.get(0).setSqlType(Types.VARCHAR);
        AbstractRecord row = query.rowFromArguments(List.of(new ParameterExpression.TypedNull(Integer.class),
                new ParameterExpression.TypedNull(Integer.class)), null);
        DatabaseField field = row.getField(new DatabaseField("value"));
        assertNull(row.get(field));
        assertEquals(Integer.class, field.getType());
        assertEquals(DatabaseField.NULL_SQL_TYPE, field.getSqlType());
        assertEquals(String.class, query.argumentFields.get(0).getType());
        assertEquals(Types.VARCHAR, query.argumentFields.get(0).getSqlType());
    }

    @Test
    void normalizedNullRetainsNullablePreparationAndConvertedValueContracts() {
        DatabaseQuery query = new DataReadQuery();
        query.addArgument("value", Object.class);
        query.getNullableArguments().add(new DatabaseField("value"));
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        AbstractRecord row = query.rowFromArguments(List.of(new ParameterExpression.TypedNull(Integer.class)), session);
        assertFalse(query.shouldPrepare(row, session));
        query.setShouldPrepare(true);
        assertTrue(query.shouldPrepare(query.rowFromArguments(List.of(7), session), session));
        var converted = new ParameterExpression.ConvertedValue("relational");
        assertEquals(converted, query.rowFromArguments(List.of(converted), session).get("value"));
    }
}
