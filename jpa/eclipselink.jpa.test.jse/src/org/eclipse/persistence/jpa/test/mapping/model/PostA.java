/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/12/2018 - Will Dazey
 *       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
 ******************************************************************************/  
package org.eclipse.persistence.jpa.test.mapping.model;

import java.util.List;

import javax.persistence.*;

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
