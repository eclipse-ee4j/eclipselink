/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     09/12/2018 - Will Dazey
//       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;

@Entity
public class PostB {
    @EmbeddedId
    private ComplexIdB complexId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({ 
        @JoinColumn(name = "POSTB_A_FK", referencedColumnName = "idA", nullable = false), 
        @JoinColumn(name = "POSTB_B_FK", referencedColumnName = "idB", nullable = false) 
    })
    private List<CommentB> comments;

    public PostB() { }

    public PostB(ComplexIdB complexId) {
        this.complexId = complexId;
    }

    public List<CommentB> getComments() {
        return comments;
    }

    public void setComments(List<CommentB> comments) {
        this.comments = comments;
    }
}
