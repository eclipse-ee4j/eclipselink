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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class PostC {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "COMPLEXID_C_A", referencedColumnName = "id", nullable = false)
    private List<CommentC> comments;

    public PostC() { }

    public PostC(Long id) {
        this.id = id;
    }

    public List<CommentC> getComments() {
        return comments;
    }

    public void setComments(List<CommentC> comments) {
        this.comments = comments;
    }
}
