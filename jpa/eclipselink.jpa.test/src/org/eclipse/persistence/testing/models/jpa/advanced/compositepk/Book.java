/*******************************************************************************
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/11/2017-2.1 Will Dazey 
 *       - 520387: multiple owning descriptors for an embeddable are not set
 ******************************************************************************/ 
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
