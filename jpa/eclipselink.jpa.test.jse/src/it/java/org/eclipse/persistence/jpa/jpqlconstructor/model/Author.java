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
package org.eclipse.persistence.jpa.jpqlconstructor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.Map;

@Entity(name = "ICRAuthor")
@Table(name = "JPA_ICR_AUTHOR")
public class Author {
    @Id
    public Integer id;
    public String name;
    @OneToMany(mappedBy = "author")
    @MapKey(name = "title")
    public Map<String, Book> books = new HashMap<>();

    public Author() {
    }

    public Author(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
