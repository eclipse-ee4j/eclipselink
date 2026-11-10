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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.query.NativeQuery;

@Entity
@Table(name = "JPA_METHOD_NATIVE_PROPERTY")
@Access(AccessType.PROPERTY)
public class MethodNativeProperty {
    private Integer id;
    private String title;

    public MethodNativeProperty() {
    }

    public MethodNativeProperty(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    @Id
    @Column(name = "ID")
    @NativeQuery("SELECT ID FROM JPA_METHOD_NATIVE_PROPERTY WHERE ID = 10")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NativeQuery("SELECT TITLE FROM JPA_METHOD_NATIVE_BOOK WHERE ID = ?1")
    public String lookupTitle(Integer id) {
        throw new AssertionError("Query declarations must not be invoked");
    }
}
