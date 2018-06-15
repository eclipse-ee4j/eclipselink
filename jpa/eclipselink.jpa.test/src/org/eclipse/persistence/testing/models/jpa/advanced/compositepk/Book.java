/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_BOOK")
public class Book {

    @EmbeddedId
    private BookId id;
    @Column(name="TITLE")
    private String title;

    public Book() {
        this.id = new BookId();
        this.title = "Default Title";
    }

    public Book(String title) {
        this.id = new BookId();
        this.title = title;
    }

    public BookId getId() {
        return id;
    }

    public String toString() {
        return "Book [id=" + this.id + ", title=" + this.title + "]";
    }
}
