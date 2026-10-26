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
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.Tuple;
import jakarta.persistence.sql.ResultSetMapping;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static jakarta.persistence.sql.ResultSetMapping.*;

class ResultSetMappingTranslatorTest {
    @Test
    void scalarMappingConvertsJdbcNumericValue() {
        assertEquals(7, result(column("TOTAL", Integer.class)));
    }

    @Test
    void compoundPreservesArrayShapeForSingleElement() {
        assertArrayEquals(new Object[] {7}, (Object[]) result(compound(column("TOTAL", Integer.class))));
    }

    @Test
    void tupleSupportsIndexAliasAndElementWithoutLeakingItsArray() {
        var total = column("TOTAL", Integer.class).withAlias("total");
        var name = column("NAME", String.class);
        Tuple tuple = (Tuple) result(tuple(total, name));
        assertEquals(7, tuple.get(0));
        assertEquals(7, tuple.get("total", Integer.class));
        assertEquals(7, tuple.get(total));
        assertEquals("entry", tuple.get(name));
        assertThrows(IllegalArgumentException.class, () -> tuple.get("unknown"));
        assertThrows(IllegalArgumentException.class, () -> tuple.get(-1));
        assertThrows(IllegalArgumentException.class, () -> tuple.get(0, String.class));
        Object[] copy = tuple.toArray();
        copy[0] = 99;
        assertEquals(7, tuple.get(0));
        tuple.getElements().clear();
        assertEquals(2, tuple.getElements().size());
    }

    @Test
    void nestedConstructorsReceiveTypedArguments() {
        var mapping = constructor(Outer.class,
                constructor(Inner.class, column("TOTAL", int.class)), column("NAME", String.class));
        assertEquals(new Outer(new Inner(7), "entry"), result(mapping));
    }

    @Test
    void badConstructorIsReportedAsArgumentError() {
        assertThrows(IllegalArgumentException.class,
                () -> result(constructor(Inner.class, column("NAME", String.class))));
    }

    @Test
    void resultClassMappingConvertsScalarAndConstructsRows() {
        DatabaseRecord row = new DatabaseRecord();
        row.put("TOTAL", BigDecimal.valueOf(7));
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        QueryExecutionContextTest.TestContext context = new QueryExecutionContextTest.TestContext();
        query.setSession(context.getSession());
        query.setSQLResultSetMappings(List.of(ResultSetMappingTranslator.forResultClass(Integer.class, context)));
        assertEquals(7, query.buildObjectsFromRecords(List.of(row), 0).get(0));
        query.setSQLResultSetMappings(List.of(ResultSetMappingTranslator.forResultClass(Inner.class, context)));
        assertEquals(new Inner(7), query.buildObjectsFromRecords(List.of(row), 0).get(0));
        query.setSQLResultSetMappings(List.of(ResultSetMappingTranslator.forResultClass(Object[].class, context)));
        assertArrayEquals(new Object[] {BigDecimal.valueOf(7)}, (Object[]) query.buildObjectsFromRecords(List.of(row), 0).get(0));
    }

    private static Object result(ResultSetMapping<?> mapping) {
        DatabaseRecord row = new DatabaseRecord();
        row.put("TOTAL", BigDecimal.valueOf(7));
        row.put("NAME", "entry");
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.setSession(new DatabaseSessionImpl(new DatabaseLogin()));
        query.setSQLResultSetMappings(List.of(ResultSetMappingTranslator.toEclipseLinkMapping(mapping)));
        return query.buildObjectsFromRecords(List.of(row), 0).get(0);
    }

    public record Inner(int total) {
    }

    public record Outer(Inner inner, String name) {
    }
}
