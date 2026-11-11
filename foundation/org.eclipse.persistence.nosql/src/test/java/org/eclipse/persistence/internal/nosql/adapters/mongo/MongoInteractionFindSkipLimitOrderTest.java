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

package org.eclipse.persistence.internal.nosql.adapters.mongo;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.Test;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import jakarta.resource.cci.Record;

/**
 * https://github.com/eclipse-ee4j/eclipselink/issues/2814
 *
 * FindIterable.iterator() executes the query and snapshots the current cursor
 * options: skip/limit/batchSize calls made after it have no effect on the
 * already-open cursor. This verifies MongoInteraction applies them to the
 * FindIterable before opening the cursor, using fake (proxy-based) MongoDB
 * driver objects so the test needs no live MongoDB instance.
 */
public class MongoInteractionFindSkipLimitOrderTest {

    @Test
    public void skipLimitAndBatchSizeAreAppliedBeforeTheCursorIsOpened() throws Exception {
        List<String> callOrder = new ArrayList<>();

        MongoCursor<Document> cursor = proxy(MongoCursor.class, callOrder, method -> {
            switch (method.getName()) {
                case "hasNext":
                    return false;
                case "close":
                    return null;
                default:
                    throw new UnsupportedOperationException(method.getName());
            }
        });

        FindIterable<Document> iterable = proxy(FindIterable.class, callOrder, method -> {
            switch (method.getName()) {
                case "sort":
                case "skip":
                case "limit":
                case "batchSize":
                    return null; // fluent return value is unused by MongoInteraction
                case "iterator":
                    return cursor;
                default:
                    throw new UnsupportedOperationException(method.getName());
            }
        });

        MongoCollection<Document> collection = proxy(MongoCollection.class, callOrder, method -> {
            switch (method.getName()) {
                case "find":
                    return iterable;
                default:
                    throw new UnsupportedOperationException(method.getName());
            }
        });

        MongoDatabase database = proxy(MongoDatabase.class, callOrder, method -> {
            switch (method.getName()) {
                case "getCollection":
                    return collection;
                default:
                    throw new UnsupportedOperationException(method.getName());
            }
        });

        MongoConnection connection = new MongoConnection(null, "testDb", true, null) {
            @Override
            public MongoDatabase getDB() {
                return database;
            }
        };

        MongoInteractionSpec spec = new MongoInteractionSpec();
        spec.setOperation(MongoOperation.FIND);
        spec.setCollection("testCollection");
        spec.setSkip(5);
        spec.setLimit(10);
        spec.setBatchSize(20);

        Record result = new MongoInteraction(connection).execute(spec, new MongoRecord());

        assertNull("cursor was empty, so no results should be returned", result);

        int skipIndex = callOrder.indexOf("skip");
        int limitIndex = callOrder.indexOf("limit");
        int batchSizeIndex = callOrder.indexOf("batchSize");
        int iteratorIndex = callOrder.indexOf("iterator");
        assertTrue("skip, limit, batchSize and iterator must all be called",
                skipIndex >= 0 && limitIndex >= 0 && batchSizeIndex >= 0 && iteratorIndex >= 0);
        assertTrue("skip must be applied before the cursor is opened", skipIndex < iteratorIndex);
        assertTrue("limit must be applied before the cursor is opened", limitIndex < iteratorIndex);
        assertTrue("batchSize must be applied before the cursor is opened", batchSizeIndex < iteratorIndex);
    }

    private interface MethodHandler {
        Object handle(Method method) throws Throwable;
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, List<String> callOrder, MethodHandler handler) {
        InvocationHandler invocationHandler = (target, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "toString" -> type.getSimpleName() + "$Fake";
                    case "hashCode" -> System.identityHashCode(target);
                    case "equals" -> target == args[0];
                    default -> null;
                };
            }
            callOrder.add(method.getName());
            return handler.handle(method);
        };
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, invocationHandler);
    }
}
