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

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.QueryHint;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.query.NativeQuery;

@Entity
@Table(name = "JPA_METHOD_NATIVE_BOOK")
@NamedNativeQuery(name = "methodNative.ordinary", query = "SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = 1", resultClass = String.class)
@NamedNativeQuery(name = "methodNative.mappedOrdinary",
        query = "SELECT TITLE AS BOOK_TITLE, ID AS BOOK_ID FROM JPA_METHOD_NATIVE_BOOK ORDER BY ID",
        resultSetMapping = "methodNative.reorderedMapping")
@NamedQuery(name = "methodNative.cachedTitles",
        query = "SELECT b.title FROM MethodNativeBook b WHERE b.id >= :id ORDER BY b.id",
        hints = {@QueryHint(name = "eclipselink.query-results-cache", value = "true"),
                @QueryHint(name = "eclipselink.maintain-cache", value = "false")})
@NamedQuery(name = "methodNative.cachedSummaries",
        query = "SELECT NEW org.eclipse.persistence.jpa.test.query.methodnative.MethodNativeResults$Summary(b.id, b.title) "
                + "FROM MethodNativeBook b WHERE b.id >= :id ORDER BY b.id",
        hints = {@QueryHint(name = "eclipselink.query-results-cache", value = "true"),
                @QueryHint(name = "eclipselink.maintain-cache", value = "false")})
@SqlResultSetMapping(name = "methodNative.reorderedMapping", classes = @ConstructorResult(
        targetClass = MethodNativeResults.Summary.class, columns = {
        @ColumnResult(name = "BOOK_ID", type = Integer.class),
        @ColumnResult(name = "BOOK_TITLE", type = String.class)}))
@SqlResultSetMapping(name = "methodNative.nestedMapping", classes = @ConstructorResult(
        targetClass = MethodNativeResults.EntityValue.class,
        columns = @ColumnResult(name = "TITLE", type = String.class),
        entities = @EntityResult(entityClass = MethodNativeBook.class)))
public class MethodNativeBook extends MethodNativeBase {
    @Id
    @Column(name = "ID")
    public Integer id;

    @Column(name = "TITLE")
    public String title;

    @Embedded
    public MethodNativeDetail detail;

    public MethodNativeBook() {
    }

    public MethodNativeBook(Integer id, String title, String note) {
        this.id = id;
        this.title = title;
        this.detail = new MethodNativeDetail(note);
    }

    @NativeQuery("SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    public String bookTitle(Integer id) {
        throw new AssertionError("Query declarations must not be invoked");
    }
}
