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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class CommentD {

    @EmbeddedId
    private ComplexIdD id;

    @Column(nullable=false)
    private String content;

    public CommentD() {
    }

    public CommentD(ComplexIdD id, String content) {
        this.id = id;
        this.content = content;
    }

    public void setContent(String s) {
        content = s;
    }

    public String getContent() {
        return content;
    }
} 
