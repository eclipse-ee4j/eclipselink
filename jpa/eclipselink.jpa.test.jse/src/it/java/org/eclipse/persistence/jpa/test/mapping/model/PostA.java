/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

// Contributors:
//     09/12/2018 - Will Dazey
//       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "POST_A")
public class PostA {
    @EmbeddedId
    private ComplexIdA complexId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "POSTA_FK", referencedColumnName = "idA", nullable = false)
    private List<CommentA> comments;

    public PostA() { }

    public PostA(ComplexIdA complexId) {
        this.complexId = complexId;
    }

    public ComplexIdA getComplexId() {
        return complexId;
    }

    public void setComplexId(ComplexIdA complexId) {
        this.complexId = complexId;
    }

    public List<CommentA> getComments() {
        return comments;
    }

    public void setComments(List<CommentA> comments) {
        this.comments = comments;
    }
}
