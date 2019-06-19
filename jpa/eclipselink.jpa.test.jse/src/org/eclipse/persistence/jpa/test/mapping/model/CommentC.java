/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     06/19/2019 - Will Dazey
//       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
package org.eclipse.persistence.jpa.test.mapping.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class CommentC {

    @EmbeddedId
    private ComplexIdC id;

    @Column(nullable=false)
    private String content;

    public CommentC() { }

    public CommentC(ComplexIdC id, String content) {
        this.id = id;
        this.content = content;
    }

    public ComplexIdC getId() {
        return id;
    }

    public void setId(ComplexIdC id) {
        this.id = id;
    }

    public void setContent(String s) {
        content = s;
    }

    public String getContent() {
        return content;
    }
}