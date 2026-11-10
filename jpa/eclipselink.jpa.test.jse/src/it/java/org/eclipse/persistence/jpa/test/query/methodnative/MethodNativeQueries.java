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
package org.eclipse.persistence.jpa.test.query.methodnative;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.QueryHint;
import jakarta.persistence.query.NativeQuery;
import jakarta.persistence.query.QueryOptions;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeResults.EntityValue;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeResults.Ordinary;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeResults.Summary;
import org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeResults.Text;

public interface MethodNativeQueries {
    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    List<Summary> summaries();

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    @QueryOptions(timeout = 3000, cacheRetrieveMode = CacheRetrieveMode.BYPASS,
            hints = @QueryHint(name = "eclipselink.jdbc.max-rows", value = "1"))
    List<Summary> limitedSummaries();

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    Summary summary(Integer id);

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE TITLE = ?1")
    Summary summary(String title);

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1 AND TITLE = ?2")
    Summary summary(int id, String title);

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    Stream<Summary> summaryStream();

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    List<Ordinary> ordinaryResults();

    @NativeQuery("SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    String title(Integer id);

    @NativeQuery("SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    List<String> titles();

    @NativeQuery("SELECT COUNT(*) FROM JPA_METHOD_NATIVE_BOOK")
    int count();

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    Object[] array(Integer id);

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    List<Object[]> arrays();

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    MethodNativeBook book(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    List<MethodNativeBook> books();

    @NativeQuery("SELECT TITLE AS BOOK_TITLE, ID AS BOOK_ID FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    @ConstructorResult(targetClass = Summary.class, columns = {
            @ColumnResult(name = "BOOK_ID", type = Integer.class),
            @ColumnResult(name = "BOOK_TITLE", type = String.class)})
    List<Object> explicitConstructor();

    @NativeQuery("SELECT TITLE, ID FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    @ColumnResult(name = "ID", type = Integer.class)
    @ColumnResult(name = "TITLE", type = String.class)
    List<Object[]> explicitColumns();

    @NativeQuery("SELECT ID FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @ColumnResult(name = "ID", type = Long.class)
    Long explicitScalar(Integer id);

    @NativeQuery("SELECT ID FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @ColumnResult(name = "ID", type = int.class)
    int explicitPrimitive(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @EntityResult(entityClass = MethodNativeBook.class)
    MethodNativeBook explicitEntity(Integer id);

    @NativeQuery("SELECT ID AS BOOK_ID, TITLE AS BOOK_TITLE, NOTE AS BOOK_NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @EntityResult(entityClass = MethodNativeBook.class, fields = {
            @FieldResult(name = "id", column = "BOOK_ID"),
            @FieldResult(name = "title", column = "BOOK_TITLE"),
            @FieldResult(name = "detail.note", column = "BOOK_NOTE")})
    MethodNativeBook aliasedEntity(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    @ColumnResult(name = "TITLE", type = String.class)
    @ConstructorResult(targetClass = Summary.class, columns = {
            @ColumnResult(name = "ID", type = Integer.class),
            @ColumnResult(name = "TITLE", type = String.class)})
    @EntityResult(entityClass = MethodNativeBook.class)
    List<Object[]> mixed();

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    @ConstructorResult(targetClass = Summary.class, columns = {
            @ColumnResult(name = "ID", type = Integer.class),
            @ColumnResult(name = "TITLE", type = String.class)})
    @ConstructorResult(targetClass = Text.class, columns = @ColumnResult(name = "TITLE", type = String.class))
    List<Object[]> constructors();

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    @EntityResult(entityClass = MethodNativeBook.class)
    @EntityResult(entityClass = MethodNativeBook.class)
    List<Object[]> entities();

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID")
    @ConstructorResult(targetClass = EntityValue.class,
            columns = @ColumnResult(name = "TITLE", type = String.class),
            entities = @EntityResult(entityClass = MethodNativeBook.class))
    List<EntityValue> nestedEntity();

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @ConstructorResult(targetClass = EntityValue.class,
            columns = @ColumnResult(name = "TITLE", type = String.class),
            entities = @EntityResult(entityClass = MethodNativeBook.class))
    EntityValue nestedOne(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @ConstructorResult(targetClass = EntityValue.class,
            columns = @ColumnResult(name = "TITLE", type = String.class),
            entities = @EntityResult(entityClass = MethodNativeBook.class, lockMode = LockModeType.PESSIMISTIC_WRITE))
    EntityValue nestedLocked(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @QueryOptions(cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.BYPASS)
    MethodNativeBook cachedBookBypass(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @QueryOptions(cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.REFRESH)
    MethodNativeBook cachedBookRefresh(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @EntityResult(entityClass = MethodNativeBook.class)
    @QueryOptions(cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.REFRESH)
    MethodNativeBook cachedMappedBookRefresh(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @QueryOptions
    MethodNativeBook cachedBookDefault(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @ConstructorResult(targetClass = EntityValue.class,
            columns = @ColumnResult(name = "TITLE", type = String.class),
            entities = @EntityResult(entityClass = MethodNativeBook.class))
    @QueryOptions(cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.BYPASS)
    EntityValue cachedNestedBookBypass(Integer id);

    @NativeQuery("SELECT ID, TITLE, NOTE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @ConstructorResult(targetClass = EntityValue.class,
            columns = @ColumnResult(name = "TITLE", type = String.class),
            entities = @EntityResult(entityClass = MethodNativeBook.class))
    @QueryOptions(cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.REFRESH)
    EntityValue cachedNestedBookRefresh(Integer id);

    @NativeQuery("SELECT ID, TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @QueryOptions
    Summary summaryDefaults(Integer id);

    @NativeQuery("SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @QueryOptions(cacheRetrieveMode = CacheRetrieveMode.BYPASS, cacheStoreMode = CacheStoreMode.REFRESH,
            timeout = 3000, flush = QueryFlushMode.NO_FLUSH, hints = {
            @QueryHint(name = "eclipselink.jdbc.fetch-size", value = "7"),
            @QueryHint(name = "method.native.unknown.hint", value = "ignored")})
    String options(Integer id);

    @NativeQuery("SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    @QueryOptions
    String defaultOptions(Integer id);

    @NativeQuery("UPDATE JPA_METHOD_NATIVE_BOOK SET TITLE = ?1 WHERE ID = ?2")
    void updateVoid(String title, Integer id);

    @NativeQuery("UPDATE JPA_METHOD_NATIVE_BOOK SET TITLE = ?1 WHERE ID = ?2")
    @QueryOptions(timeout = 3000, flush = QueryFlushMode.NO_FLUSH)
    int updateInt(String title, Integer id);

    @NativeQuery("UPDATE JPA_METHOD_NATIVE_BOOK SET TITLE = ?1 WHERE ID = ?2")
    long updateLong(String title, Integer id);

    @NativeQuery("DELETE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    long delete(Integer id);
}
