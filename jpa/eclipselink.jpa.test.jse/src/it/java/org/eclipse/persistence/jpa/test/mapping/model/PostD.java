/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     06/19/2019 - Will Dazey
//       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToMany;

@Entity
public class PostD {

    @EmbeddedId
    private ComplexIdD id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({ 
        @JoinColumn(name = "COMPLEXID_D_A", referencedColumnName = "COMPLEXID_D_A", nullable = false), 
        @JoinColumn(name = "COMPLEXID_D_B", referencedColumnName = "COMPLEXID_D_B", nullable = false)
    })
    private List<CommentD> comments;

    public PostD() { }

    public PostD(ComplexIdD id) {
        this.id = id;
    }

    public List<CommentD> getComments() {
        return comments;
    }

    public void setComments(List<CommentD> comments) {
        this.comments = comments;
    }
}
