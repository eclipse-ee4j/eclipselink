/*
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="CMP3_AUTHOR")
public class Author {

    @EmbeddedId
    private AuthorId id;
    @Column(name="NAME")
    private String name;

    public Author() {
        this.id = new AuthorId();
    }

    public Author(String name) {
        this.id = new AuthorId();
        this.name = name;
    }

    public AuthorId getId() {
        return id;
    }

    public String toString() {
        return "Author [id=" + this.id + ", name=" + this.name + "]";
    }
}
