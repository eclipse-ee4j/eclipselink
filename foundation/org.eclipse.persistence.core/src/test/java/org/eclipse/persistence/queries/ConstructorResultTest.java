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
package org.eclipse.persistence.queries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.List;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConstructorResultTest {
    @Test
    void columnsPrecedeEntitiesAndBothKindsRetainDeclarationOrder() {
        ConstructorResult result = new ConstructorResult(Mixed.class);
        result.addColumnResult(column("SECOND", Integer.class));
        result.addColumnResult(column("FIRST", String.class));
        result.addEntityResult(entity("left"));
        result.addEntityResult(entity("right"));
        DatabaseRecord record = new DatabaseRecord();
        record.put("FIRST", "value");
        record.put("SECOND", BigDecimal.valueOf(7));
        assertEquals(new Mixed(7, "value", "left", "right"), value(result, record));
        record.put("SECOND", BigDecimal.valueOf(9));
        assertEquals(new Mixed(9, "value", "left", "right"), value(result, record));
    }

    @Test
    void entityOnlyConstructorUsesDeclaredTypesForNullArguments() {
        ConstructorResult result = new ConstructorResult(EntityOnly.class);
        result.addEntityResult(entity(null));
        assertEquals(new EntityOnly(null), value(result, new DatabaseRecord()));
    }

    @Test
    void columnOnlyConstructorKeepsConversionAndTypedNullBehavior() {
        ConstructorResult result = new ConstructorResult(ColumnOnly.class);
        result.setColumnResults(List.of(column("VALUE", Integer.class)));
        DatabaseRecord record = new DatabaseRecord();
        record.put("VALUE", BigDecimal.TEN);
        assertEquals(new ColumnOnly(10), value(result, record));
        record.put("VALUE", null);
        assertEquals(new ColumnOnly(null), value(result, record));
    }

    @Test
    void missingUntypedColumnAndMissingConstructorKeepQueryExceptions() {
        ConstructorResult missingColumn = new ConstructorResult(ColumnOnly.class);
        missingColumn.addColumnResult(new ColumnResult("MISSING"));
        assertThrows(QueryException.class, () -> value(missingColumn, new DatabaseRecord()));
        ConstructorResult missingConstructor = new ConstructorResult(ColumnOnly.class);
        missingConstructor.addEntityResult(entity("not an Integer"));
        assertThrows(QueryException.class, () -> value(missingConstructor, new DatabaseRecord()));
    }

    @Test
    void parentMappingReachesChildrenAddedBeforeAndAfterRegistration() {
        ConstructorResult result = new ConstructorResult(EntityOnly.class);
        EntityResult first = entity("first");
        result.addEntityResult(first);
        SQLResultSetMapping mapping = new SQLResultSetMapping("constructor-parent");
        mapping.addResult(result);
        assertSame(mapping, first.getSQLResultMapping());
        EntityResult second = entity("second");
        result.addEntityResult(second);
        assertSame(mapping, second.getSQLResultMapping());
        EntityResult replacement = entity("replacement");
        result.setEntityResults(List.of(replacement));
        assertSame(mapping, replacement.getSQLResultMapping());
    }

    @Test
    void classNameConversionIncludesEntityArguments() {
        ConstructorResult result = new ConstructorResult(EntityOnly.class.getName());
        result.addEntityResult(new EntityResult(String.class.getName()));
        result.convertClassNamesToClasses(getClass().getClassLoader());
        assertSame(EntityOnly.class, result.getTargetClass());
        assertSame(String.class, result.getEntityResults().getFirst().getEntityClass());
    }

    @Test
    void serializationRetainsOrderedEntityMappingsAndParentAssociation() throws Exception {
        ConstructorResult result = new ConstructorResult(EntityOnly.class);
        EntityResult entity = new EntityResult(String.class);
        entity.setLockMode("PESSIMISTIC_READ");
        entity.addFieldResult(new FieldResult("value", "ENTITY_VALUE"));
        result.addEntityResult(entity);
        SQLResultSetMapping mapping = new SQLResultSetMapping("serialized-constructor");
        mapping.addResult(result);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(mapping);
        }
        SQLResultSetMapping restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (SQLResultSetMapping) input.readObject();
        }
        ConstructorResult constructor = (ConstructorResult) restored.getResults().getFirst();
        assertNull(constructor.getTargetClass());
        restored.convertClassNamesToClasses(getClass().getClassLoader());
        assertSame(EntityOnly.class, constructor.getTargetClass());
        EntityResult child = constructor.getEntityResults().getFirst();
        assertSame(String.class, child.getEntityClass());
        assertEquals("PESSIMISTIC_READ", child.getLockMode());
        assertEquals(1, child.getFieldResults().size());
        assertSame(restored, child.getSQLResultMapping());
    }

    private static Object value(ConstructorResult result, DatabaseRecord record) {
        return result.getValueFromRecord(record, new ResultSetMappingQuery());
    }

    private static ColumnResult column(String name, Class<?> type) {
        DatabaseField field = new DatabaseField(name);
        field.setType(type);
        return new ColumnResult(field);
    }

    private static EntityResult entity(String value) {
        return new EntityResult(String.class) {
            @Override
            public Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query) {
                return value;
            }
        };
    }

    public record Mixed(Integer number, String value, String firstEntity, String secondEntity) { }
    public record EntityOnly(String entity) { }
    public record ColumnOnly(Integer number) { }
}
