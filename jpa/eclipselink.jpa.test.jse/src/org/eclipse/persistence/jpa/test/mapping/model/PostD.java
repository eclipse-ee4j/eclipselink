/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/19/2019 - Will Dazey
 *       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;

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
